package com.giovani.tarefas.dto;

import java.time.LocalDate;

public record TaskRequest(
        Long projectId,
        Long responsibleId,
        String title,
        String description,
        String prio,
        LocalDate dueDate
) {
}
