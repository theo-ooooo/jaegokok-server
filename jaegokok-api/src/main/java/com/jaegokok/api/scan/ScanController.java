package com.jaegokok.api.scan;

import com.jaegokok.api.security.UserPrincipal;
import com.jaegokok.common.response.GlobalResponse;
import com.jaegokok.domain.inventory.ScanService;
import com.jaegokok.domain.inventory.dto.ScanRequest;
import com.jaegokok.domain.inventory.dto.ScanResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/scan")
@RequiredArgsConstructor
public class ScanController {

    private final ScanService scanService;

    @PostMapping("/{qrCode}/in")
    @ResponseStatus(HttpStatus.CREATED)
    public GlobalResponse<ScanResponse> scanIn(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String qrCode,
            @Valid @RequestBody ScanRequest request
    ) {
        return GlobalResponse.success(HttpStatus.CREATED.value(), scanService.scanIn(qrCode, principal.getId(), request));
    }

    @PostMapping("/{qrCode}/out")
    @ResponseStatus(HttpStatus.CREATED)
    public GlobalResponse<ScanResponse> scanOut(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String qrCode,
            @Valid @RequestBody ScanRequest request
    ) {
        return GlobalResponse.success(HttpStatus.CREATED.value(), scanService.scanOut(qrCode, principal.getId(), request));
    }
}
