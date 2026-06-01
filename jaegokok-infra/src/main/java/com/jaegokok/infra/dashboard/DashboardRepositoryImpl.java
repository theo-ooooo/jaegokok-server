package com.jaegokok.infra.dashboard;

import com.jaegokok.core.inventory.InventoryType;
import com.jaegokok.core.inventory.QInventoryRecordEntity;
import com.jaegokok.core.product.QProductEntity;
import com.jaegokok.domain.dashboard.DashboardRepository;
import com.jaegokok.domain.dashboard.dto.LowStockProduct;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public long countLowStockProducts(Long workspaceId) {
        QProductEntity product = QProductEntity.productEntity;
        return Optional.ofNullable(
                queryFactory.select(product.count())
                        .from(product)
                        .where(
                                product.workspace.id.eq(workspaceId),
                                product.minStockLevel.gt(0),
                                product.currentStock.loe(product.minStockLevel)
                        )
                        .fetchOne()
        ).orElse(0L);
    }

    @Override
    public List<LowStockProduct> findLowStockProducts(Long workspaceId, int limit) {
        QProductEntity product = QProductEntity.productEntity;
        return queryFactory.selectFrom(product)
                .where(
                        product.workspace.id.eq(workspaceId),
                        product.minStockLevel.gt(0),
                        product.currentStock.loe(product.minStockLevel)
                )
                .orderBy(product.currentStock.asc())
                .limit(limit)
                .fetch()
                .stream()
                .map(p -> new LowStockProduct(p.getId(), p.getName(), p.getCurrentStock(), p.getMinStockLevel(), p.getUnit()))
                .toList();
    }

    @Override
    public Map<InventoryType, Long> countTodayRecordsByType(Long workspaceId, LocalDate today) {
        QInventoryRecordEntity ir = QInventoryRecordEntity.inventoryRecordEntity;
        QProductEntity product = QProductEntity.productEntity;
        List<Tuple> rows = queryFactory
                .select(ir.type, ir.count())
                .from(ir)
                .join(ir.product, product)
                .where(
                        product.workspace.id.eq(workspaceId),
                        ir.createdAt.goe(today.atStartOfDay()),
                        ir.createdAt.lt(today.plusDays(1).atStartOfDay())
                )
                .groupBy(ir.type)
                .fetch();

        return rows.stream()
                .collect(Collectors.toMap(
                        t -> t.get(ir.type),
                        t -> Optional.ofNullable(t.get(ir.count())).orElse(0L)
                ));
    }
}
