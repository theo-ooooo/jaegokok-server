package com.jaegokok.domain.workspace;

import com.jaegokok.core.workspace.WorkspacePlan;

import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository {
    Workspace save(Long ownerId, String name, String description, WorkspacePlan plan);
    Workspace saveWithSlug(Long ownerId, String name, String description, WorkspacePlan plan, String slug);
    Optional<Workspace> findById(Long id);
    Optional<Workspace> findByOwnerId(Long ownerId);
    Optional<Workspace> findBySlug(String slug);
    List<Workspace> findAllByMemberId(Long memberId);
    boolean existsByOwnerId(Long ownerId);
    boolean existsBySlug(String slug);
    Workspace updateProfile(Long ownerId, String name, String businessNumber, String address, String phone);
    Workspace updatePlan(Long workspaceId, WorkspacePlan plan);
}
