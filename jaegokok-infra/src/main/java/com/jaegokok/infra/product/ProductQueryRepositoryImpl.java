package com.jaegokok.infra.product;

import com.jaegokok.core.product.ProductEntity;
import com.jaegokok.core.product.QProductEntity;
import com.jaegokok.domain.product.dto.ProductSearchCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

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
                builder.and(product.minStockLevel.gt(0));
            }
        }

        List<ProductEntity> content = queryFactory
                .selectFrom(product)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(product.count())
                .from(product)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
