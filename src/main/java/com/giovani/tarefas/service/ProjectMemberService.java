package com.giovani.tarefas.service;

import com.giovani.tarefas.dto.ProjectMemberRequest;
import com.giovani.tarefas.dto.ProjectMemberResponse;
import com.giovani.tarefas.model.entity.Project;
import com.giovani.tarefas.model.entity.ProjectMember;
import com.giovani.tarefas.model.entity.User;
import com.giovani.tarefas.repository.ProjectMemberRepository;
import com.giovani.tarefas.repository.ProjectRepository;
import com.giovani.tarefas.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ProjectMemberService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectMemberService(UserRepository userRepository, ProjectRepository projectRepository,  ProjectMemberRepository projectMemberRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    public ProjectMemberResponse addUserToProject(Long projectId, ProjectMemberRequest request){
        // Get username from JWT to verify the owner
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwner().getUsername().equals(loggedUser)) throw new RuntimeException("Only the owner can add new users");

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProjectMember newMember = new ProjectMember();
        newMember.setUser(user);
        newMember.setProject(project);

        ProjectMember saveMember = projectMemberRepository.save(newMember);
        return  ProjectMemberResponse.fromEntity(saveMember);
    }
}
