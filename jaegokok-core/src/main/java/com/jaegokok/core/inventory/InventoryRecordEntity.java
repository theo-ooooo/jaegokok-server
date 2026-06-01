package com.jaegokok.core.inventory;

import com.jaegokok.core.BaseEntity;
import com.jaegokok.core.member.MemberEntity;
import com.jaegokok.core.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryRecordEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryType type;

    @Column(nullable = false)
    private int quantity;

    @Column(length = 500)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private MemberEntity createdBy;

    @Builder
    private InventoryRecordEntity(ProductEntity product, InventoryType type, int quantity, String note, MemberEntity createdBy) {
        this.product = product;
        this.type = type;
        this.quantity = quantity;
        this.note = note;
        this.createdBy = createdBy;
    }

    public static InventoryRecordEntity of(ProductEntity product, InventoryType type, int quantity, String note, MemberEntity createdBy) {
        return InventoryRecordEntity.builder()
                .product(product).type(type).quantity(quantity).note(note).createdBy(createdBy)
                .build();
    }
}
