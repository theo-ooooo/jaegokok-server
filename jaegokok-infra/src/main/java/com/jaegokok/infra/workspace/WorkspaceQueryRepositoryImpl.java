package com.jaegokok.infra.workspace;

import com.jaegokok.core.workspace.QWorkspaceMemberEntity;
import com.jaegokok.core.workspace.WorkspaceEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WorkspaceQueryRepositoryImpl implements WorkspaceQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<WorkspaceEntity> findAllByMemberId(Long memberId) {
        QWorkspaceMemberEntity wm = QWorkspaceMemberEntity.workspaceMemberEntity;
        return queryFactory
                .select(wm.workspace)
                .from(wm)
                .where(wm.member.id.eq(memberId))
                .fetch();
    }
}
