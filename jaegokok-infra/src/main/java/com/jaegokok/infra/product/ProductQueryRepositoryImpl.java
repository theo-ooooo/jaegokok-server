package com.jaegokok.infra.product;

import com.jaegokok.core.product.ProductEntity;
import com.jaegokok.core.product.QProductEntity;
import com.jaegokok.domain.product.dto.ProductSearchCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductEntity> findByWorkspaceId(Long workspaceId, ProductSearchCondition condition, Pageable pageable) {
        QProductEntity product = QProductEntity.productEntity;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(product.workspace.id.eq(workspaceId));

        if (condition != null) {
            if (StringUtils.hasText(condition.name())) {
                builder.and(product.name.containsIgnoreCase(condition.name()));
            }
            if (condition.category() != null) {
                builder.and(product.category.eq(condition.category()));
            }
            if (Boolean.TRUE.equals(condition.lowStock())) {
                builder.and(product.minStockLevel.gt(0))
                       .and(product.currentStock.lt(product.minStockLevel));
            }
        }

        // Pageable Sort → QueryDSL OrderSpecifier
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (Sort.Order o : pageable.getSort()) {
            boolean asc = o.isAscending();
            switch (o.getProperty()) {
                case "name"          -> orders.add(asc ? product.name.asc()          : product.name.desc());
                case "currentStock"  -> orders.add(asc ? product.currentStock.asc()  : product.currentStock.desc());
                case "minStockLevel" -> orders.add(asc ? product.minStockLevel.asc() : product.minStockLevel.desc());
                default              -> orders.add(asc ? product.createdAt.asc()      : product.createdAt.desc());
            }
        }
        if (orders.isEmpty()) {
            orders.add(product.createdAt.desc());
        }

        List<ProductEntity> content = queryFactory
                .selectFrom(product)
                .where(builder)
                .orderBy(orders.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                queryFactory.select(product.count()).from(product).where(builder).fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }
}
