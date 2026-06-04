package com.jaegokok.api.inventory;

import com.jaegokok.api.security.UserPrincipal;
import com.jaegokok.common.response.GlobalResponse;
import com.jaegokok.core.inventory.InventoryType;
import com.jaegokok.domain.inventory.InventoryService;
import com.jaegokok.domain.inventory.dto.InventoryHistoryCondition;
import com.jaegokok.domain.inventory.dto.InventoryHistoryResponse;
import com.jaegokok.domain.inventory.dto.InventoryRecordRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/history")
    public GlobalResponse<Page<InventoryHistoryResponse>> getHistory(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String workspaceSlug,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) InventoryType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        InventoryHistoryCondition condition = new InventoryHistoryCondition(productId, type, dateFrom, dateTo);
        return GlobalResponse.success(HttpStatus.OK.value(), inventoryService.getHistory(principal.getId(), workspaceSlug, condition, pageable));
    }

    @PostMapping("/in")
    @ResponseStatus(HttpStatus.CREATED)
    public GlobalResponse<InventoryHistoryResponse> recordIn(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody InventoryRecordRequest request
    ) {
        return GlobalResponse.success(HttpStatus.CREATED.value(), inventoryService.recordIn(principal.getId(), request));
    }

    @PostMapping("/out")
    @ResponseStatus(HttpStatus.CREATED)
    public GlobalResponse<InventoryHistoryResponse> recordOut(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody InventoryRecordRequest request
    ) {
        return GlobalResponse.success(HttpStatus.CREATED.value(), inventoryService.recordOut(principal.getId(), request));
    }
}
