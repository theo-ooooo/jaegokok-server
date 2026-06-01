package com.jaegokok.domain.product;

import com.jaegokok.domain.product.dto.CreateProductRequest;
import com.jaegokok.domain.product.dto.ProductSearchCondition;
import com.jaegokok.domain.product.dto.UpdateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Long workspaceId, CreateProductRequest request, String qrCode);
    Optional<Product> findById(Long id);
    Optional<Product> findByQrCode(String qrCode);
    Page<Product> findByWorkspaceId(Long workspaceId, ProductSearchCondition condition, Pageable pageable);
    long countByWorkspaceId(Long workspaceId);
    Product update(Long productId, UpdateProductRequest request);
    void deleteById(Long id);
    void adjustStock(Long productId, int delta);
    List<Product> findAllByIds(List<Long> ids);
    Product updateImageUrl(Long productId, String imageUrl);
}
