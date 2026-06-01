package com.jaegokok.infra.inventory;

import com.jaegokok.core.inventory.InventoryRecordEntity;
import com.jaegokok.core.inventory.InventoryType;
import com.jaegokok.domain.inventory.InventoryRecord;
import com.jaegokok.domain.inventory.InventoryRecordRepository;
import com.jaegokok.domain.inventory.dto.InventoryHistoryCondition;
import com.jaegokok.infra.member.MemberJpaRepository;
import com.jaegokok.infra.product.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InventoryRecordRepositoryImpl implements InventoryRecordRepository {

    private final InventoryRecordJpaRepository jpaRepository;
    private final InventoryRecordQueryRepository queryRepository;
    private final ProductJpaRepository productJpaRepository;
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public InventoryRecord save(Long productId, InventoryType type, int quantity, String note, Long createdById) {
        InventoryRecordEntity entity = InventoryRecordEntity.of(
                productJpaRepository.getReferenceById(productId),
                type,
                quantity,
                note,
                memberJpaRepository.getReferenceById(createdById)
        );
        return toRecord(jpaRepository.save(entity));
    }

    @Override
    public List<InventoryRecord> findRecentByProductId(Long productId, int limit) {
        return queryRepository.findRecentByProductId(productId, limit)
                .stream().map(this::toRecord).toList();
    }

    @Override
    public Page<InventoryRecord> findByCondition(Long workspaceId, InventoryHistoryCondition condition, Pageable pageable) {
        return queryRepository.findByCondition(workspaceId, condition, pageable)
                .map(this::toRecord);
    }

    private InventoryRecord toRecord(InventoryRecordEntity e) {
        return new InventoryRecord(
                e.getId(),
                e.getProduct().getId(),
                e.getProduct().getName(),
                e.getProduct().getWorkspace().getId(),
                e.getType(),
                e.getQuantity(),
                e.getNote(),
                e.getCreatedBy().getId(),
                e.getCreatedBy().getNickname(),
                e.getCreatedAt()
        );
    }
}
