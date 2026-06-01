package com.jaegokok.api.product;

import com.jaegokok.api.security.UserPrincipal;
import com.jaegokok.common.response.GlobalResponse;
import com.jaegokok.domain.inventory.ScanService;
import com.jaegokok.domain.inventory.dto.StockResponse;
import com.jaegokok.domain.product.ProductService;
import com.jaegokok.domain.product.dto.CreateProductRequest;
import com.jaegokok.domain.product.dto.ProductResponse;
import com.jaegokok.domain.product.dto.ProductSearchCondition;
import com.jaegokok.domain.product.dto.UpdateProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ScanService scanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GlobalResponse<ProductResponse> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateProductRequest request
    ) {
        return GlobalResponse.success(HttpStatus.CREATED.value(), productService.create(principal.getId(), request));
    }

    @GetMapping
    public GlobalResponse<Page<ProductResponse>> findAll(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean lowStock,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        ProductSearchCondition condition = new ProductSearchCondition(name, category, lowStock);
        return GlobalResponse.success(HttpStatus.OK.value(), productService.findAll(principal.getId(), condition, pageable));
    }

    @GetMapping("/{id}")
    public GlobalResponse<ProductResponse> findById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        return GlobalResponse.success(HttpStatus.OK.value(), productService.findById(principal.getId(), id));
    }

    @PatchMapping("/{id}")
    public GlobalResponse<ProductResponse> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        return GlobalResponse.success(HttpStatus.OK.value(), productService.update(principal.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public GlobalResponse<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        productService.delete(principal.getId(), id);
        return GlobalResponse.success(HttpStatus.OK.value(), null);
    }

    @GetMapping("/{id}/stock")
    public GlobalResponse<StockResponse> getStock(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        return GlobalResponse.success(HttpStatus.OK.value(), scanService.getStock(id, principal.getId()));
    }

    @GetMapping("/{id}/qr")
    public ResponseEntity<byte[]> downloadQrPng(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        byte[] png = productService.downloadQrPng(principal.getId(), id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"qr-" + id + ".png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }

    @GetMapping("/qr")
    public ResponseEntity<byte[]> downloadBulkQrPdf(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam List<Long> ids
    ) {
        byte[] pdf = productService.downloadBulkQrPdf(principal.getId(), ids);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"qr-bulk.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
