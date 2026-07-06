package com.giovani.tarefas.controller;

import com.giovani.tarefas.dto.ProjectRequest;
import com.giovani.tarefas.dto.ProjectResponse;
import com.giovani.tarefas.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(@RequestBody ProjectRequest projectRequest) {
        return projectService.createProject(projectRequest);
    }

    @GetMapping
    public Page<ProjectResponse> findProjectByOwner(@RequestParam String owner, Pageable pageable) {
        return projectService.findProjectByOwner(owner, pageable);
    }
}
