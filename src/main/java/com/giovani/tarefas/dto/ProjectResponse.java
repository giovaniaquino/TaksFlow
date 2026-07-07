package com.giovani.tarefas.dto;

import com.giovani.tarefas.model.entity.Project;

public record ProjectResponse (
        Long id,
        String name,
        String description
) {
    public static ProjectResponse fromEntity(Project project) {
        return new ProjectResponse(
            project.getId(),
            project.getName(),
            project.getDescription()
        );
    }
}
