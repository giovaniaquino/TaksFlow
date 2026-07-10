package com.giovani.tarefas.exception;

import java.time.LocalDateTime;

public record ExceptionResponse(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String message
) {
}
