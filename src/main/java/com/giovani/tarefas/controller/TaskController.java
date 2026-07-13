package com.giovani.tarefas.controller;

import com.giovani.tarefas.dto.TaskRequest;
import com.giovani.tarefas.dto.TaskResponse;
import com.giovani.tarefas.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project/{projectId}/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@PathVariable Long projectId, @Valid @RequestBody TaskRequest request) {
        return taskService.createTask(projectId, request);
    }

    @GetMapping
    public Page<TaskResponse> getTasks(@PathVariable Long projectId, Pageable pageable) {
        return taskService.getTasks(projectId, pageable);
    }

    @PutMapping("/{taskId}")
    public TaskResponse updateTask(@PathVariable Long projectId, @PathVariable Long taskId, @RequestBody TaskRequest request) {
        return taskService.updateTask(projectId, taskId, request);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        taskService.deleteTask(projectId, taskId);
    }

    @PatchMapping("/{taskId}/in_progress")
    public TaskResponse taskInProgress(@PathVariable Long taskId) {
        return taskService.taskInProgress(taskId);
    }

    @PatchMapping("/{taskId}/done")
    public TaskResponse taskDone(@PathVariable Long taskId) {
        return taskService.taskDone(taskId);
    }

    @PatchMapping("/{taskId}/canceled")
    public TaskResponse taskCanceled(@PathVariable Long taskId) {
        return taskService.taskCanceled(taskId);
    }
}
