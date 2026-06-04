package com.jaegokok.infra.image;

import com.jaegokok.core.image.ImageEntity;
import com.jaegokok.core.image.ImageEntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ImageJpaRepository extends JpaRepository<ImageEntity, Long> {
    List<ImageEntity> findByEntityTypeAndEntityId(ImageEntityType entityType, Long entityId);
    Optional<ImageEntity> findFirstByEntityTypeAndEntityId(ImageEntityType entityType, Long entityId);
    List<ImageEntity> findByEntityTypeAndEntityIdIn(ImageEntityType entityType, List<Long> entityIds);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ImageEntity i WHERE i.entityType = :entityType AND i.entityId = :entityId")
    void deleteByEntityTypeAndEntityId(@Param("entityType") ImageEntityType entityType, @Param("entityId") Long entityId);
}
