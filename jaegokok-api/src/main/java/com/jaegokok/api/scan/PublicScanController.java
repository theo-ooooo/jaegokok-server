package com.jaegokok.api.scan;

import com.jaegokok.common.response.GlobalResponse;
import com.jaegokok.domain.inventory.ScanService;
import com.jaegokok.domain.inventory.dto.PublicScanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/scan")
@RequiredArgsConstructor
public class PublicScanController {

    private final ScanService scanService;

    @GetMapping("/{qrCode}")
    public GlobalResponse<PublicScanResponse> getProductByQrCode(@PathVariable String qrCode) {
        return GlobalResponse.success(HttpStatus.OK.value(), scanService.getProductByQrCode(qrCode));
    }
}
