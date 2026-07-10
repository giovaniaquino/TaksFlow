package com.giovani.tarefas.controller;

import com.giovani.tarefas.dto.TaskRequest;
import com.giovani.tarefas.dto.TaskResponse;
import com.giovani.tarefas.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@Valid @RequestBody TaskRequest request) {
        return taskService.createTask(request);
    }

    @PatchMapping("/{taskId}/in_progress")
    public TaskResponse taskInProgress(@Valid @PathVariable Long taskId) {
        return taskService.taskInProgress(taskId);
    }

    @PatchMapping("/{taskId}/done")
    public TaskResponse taskDone(@Valid @PathVariable Long taskId) {
        return taskService.taskDone(taskId);
    }

    @PatchMapping("/{taskId}/canceled")
    public TaskResponse taskCanceled(@Valid @PathVariable Long taskId) {
        return taskService.taskCanceled(taskId);
    }
}
