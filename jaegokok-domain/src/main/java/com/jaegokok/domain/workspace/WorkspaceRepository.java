package com.jaegokok.domain.workspace;

import com.jaegokok.core.workspace.WorkspacePlan;

import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository {
    Workspace save(Long ownerId, String name, String description, WorkspacePlan plan);
    Optional<Workspace> findById(Long id);
    Optional<Workspace> findByOwnerId(Long ownerId);
    List<Workspace> findAllByMemberId(Long memberId);
    boolean existsByOwnerId(Long ownerId);
    Workspace update(Long ownerId, String name, String description);
}
