package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.QWorkspaceMemberEntity;
import com.jaegokok.core.workspace.WorkspaceMemberEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkspaceMemberQueryRepositoryImpl implements WorkspaceMemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<WorkspaceMemberEntity> findByWorkspaceId(Long workspaceId) {
        QWorkspaceMemberEntity wm = QWorkspaceMemberEntity.workspaceMemberEntity;
        return queryFactory
                .selectFrom(wm)
                .where(wm.workspace.id.eq(workspaceId))
                .fetch();
    }

    @Override
    public Optional<WorkspaceMemberEntity> findByWorkspaceIdAndMemberId(Long workspaceId, Long memberId) {
        QWorkspaceMemberEntity wm = QWorkspaceMemberEntity.workspaceMemberEntity;
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(wm)
                        .where(
                                wm.workspace.id.eq(workspaceId),
                                wm.member.id.eq(memberId)
                        )
                        .fetchOne()
        );
    }

    @Override
    public boolean existsByWorkspaceIdAndEmail(Long workspaceId, String email) {
        QWorkspaceMemberEntity wm = QWorkspaceMemberEntity.workspaceMemberEntity;
        return queryFactory
                .selectOne()
                .from(wm)
                .where(
                        wm.workspace.id.eq(workspaceId),
                        wm.member.email.eq(email)
                )
                .fetchFirst() != null;
    }
}
