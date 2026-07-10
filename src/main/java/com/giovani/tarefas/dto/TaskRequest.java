package com.giovani.tarefas.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank Long projectId,
        @NotBlank Long responsibleId,
        @NotBlank String title,
        String description,
        String prio,
        @NotBlank LocalDate dueDate
) {
}
