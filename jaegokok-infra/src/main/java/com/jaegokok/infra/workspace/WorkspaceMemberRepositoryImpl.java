package com.jaegokok.infra.workspace;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.core.member.MemberEntity;
import com.jaegokok.core.workspace.WorkspaceEntity;
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
        WorkspaceEntity workspace = workspaceJpaRepository.findById(workspaceId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));
        MemberEntity member = memberJpaRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        WorkspaceMemberEntity entity = WorkspaceMemberEntity.from(workspace, member, role);
        return toWorkspaceMember(workspaceMemberJpaRepository.save(entity));
    }

    @Override
    public List<WorkspaceMember> findByWorkspaceId(Long workspaceId) {
        return workspaceMemberQueryRepository.findByWorkspaceId(workspaceId)
                .stream().map(this::toWorkspaceMember).toList();
    }

    @Override
    public Optional<WorkspaceMember> findByWorkspaceIdAndMemberId(Long workspaceId, Long memberId) {
        return workspaceMemberQueryRepository.findByWorkspaceIdAndMemberId(workspaceId, memberId)
                .map(this::toWorkspaceMember);
    }

    @Override
    public void deleteById(Long id) {
        workspaceMemberJpaRepository.deleteById(id);
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
