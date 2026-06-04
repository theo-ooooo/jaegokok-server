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
    private final com.jaegokok.domain.workspace.WorkspaceService workspaceService;
    private final com.jaegokok.domain.workspace.WorkspaceRepository workspaceRepository;

    private static void setCellVal(org.apache.poi.ss.usermodel.Row row, int col, String val, org.apache.poi.ss.usermodel.CellStyle style) {
        org.apache.poi.ss.usermodel.Cell c = row.createCell(col);
        c.setCellValue(val);
        c.setCellStyle(style);
    }

    private static void setBorder(org.apache.poi.ss.usermodel.CellStyle s, org.apache.poi.ss.usermodel.BorderStyle bs, org.apache.poi.ss.usermodel.IndexedColors color) {
        s.setBorderTop(bs); s.setBorderBottom(bs); s.setBorderLeft(bs); s.setBorderRight(bs);
        s.setTopBorderColor(color.getIndex()); s.setBottomBorderColor(color.getIndex());
        s.setLeftBorderColor(color.getIndex()); s.setRightBorderColor(color.getIndex());
    }

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

        String workspaceName = workspaceService.getPublicWorkspaceBySlug(
                workspaceRepository.findById(workspaceId)
                        .orElseThrow(() -> new com.jaegokok.common.exception.CustomException(com.jaegokok.common.ErrorCode.WORKSPACE_NOT_FOUND))
                        .slug()
        ).name();

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // ── 색상 정의 ──────────────────────────────────────
            org.apache.poi.ss.usermodel.IndexedColors brand = org.apache.poi.ss.usermodel.IndexedColors.INDIGO;
            // XSSF 전용 색상 (hex)
            byte[] brandRgb = new byte[]{(byte)0x3A, (byte)0x5B, (byte)0xD9};   // #3A5BD9
            byte[] brandLightRgb = new byte[]{(byte)0xEC, (byte)0xEF, (byte)0xFD}; // brand-tint
            byte[] grayRgb = new byte[]{(byte)0xF4, (byte)0xF5, (byte)0xF7};    // bg-alt
            byte[] textRgb = new byte[]{(byte)0x14, (byte)0x16, (byte)0x1C};    // text-primary

            // ── 표지 시트 ─────────────────────────────────────
            org.apache.poi.xssf.usermodel.XSSFSheet cover = workbook.createSheet("표지");
            cover.setColumnWidth(0, 256 * 3);
            cover.setColumnWidth(1, 256 * 40);
            cover.setColumnWidth(2, 256 * 20);

            // 타이틀
            Row titleRow = cover.createRow(3);
            titleRow.setHeightInPoints(48);
            Cell titleCell = titleRow.createCell(1);
            titleCell.setCellValue("입출고 내역");
            org.apache.poi.xssf.usermodel.XSSFCellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short)28);
            titleFont.setColor(new org.apache.poi.xssf.usermodel.XSSFColor(brandRgb, null));
            titleStyle.setFont(titleFont);
            titleStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
            titleCell.setCellStyle(titleStyle);

            // 구분선 흉내 (배경 행)
            Row divRow = cover.createRow(4);
            divRow.setHeightInPoints(6);
            Cell divCell = divRow.createCell(1);
            org.apache.poi.xssf.usermodel.XSSFCellStyle divStyle = workbook.createCellStyle();
            divStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(brandRgb, null));
            divStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            divCell.setCellStyle(divStyle);
            cover.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(4, 4, 1, 2));

            // 메타 정보 행 헬퍼
            String dateRange = (dateFrom != null ? dateFrom.toString() : "전체") + " ~ " + (dateTo != null ? dateTo.toString() : "전체");
            String generatedAt = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
            String[][] meta = {
                {"사업장", workspaceName},
                {"기간", dateRange},
                {"총 건수", page.getTotalElements() + "건"},
                {"출력일", generatedAt},
            };
            int metaStartRow = 6;
            for (String[] m : meta) {
                Row r = cover.createRow(metaStartRow++);
                r.setHeightInPoints(22);
                Cell labelCell = r.createCell(1);
                labelCell.setCellValue(m[0]);
                org.apache.poi.xssf.usermodel.XSSFCellStyle labelStyle = workbook.createCellStyle();
                org.apache.poi.xssf.usermodel.XSSFFont labelFont = workbook.createFont();
                labelFont.setBold(true);
                labelFont.setFontHeightInPoints((short)11);
                labelFont.setColor(new org.apache.poi.xssf.usermodel.XSSFColor(textRgb, null));
                labelStyle.setFont(labelFont);
                labelCell.setCellStyle(labelStyle);

                Cell valCell = r.createCell(2);
                valCell.setCellValue(m[1]);
                org.apache.poi.xssf.usermodel.XSSFCellStyle valStyle = workbook.createCellStyle();
                org.apache.poi.xssf.usermodel.XSSFFont valFont = workbook.createFont();
                valFont.setFontHeightInPoints((short)11);
                valStyle.setFont(valFont);
                valCell.setCellStyle(valStyle);
            }

            // ── 데이터 시트 ─────────────────────────────────────
            org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("입출고 내역");
            sheet.setColumnWidth(0, 256 * 20); // 일시
            sheet.setColumnWidth(1, 256 * 8);  // 유형
            sheet.setColumnWidth(2, 256 * 30); // 상품명
            sheet.setColumnWidth(3, 256 * 8);  // 수량
            sheet.setColumnWidth(4, 256 * 12); // 담당자
            sheet.setColumnWidth(5, 256 * 30); // 메모

            // 헤더 행
            String[] headers = {"일시", "유형", "상품명", "수량", "담당자", "메모"};
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(22);

            org.apache.poi.xssf.usermodel.XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(brandRgb, null));
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
            setBorder(headerStyle, org.apache.poi.ss.usermodel.BorderStyle.THIN, org.apache.poi.ss.usermodel.IndexedColors.WHITE);
            org.apache.poi.xssf.usermodel.XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short)11);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            // 데이터 행
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            int rowNum = 1;
            for (InventoryHistoryResponse r : page.getContent()) {
                Row row = sheet.createRow(rowNum);
                row.setHeightInPoints(20);

                boolean even = (rowNum % 2 == 0);
                org.apache.poi.xssf.usermodel.XSSFCellStyle baseStyle = workbook.createCellStyle();
                if (even) {
                    baseStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(grayRgb, null));
                    baseStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
                }
                baseStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
                setBorder(baseStyle, org.apache.poi.ss.usermodel.BorderStyle.THIN, org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT);

                boolean isIn = "IN".equals(r.type().name());
                org.apache.poi.xssf.usermodel.XSSFCellStyle typeStyle = workbook.createCellStyle();
                typeStyle.cloneStyleFrom(baseStyle);
                typeStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
                org.apache.poi.xssf.usermodel.XSSFFont typeFont = workbook.createFont();
                typeFont.setBold(true);
                typeFont.setColor(new org.apache.poi.xssf.usermodel.XSSFColor(
                        isIn ? new byte[]{(byte)0x1E, (byte)0x9E, (byte)0x6A}  // green
                             : new byte[]{(byte)0xD6, (byte)0x45, (byte)0x3F}, // red
                        null));
                typeStyle.setFont(typeFont);

                org.apache.poi.xssf.usermodel.XSSFCellStyle numStyle = workbook.createCellStyle();
                numStyle.cloneStyleFrom(baseStyle);
                numStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);

                setCellVal(row, 0, r.createdAt().format(fmt), baseStyle);
                setCellVal(row, 1, isIn ? "입고" : "출고", typeStyle);
                setCellVal(row, 2, r.productName(), baseStyle);
                row.createCell(3).setCellValue(r.quantity());
                row.getCell(3).setCellStyle(numStyle);
                setCellVal(row, 4, r.createdByNickname(), baseStyle);
                setCellVal(row, 5, r.note() != null ? r.note() : "", baseStyle);

                rowNum++;
            }

            // 합계 행
            Row sumRow = sheet.createRow(rowNum);
            sumRow.setHeightInPoints(22);
            org.apache.poi.xssf.usermodel.XSSFCellStyle sumStyle = workbook.createCellStyle();
            sumStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(brandLightRgb, null));
            sumStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            sumStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
            setBorder(sumStyle, org.apache.poi.ss.usermodel.BorderStyle.MEDIUM, org.apache.poi.ss.usermodel.IndexedColors.INDIGO);
            org.apache.poi.xssf.usermodel.XSSFFont sumFont = workbook.createFont();
            sumFont.setBold(true);
            sumFont.setFontHeightInPoints((short)11);
            sumStyle.setFont(sumFont);
            setCellVal(sumRow, 0, "합계", sumStyle);
            sumRow.createCell(3).setCellValue(page.getContent().stream().mapToInt(InventoryHistoryResponse::quantity).sum());
            sumRow.getCell(3).setCellStyle(sumStyle);
            for (int i : new int[]{1, 2, 4, 5}) {
                Cell c = sumRow.createCell(i);
                c.setCellStyle(sumStyle);
            }

            workbook.write(out);
            byte[] bytes = out.toByteArray();

            String filename = URLEncoder.encode("입출고내역_" + LocalDate.now() + ".xlsx", StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(bytes);
        }
    }
}
