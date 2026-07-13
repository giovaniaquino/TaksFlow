package com.giovani.tarefas.service;

import com.giovani.tarefas.dto.ProjectRequest;
import com.giovani.tarefas.dto.ProjectResponse;
import com.giovani.tarefas.exception.BusinessRuleException;
import com.giovani.tarefas.model.entity.Project;
import com.giovani.tarefas.model.entity.ProjectMember;
import com.giovani.tarefas.model.entity.User;
import com.giovani.tarefas.model.enums.ProjectStatus;
import com.giovani.tarefas.repository.ProjectMemberRepository;
import com.giovani.tarefas.repository.ProjectRepository;
import com.giovani.tarefas.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectService(ProjectRepository projectRepository,  UserRepository userRepository,  ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    public ProjectResponse createProject(ProjectRequest request) {
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();

        User owner = userRepository.findByUsername(loggedUser)
                .orElseThrow(() -> new BusinessRuleException("User not found"));

        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setOwner(owner);
        project.setStatus(ProjectStatus.ACTIVE);

        Project saveProject = projectRepository.save(project);

        ProjectMember newMember = new ProjectMember();
        newMember.setUser(owner);
        newMember.setProject(saveProject);

        ProjectMember saveMember = projectMemberRepository.save(newMember);

        return ProjectResponse.fromEntity(saveProject);
    }

    public Page<ProjectResponse> findUserProjects (Pageable pageable) {
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(loggedUser)
                .orElseThrow(() -> new BusinessRuleException("User not found"));

        ProjectMember projects = projectMemberRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessRuleException("User does not participate in any project"));
        return projectRepository.findAllById(projects.getProject().getId(), pageable).map(ProjectResponse::fromEntity);
    }

    public ProjectResponse findProjectById(Long projectId) {
        Project foundProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessRuleException("Project not found"));

        return ProjectResponse.fromEntity(foundProject);
    }

    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();

        User owner = userRepository.findByUsername(loggedUser)
                .orElseThrow(() -> new BusinessRuleException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessRuleException("Project not found"));

        if (!project.getOwner().getId().equals(owner.getId())) {
            throw new BusinessRuleException("You are not the owner of this project");
        }
        if (project.getStatus() != ProjectStatus.ACTIVE) {
            throw new BusinessRuleException("Status has to be ACTIVE to update");
        }
        if (request.name() != null){
            project.setName(request.name());
        }
        if (request.description() != null){
            project.setDescription(request.description());
        }

        Project updatedProject = projectRepository.save(project);
        return ProjectResponse.fromEntity(updatedProject);
    }

    @Transactional
    public void deleteProject(Long projectId){
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();

        User owner = userRepository.findByUsername(loggedUser)
                .orElseThrow(() -> new BusinessRuleException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessRuleException("Project not found"));

        if (!project.getOwner().getId().equals(owner.getId())) {
            throw new BusinessRuleException("You are not the owner of this project");
        }

        projectMemberRepository.deleteByProjectId(projectId);
        projectRepository.delete(project);
    }
}
