package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.WorkspaceMemberEntity;
import com.jaegokok.core.workspace.WorkspaceMemberRole;
import com.jaegokok.domain.workspace.WorkspaceMember;
import com.jaegokok.domain.workspace.WorkspaceMemberRepository;
import com.jaegokok.infra.member.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkspaceMemberRepositoryImpl implements WorkspaceMemberRepository {

    private final WorkspaceMemberJpaRepository workspaceMemberJpaRepository;
    private final WorkspaceMemberQueryRepository workspaceMemberQueryRepository;
    private final WorkspaceJpaRepository workspaceJpaRepository;
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public WorkspaceMember save(Long workspaceId, Long memberId, WorkspaceMemberRole role) {
        WorkspaceMemberEntity entity = WorkspaceMemberEntity.from(
                workspaceJpaRepository.getReferenceById(workspaceId),
                memberJpaRepository.getReferenceById(memberId),
                role
        );
        return toWorkspaceMember(workspaceMemberJpaRepository.save(entity));
    }

    @Override
    public List<WorkspaceMember> findByWorkspaceId(Long workspaceId) {
        return workspaceMemberQueryRepository.findByWorkspaceId(workspaceId)
                .stream().map(this::toWorkspaceMember).toList();
    }

    @Override
    public Optional<WorkspaceMember> findById(Long id) {
        return workspaceMemberJpaRepository.findById(id)
                .map(this::toWorkspaceMember);
    }

    @Override
    public Optional<WorkspaceMember> findByMemberId(Long memberId) {
        return workspaceMemberJpaRepository.findFirstByMember_Id(memberId)
                .map(this::toWorkspaceMember);
    }

    @Override
    public Optional<WorkspaceMember> findByWorkspaceIdAndMemberId(Long workspaceId, Long memberId) {
        return workspaceMemberQueryRepository.findByWorkspaceIdAndMemberId(workspaceId, memberId)
                .map(this::toWorkspaceMember);
    }

    @Override
    public WorkspaceMember updateRole(Long id, WorkspaceMemberRole role) {
        WorkspaceMemberEntity entity = workspaceMemberJpaRepository.findById(id)
                .orElseThrow(() -> new com.jaegokok.common.exception.CustomException(com.jaegokok.common.ErrorCode.MEMBER_NOT_FOUND));
        entity.updateRole(role);
        return toWorkspaceMember(entity);
    }

    @Override
    public void deleteById(Long id) {
        workspaceMemberJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByWorkspaceIdAndMemberId(Long workspaceId, Long memberId) {
        return workspaceMemberJpaRepository.existsByWorkspace_IdAndMember_Id(workspaceId, memberId);
    }

    @Override
    public boolean existsByWorkspaceIdAndEmail(Long workspaceId, String email) {
        return workspaceMemberQueryRepository.existsByWorkspaceIdAndEmail(workspaceId, email);
    }

    private WorkspaceMember toWorkspaceMember(WorkspaceMemberEntity entity) {
        return new WorkspaceMember(
                entity.getId(),
                entity.getWorkspace().getId(),
                entity.getMember().getId(),
                entity.getRole()
        );
    }
}
