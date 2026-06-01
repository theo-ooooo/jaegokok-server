package com.jaegokok.infra.product;

import com.jaegokok.core.product.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageJpaRepository extends JpaRepository<ProductImageEntity, Long> {
    List<ProductImageEntity> findByProduct_Id(Long productId);
    void deleteByProduct_Id(Long productId);
}
