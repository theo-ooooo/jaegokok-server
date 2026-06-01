package com.jaegokok.infra.product;

import com.jaegokok.core.product.ProductEntity;
import com.jaegokok.domain.product.dto.ProductSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryRepository {
    Page<ProductEntity> findByWorkspaceId(Long workspaceId, ProductSearchCondition condition, Pageable pageable);
}
