package com.jaegokok.infra.billing;

import com.jaegokok.core.billing.WorkspaceBillingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkspaceBillingJpaRepository extends JpaRepository<WorkspaceBillingEntity, Long> {
    Optional<WorkspaceBillingEntity> findByWorkspaceId(Long workspaceId);

    @Query("SELECT b FROM WorkspaceBillingEntity b WHERE b.status = 'ACTIVE' AND b.nextBillingDate <= :today")
    List<WorkspaceBillingEntity> findAllDueForBilling(@Param("today") LocalDate today);
}
