package com.jaegokok.infra.product;

import com.jaegokok.core.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
    long countByWorkspace_Id(Long workspaceId);
    Optional<ProductEntity> findByQrCode(String qrCode);

    @Modifying
    @Query("UPDATE ProductEntity p SET p.currentStock = p.currentStock + :delta WHERE p.id = :id")
    void adjustStock(@Param("id") Long id, @Param("delta") int delta);

    List<ProductEntity> findAllByIdIn(List<Long> ids);
}
