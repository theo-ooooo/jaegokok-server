package com.jaegokok.infra.inventory;

import com.jaegokok.core.inventory.InventoryRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRecordJpaRepository extends JpaRepository<InventoryRecordEntity, Long> {
}
