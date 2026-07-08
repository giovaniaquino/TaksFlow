package com.giovani.tarefas.dto;

import com.giovani.tarefas.model.entity.Task;
import com.giovani.tarefas.model.enums.TaskPrio;
import com.giovani.tarefas.model.enums.TaskStatus;

import java.time.LocalDate;

public record TaskResponse(
        Long id,
        String projectTitle,
        String responsible,
        String title,
        String description,
        TaskPrio prio,
        TaskStatus status,
        LocalDate dueDate
) {
    public static TaskResponse fromEntity(Task task){
        return new TaskResponse(
            task.getId(),
            task.getProject().getName(),
            task.getResponsibleUser().getUsername(),
            task.getTitle(),
            task.getDescription(),
            task.getPrio(),
            task.getStatus(),
            task.getDueDate()
        );
    }
}
