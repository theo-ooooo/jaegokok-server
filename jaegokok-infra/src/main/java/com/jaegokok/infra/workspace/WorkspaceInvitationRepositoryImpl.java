package com.jaegokok.infra.workspace;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.workspace.WorkspaceInvitationEntity;
import com.jaegokok.core.workspace.WorkspaceMemberRole;
import com.jaegokok.domain.workspace.WorkspaceInvitation;
import com.jaegokok.domain.workspace.WorkspaceInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkspaceInvitationRepositoryImpl implements WorkspaceInvitationRepository {

    private final WorkspaceInvitationJpaRepository workspaceInvitationJpaRepository;

    @Override
    public WorkspaceInvitation save(Long workspaceId, String email, WorkspaceMemberRole role) {
        WorkspaceInvitationEntity entity = WorkspaceInvitationEntity.create(workspaceId, email, role);
        return toWorkspaceInvitation(workspaceInvitationJpaRepository.save(entity));
    }

    @Override
    public Optional<WorkspaceInvitation> findByToken(String token) {
        return workspaceInvitationJpaRepository.findByToken(token)
                .map(this::toWorkspaceInvitation);
    }

    @Override
    public void markUsed(Long id) {
        WorkspaceInvitationEntity entity = workspaceInvitationJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.INVITATION_NOT_FOUND));
        entity.markUsed();
        workspaceInvitationJpaRepository.save(entity);
    }

    @Override
    public boolean markUsedByToken(String token) {
        return workspaceInvitationJpaRepository.markUsedByToken(token) > 0;
    }

    private WorkspaceInvitation toWorkspaceInvitation(WorkspaceInvitationEntity e) {
        return new WorkspaceInvitation(
                e.getId(),
                e.getWorkspaceId(),
                e.getEmail(),
                e.getToken(),
                e.getExpiresAt(),
                e.isUsed(),
                e.getRole()
        );
    }
}
