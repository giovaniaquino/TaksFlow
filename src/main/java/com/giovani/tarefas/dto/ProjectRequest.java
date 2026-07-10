package com.giovani.tarefas.dto;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequest(
        @NotBlank String name,
        String description
) {
}
