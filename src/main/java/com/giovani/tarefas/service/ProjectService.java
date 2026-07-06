package com.giovani.tarefas.service;

import com.giovani.tarefas.dto.ProjectRequest;
import com.giovani.tarefas.dto.ProjectResponse;
import com.giovani.tarefas.model.entity.Project;
import com.giovani.tarefas.model.entity.User;
import com.giovani.tarefas.model.enums.ProjectStatus;
import com.giovani.tarefas.repository.ProjectRepository;
import com.giovani.tarefas.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository,  UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public ProjectResponse createProject(ProjectRequest request) {
        User owner = userRepository.findByUsername(request.owner())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setOwner(owner);
        project.setStatus(ProjectStatus.ACTIVE);

        Project saveProject = projectRepository.save(project);
        return ProjectResponse.fromEntity(saveProject);
    }

    public Page<ProjectResponse> findProjectByOwner (String owner, Pageable pageable) {
        User user = userRepository.findByUsername(owner)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return projectRepository.findAllByOwnerId(user.getId(), pageable).map(ProjectResponse::fromEntity);
    }
}
