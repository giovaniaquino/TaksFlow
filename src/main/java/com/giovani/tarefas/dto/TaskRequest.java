package com.giovani.tarefas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskRequest(
        @NotNull Long responsibleId,
        @NotBlank String title,
        String description,
        String prio,
        @NotNull LocalDate dueDate
) {
}
