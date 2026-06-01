package com.jaegokok.infra.dashboard;

import com.jaegokok.core.inventory.InventoryType;
import com.jaegokok.core.inventory.QInventoryRecordEntity;
import com.jaegokok.core.product.QProductEntity;
import com.jaegokok.domain.dashboard.DashboardRepository;
import com.jaegokok.domain.dashboard.dto.LowStockProduct;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DashboardRepositoryImpl implements DashboardRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public long countProducts(Long workspaceId) {
        QProductEntity product = QProductEntity.productEntity;
        return Optional.ofNullable(
                queryFactory.select(product.count())
                        .from(product)
                        .where(product.workspace.id.eq(workspaceId))
                        .fetchOne()
        ).orElse(0L);
    }

    @Override
    public List<LowStockProduct> findLowStockProducts(Long workspaceId) {
        QProductEntity product = QProductEntity.productEntity;
        return queryFactory.selectFrom(product)
                .where(
                        product.workspace.id.eq(workspaceId),
                        product.minStockLevel.gt(0),
                        product.currentStock.loe(product.minStockLevel)
                )
                .orderBy(product.currentStock.asc())
                .fetch()
                .stream()
                .map(p -> new LowStockProduct(p.getId(), p.getName(), p.getCurrentStock(), p.getMinStockLevel(), p.getUnit()))
                .toList();
    }

    @Override
    public long countTodayRecords(Long workspaceId, InventoryType type, LocalDate today) {
        QInventoryRecordEntity ir = QInventoryRecordEntity.inventoryRecordEntity;
        QProductEntity product = QProductEntity.productEntity;
        return Optional.ofNullable(
                queryFactory.select(ir.count())
                        .from(ir)
                        .join(ir.product, product)
                        .where(
                                product.workspace.id.eq(workspaceId),
                                ir.type.eq(type),
                                ir.createdAt.goe(today.atStartOfDay()),
                                ir.createdAt.lt(today.plusDays(1).atStartOfDay())
                        )
                        .fetchOne()
        ).orElse(0L);
    }
}
