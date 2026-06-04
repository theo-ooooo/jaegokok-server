package com.jaegokok.domain.product;

import java.util.List;

public interface QrCodePort {
    byte[] generateQrPng(String workspaceSlug, String qrCode);
    byte[] generateBulkQrPdf(List<QrItem> items);
}
