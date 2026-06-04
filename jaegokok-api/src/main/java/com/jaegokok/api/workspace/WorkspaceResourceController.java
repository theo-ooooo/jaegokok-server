package com.jaegokok.api.workspace;

import com.jaegokok.api.security.UserPrincipal;
import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.common.response.GlobalResponse;
import com.jaegokok.core.inventory.InventoryType;
import com.jaegokok.core.workspace.WorkspaceMemberRole;
import com.jaegokok.domain.dashboard.DashboardService;
import com.jaegokok.domain.dashboard.dto.DashboardResponse;
import com.jaegokok.domain.inventory.InventoryService;
import com.jaegokok.domain.inventory.dto.InventoryHistoryCondition;
import com.jaegokok.domain.inventory.dto.InventoryHistoryResponse;
import com.jaegokok.domain.product.ProductService;
import com.jaegokok.domain.product.dto.CreateProductRequest;
import com.jaegokok.domain.product.dto.ProductResponse;
import com.jaegokok.domain.product.dto.ProductSearchCondition;
import com.jaegokok.domain.workspace.WorkspaceMemberRepository;
import com.jaegokok.api.util.FileValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}")
@RequiredArgsConstructor
public class WorkspaceResourceController {

    private final ProductService productService;
    private final DashboardService dashboardService;
    private final InventoryService inventoryService;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    private void checkAccess(Long memberId, Long workspaceId) {
        WorkspaceMemberRole role = workspaceMemberRepository.findByWorkspaceIdAndMemberId(workspaceId, memberId)
                .map(wm -> wm.role())
                .orElseThrow(() -> new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED));
        if (role == WorkspaceMemberRole.EMPLOYEE) {
            throw new CustomException(ErrorCode.WORKSPACE_ACCESS_DENIED);
        }
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public GlobalResponse<ProductResponse> createProduct(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workspaceId,
            @Valid @RequestBody CreateProductRequest request
    ) {
        checkAccess(principal.getId(), workspaceId);
        return GlobalResponse.success(HttpStatus.CREATED.value(), productService.createInWorkspace(workspaceId, request));
    }

    @PostMapping(value = "/products/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GlobalResponse<ProductResponse> uploadProductImage(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workspaceId,
            @PathVariable Long productId,
            @RequestPart MultipartFile file
    ) throws IOException {
        checkAccess(principal.getId(), workspaceId);
        FileValidator.validateImage(file);
        return GlobalResponse.success(HttpStatus.OK.value(),
                productService.uploadImageInWorkspace(workspaceId, productId,
                        FileValidator.safeFilename(file), file.getBytes(), file.getContentType()));
    }

    @GetMapping("/products")
    public GlobalResponse<Page<ProductResponse>> getProducts(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workspaceId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean lowStock,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        checkAccess(principal.getId(), workspaceId);
        ProductSearchCondition condition = new ProductSearchCondition(name, category, lowStock);
        return GlobalResponse.success(HttpStatus.OK.value(), productService.findAllInWorkspace(workspaceId, condition, pageable));
    }

    @GetMapping("/dashboard")
    public GlobalResponse<DashboardResponse> getDashboard(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workspaceId
    ) {
        checkAccess(principal.getId(), workspaceId);
        return GlobalResponse.success(HttpStatus.OK.value(), dashboardService.getDashboard(workspaceId));
    }

    @GetMapping("/inventory/history")
    public GlobalResponse<Page<InventoryHistoryResponse>> getInventoryHistory(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workspaceId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) InventoryType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        checkAccess(principal.getId(), workspaceId);
        InventoryHistoryCondition condition = new InventoryHistoryCondition(productId, type, dateFrom, dateTo);
        return GlobalResponse.success(HttpStatus.OK.value(), inventoryService.getHistoryByWorkspace(workspaceId, condition, pageable));
    }

    @GetMapping("/inventory/history/excel")
    public ResponseEntity<byte[]> downloadInventoryExcel(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workspaceId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) InventoryType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo
    ) throws IOException {
        checkAccess(principal.getId(), workspaceId);
        InventoryHistoryCondition condition = new InventoryHistoryCondition(productId, type, dateFrom, dateTo);
        Page<InventoryHistoryResponse> page = inventoryService.getHistoryByWorkspace(workspaceId, condition, PageRequest.of(0, 10000));

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("입출고 내역");

            // Header row
            String[] headers = {"일시", "유형", "상품명", "수량", "담당자", "메모"};
            Row header = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            int rowNum = 1;
            for (InventoryHistoryResponse r : page.getContent()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(r.createdAt().format(fmt));
                row.createCell(1).setCellValue(r.type().name().equals("IN") ? "입고" : "출고");
                row.createCell(2).setCellValue(r.productName());
                row.createCell(3).setCellValue(r.quantity());
                row.createCell(4).setCellValue(r.createdByNickname());
                row.createCell(5).setCellValue(r.note() != null ? r.note() : "");
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            workbook.write(out);
            byte[] bytes = out.toByteArray();

            String filename = URLEncoder.encode("입출고내역.xlsx", StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(bytes);
        }
    }
}
