package com.jaegokok.domain.product;

import com.jaegokok.domain.product.dto.CreateProductRequest;
import com.jaegokok.domain.product.dto.ProductSearchCondition;
import com.jaegokok.domain.product.dto.UpdateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository {
    Product save(Long workspaceId, CreateProductRequest request, String qrCode);
    Optional<Product> findById(Long id);
    Page<Product> findByWorkspaceId(Long workspaceId, ProductSearchCondition condition, Pageable pageable);
    long countByWorkspaceId(Long workspaceId);
    Product update(Long productId, UpdateProductRequest request);
    void deleteById(Long id);
    Optional<Product> findByQrCode(String qrCode);
    void adjustStock(Long productId, int delta);
}
