package com.jaegokok.domain.inventory.dto;

public record PublicScanResponse(Long productId, String productName, String qrCode, Long workspaceId) {}
