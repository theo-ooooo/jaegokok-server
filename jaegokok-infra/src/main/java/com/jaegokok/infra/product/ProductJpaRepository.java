package com.jaegokok.infra.product;

import com.jaegokok.core.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
    long countByWorkspace_Id(Long workspaceId);
}
