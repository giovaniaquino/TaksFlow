package com.giovani.tarefas.repository;

import com.giovani.tarefas.model.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    Optional<ProjectMember> findByUserId(Long userId);

    void deleteByProjectId(Long projectId);

    void deleteByProjectIdAndUserId(Long projectId, Long userId);
}
