package com.giovani.tarefas.dto;

import jakarta.validation.constraints.NotBlank;

public record ProjectMemberRequest(
        @NotBlank Long userId
) {
}
