package com.jaegokok.domain.inventory;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.domain.inventory.dto.InventoryHistoryCondition;
import com.jaegokok.domain.inventory.dto.InventoryHistoryResponse;
import com.jaegokok.domain.workspace.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRecordRepository inventoryRecordRepository;
    private final WorkspaceRepository workspaceRepository;

    public Page<InventoryHistoryResponse> getHistory(Long memberId, InventoryHistoryCondition condition, Pageable pageable) {
        Long workspaceId = workspaceRepository.findAllByMemberId(memberId)
                .stream()
                .findFirst()
                .map(w -> w.id())
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_NOT_FOUND));

        return inventoryRecordRepository.findByCondition(workspaceId, condition, pageable)
                .map(InventoryHistoryResponse::from);
    }
}
