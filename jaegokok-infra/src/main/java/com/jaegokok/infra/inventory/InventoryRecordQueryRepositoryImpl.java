package com.jaegokok.infra.inventory;

import com.jaegokok.core.inventory.InventoryRecordEntity;
import com.jaegokok.core.inventory.QInventoryRecordEntity;
import com.jaegokok.core.member.QMemberEntity;
import com.jaegokok.core.product.QProductEntity;
import com.jaegokok.core.workspace.QWorkspaceEntity;
import com.jaegokok.domain.inventory.dto.InventoryHistoryCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InventoryRecordQueryRepositoryImpl implements InventoryRecordQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<InventoryRecordEntity> findRecentByProductId(Long productId, int limit) {
        QInventoryRecordEntity ir = QInventoryRecordEntity.inventoryRecordEntity;
        QProductEntity product = QProductEntity.productEntity;
        QMemberEntity createdBy = QMemberEntity.memberEntity;
        return queryFactory.selectFrom(ir)
                .join(ir.product, product).fetchJoin()
                .join(ir.createdBy, createdBy).fetchJoin()
                .where(ir.product.id.eq(productId))
                .orderBy(ir.createdAt.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Page<InventoryRecordEntity> findByCondition(Long workspaceId, InventoryHistoryCondition condition, Pageable pageable) {
        QInventoryRecordEntity ir = QInventoryRecordEntity.inventoryRecordEntity;
        QProductEntity product = QProductEntity.productEntity;
        QWorkspaceEntity workspace = QWorkspaceEntity.workspaceEntity;
        QMemberEntity createdBy = QMemberEntity.memberEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(ir.product.workspace.id.eq(workspaceId));
        if (condition.productId() != null) builder.and(ir.product.id.eq(condition.productId()));
        if (condition.type() != null) builder.and(ir.type.eq(condition.type()));
        if (condition.dateFrom() != null) builder.and(ir.createdAt.goe(condition.dateFrom().atStartOfDay()));
        if (condition.dateTo() != null) builder.and(ir.createdAt.loe(condition.dateTo().atTime(23, 59, 59)));

        List<InventoryRecordEntity> content = queryFactory.selectFrom(ir)
                .join(ir.product, product).fetchJoin()
                .join(ir.product.workspace, workspace).fetchJoin()
                .join(ir.createdBy, createdBy).fetchJoin()
                .where(builder)
                .orderBy(ir.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable,
                () -> Optional.ofNullable(queryFactory.select(ir.count()).from(ir).where(builder).fetchOne()).orElse(0L));
    }
}
