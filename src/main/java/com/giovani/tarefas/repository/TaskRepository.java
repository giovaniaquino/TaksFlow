package com.giovani.tarefas.repository;

import com.giovani.tarefas.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
