package com.giovani.tarefas.service;

import com.giovani.tarefas.dto.TaskRequest;
import com.giovani.tarefas.dto.TaskResponse;
import com.giovani.tarefas.exception.BusinessRuleException;
import com.giovani.tarefas.model.entity.Project;
import com.giovani.tarefas.model.entity.Task;
import com.giovani.tarefas.model.entity.User;
import com.giovani.tarefas.model.enums.TaskPrio;
import com.giovani.tarefas.model.enums.TaskStatus;
import com.giovani.tarefas.repository.ProjectMemberRepository;
import com.giovani.tarefas.repository.ProjectRepository;
import com.giovani.tarefas.repository.TaskRepository;
import com.giovani.tarefas.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository,  UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }

    public TaskResponse createTask(TaskRequest request){
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new BusinessRuleException("Project not found"));

        User user = userRepository.findById(request.responsibleId())
                .orElseThrow(() -> new BusinessRuleException("User not found"));

        if (!projectMemberRepository.existsByProjectIdAndUserId(project.getId(), user.getId())){
            throw new BusinessRuleException("User doesn't make part of the project");
        }

        Task newTask = new Task();
        newTask.setProject(project);
        newTask.setResponsibleUser(user);
        newTask.setTitle(request.title());
        newTask.setDescription(request.description());
        if (request.prio() != null){
            newTask.setPrio(TaskPrio.valueOf(request.prio()));
        } else {
            newTask.setPrio(TaskPrio.LOW);
        }
        newTask.setStatus(TaskStatus.TO_DO);
        if (request.dueDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException("Due date cannot be before current date");
        } else {
            newTask.setDueDate(request.dueDate());
        }

        taskRepository.save(newTask);
        return TaskResponse.fromEntity(newTask);
    }

    public TaskResponse taskInProgress(Long taskId){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessRuleException("Task not found"));

        if (!checkResponsible(task)) throw new BusinessRuleException("Only the responsible set in progress");

        if (!task.getStatus().equals(TaskStatus.TO_DO)) {
            throw new BusinessRuleException("Only task to do can be in progress");
        }
        task.setUpdatedAt(LocalDateTime.now());
        task.setStatus(TaskStatus.IN_PROGRESS);

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    public TaskResponse taskDone(Long taskId){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessRuleException("Task not found"));

        if (!checkResponsible(task)) throw new BusinessRuleException("Only the responsible set done");

        if (!task.getStatus().equals(TaskStatus.IN_PROGRESS)) {
            throw new BusinessRuleException("Only task in progress can be done");
        }
        task.setUpdatedAt(LocalDateTime.now());
        task.setStatus(TaskStatus.DONE);
        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    public TaskResponse taskCanceled(Long taskId){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessRuleException("Task not found"));

        if (!checkResponsible(task)) throw new BusinessRuleException("Only the responsible set cancel");

        if (task.getStatus().equals(TaskStatus.DONE)) {
            throw new BusinessRuleException("Done task cannot be canceled");
        }
        task.setUpdatedAt(LocalDateTime.now());
        task.setStatus(TaskStatus.CANCELED);
        return TaskResponse.fromEntity(taskRepository.save(task));
    }


    private boolean checkResponsible(Task task){
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();

        User responsible = userRepository.findByUsername(loggedUser)
                .orElseThrow(() -> new BusinessRuleException("User not found"));

        return responsible.getUsername().equals(task.getResponsibleUser().getUsername());
    }
}
