package com.jaegokok.infra.inventory;

import com.jaegokok.core.inventory.InventoryRecordEntity;
import com.jaegokok.core.inventory.QInventoryRecordEntity;
import com.jaegokok.core.member.QMemberEntity;
import com.jaegokok.core.product.QProductEntity;
import com.jaegokok.core.workspace.QWorkspaceEntity;
import com.jaegokok.domain.inventory.dto.InventoryHistoryCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
        if (condition.dateTo() != null) builder.and(ir.createdAt.lt(condition.dateTo().plusDays(1).atStartOfDay()));

        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (Sort.Order o : pageable.getSort()) {
            boolean asc = o.isAscending();
            switch (o.getProperty()) {
                case "quantity" -> orders.add(asc ? ir.quantity.asc() : ir.quantity.desc());
                case "productName" -> orders.add(asc ? ir.product.name.asc() : ir.product.name.desc());
                default -> orders.add(asc ? ir.createdAt.asc() : ir.createdAt.desc());
            }
        }
        if (orders.isEmpty()) orders.add(ir.createdAt.desc());

        List<InventoryRecordEntity> content = queryFactory.selectFrom(ir)
                .join(ir.product, product).fetchJoin()
                .join(ir.product.workspace, workspace).fetchJoin()
                .join(ir.createdBy, createdBy).fetchJoin()
                .where(builder)
                .orderBy(orders.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable,
                () -> Optional.ofNullable(queryFactory.select(ir.count()).from(ir).where(builder).fetchOne()).orElse(0L));
    }
}
