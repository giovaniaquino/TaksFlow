package com.giovani.tarefas.controller;

import com.giovani.tarefas.dto.ProjectMemberResponse;
import com.giovani.tarefas.dto.ProjectRequest;
import com.giovani.tarefas.dto.ProjectResponse;
import com.giovani.tarefas.service.ProjectMemberService;
import com.giovani.tarefas.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;

    public ProjectController(ProjectService projectService,  ProjectMemberService projectMemberService) {
        this.projectService = projectService;
        this.projectMemberService = projectMemberService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(@Valid @RequestBody ProjectRequest projectRequest) {
        return projectService.createProject(projectRequest);
    }

    @GetMapping
    public Page<ProjectResponse> findUserProjects(Pageable pageable) {
        return projectService.findUserProjects(pageable);
    }

    @GetMapping("/{projectId}")
    public ProjectResponse findProjectById(@PathVariable Long projectId) {
        return projectService.findProjectById(projectId);
    }

    @PutMapping("/{projectId}")
    public ProjectResponse updateProject(@PathVariable Long projectId, @RequestBody ProjectRequest projectRequest) {
        return projectService.updateProject(projectId, projectRequest);
    }

    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
    }

    @PostMapping("{projectId}/member/{userId}")
    public ProjectMemberResponse addUserToProject(@PathVariable Long projectId, @PathVariable Long userId) {
        return projectMemberService.addUserToProject(projectId, userId);
    }

    @DeleteMapping("{projectId}/member/{userId}")
    public void deleteUserFromProject(@PathVariable Long projectId, @PathVariable Long userId) {
        projectMemberService.deleteUserFromProject(projectId, userId);
    }
}
