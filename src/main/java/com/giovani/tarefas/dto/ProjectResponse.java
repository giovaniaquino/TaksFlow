package com.giovani.tarefas.dto;

import com.giovani.tarefas.model.entity.Project;

public record ProjectResponse (
        String name,
        String description
) {
    public static ProjectResponse fromEntity(Project project) {
        return new ProjectResponse(
            project.getName(),
            project.getDescription()
        );
    }
}
