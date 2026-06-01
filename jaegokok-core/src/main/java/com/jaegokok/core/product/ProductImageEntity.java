package com.jaegokok.core.product;

import com.jaegokok.core.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImageEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false, length = 500)
    private String originalPath;

    @Column(length = 500)
    private String webpPath;

    @Column(nullable = false, length = 100)
    private String bucket;

    @Builder
    private ProductImageEntity(ProductEntity product, String originalPath, String webpPath, String bucket) {
        this.product = product;
        this.originalPath = originalPath;
        this.webpPath = webpPath;
        this.bucket = bucket;
    }

    public static ProductImageEntity of(ProductEntity product, String originalPath, String webpPath, String bucket) {
        return ProductImageEntity.builder()
                .product(product)
                .originalPath(originalPath)
                .webpPath(webpPath)
                .bucket(bucket)
                .build();
    }
}
