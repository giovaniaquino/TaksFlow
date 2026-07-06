package com.giovani.tarefas.dto;

public record ProjectRequest(
        String name,
        String description,
        String owner
) {
}
