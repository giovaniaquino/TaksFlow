package com.giovani.tarefas.dto;

import com.giovani.tarefas.model.entity.ProjectMember;

public record ProjectMemberResponse(
        Long id,
        String username,
        String projectName
) {
    public static ProjectMemberResponse fromEntity(ProjectMember projectMember) {
        return new ProjectMemberResponse(
                projectMember.getId(),
                projectMember.getUser().getUsername(),
                projectMember.getProject().getName()
        );
    }
}
