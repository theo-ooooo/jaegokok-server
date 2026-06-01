package com.jaegokok.core.product;

import com.jaegokok.core.BaseEntity;
import com.jaegokok.core.workspace.WorkspaceEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceEntity workspace;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 100)
    private String sku;

    @Column(length = 1000)
    private String description;

    @Column
    private BigDecimal price;

    @Column(length = 50)
    private String unit;

    @Column(length = 100)
    private String category;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int minStockLevel;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int currentStock;

    @Column(name = "qr_code", nullable = false, unique = true, length = 36)
    private String qrCode;

    @Builder
    private ProductEntity(WorkspaceEntity workspace, String name, String sku, String description,
                          BigDecimal price, String unit, String category, int minStockLevel, int currentStock, String qrCode) {
        this.workspace = workspace;
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.price = price;
        this.unit = unit;
        this.category = category;
        this.minStockLevel = minStockLevel;
        this.currentStock = currentStock;
        this.qrCode = qrCode;
    }

    public static ProductEntity from(WorkspaceEntity workspace, String name, String sku, String description,
                                     BigDecimal price, String unit, String category, int minStockLevel, int currentStock, String qrCode) {
        return ProductEntity.builder()
                .workspace(workspace)
                .name(name)
                .sku(sku)
                .description(description)
                .price(price)
                .unit(unit)
                .category(category)
                .minStockLevel(minStockLevel)
                .currentStock(currentStock)
                .qrCode(qrCode)
                .build();
    }

    public void adjustStock(int delta) {
        this.currentStock += delta;
    }

    public void update(String name, String sku, String description, BigDecimal price,
                       String unit, String category, Integer minStockLevel) {
        if (name != null) this.name = name;
        if (sku != null) this.sku = sku;
        if (description != null) this.description = description;
        if (price != null) this.price = price;
        if (unit != null) this.unit = unit;
        if (category != null) this.category = category;
        if (minStockLevel != null) this.minStockLevel = minStockLevel;
    }

}
