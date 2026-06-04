package com.jaegokok.infra.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jaegokok.domain.product.QrCodePort;
import com.jaegokok.domain.product.QrItem;
import com.jaegokok.infra.config.QrProperties;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QrCodeService implements QrCodePort {

    private static final int QR_SIZE = 300;

    private final QrProperties qrProperties;

    @Override
    public byte[] generateQrPng(String workspaceSlug, String qrCode) {
        String base = qrProperties.baseUrl().replaceAll("/scan$", "").replaceAll("/+$", "");
        String url = base + "/@" + workspaceSlug + "/scan/" + qrCode;
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
            return baos.toByteArray();
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("QR PNG 생성 실패", e);
        }
    }

    @Override
    public byte[] generateBulkQrPdf(List<QrItem> items) {
        try (PDDocument document = new PDDocument()) {
            PDFont font = resolveFont(document);
            for (QrItem item : items) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                byte[] qrImageBytes = generateQrPng(item.slug(), item.qrCode());
                PDImageXObject qrImage = PDImageXObject.createFromByteArray(document, qrImageBytes, "qr");

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.setFont(font, 14);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, 750);
                    contentStream.showText(sanitize(font, item.productName()));
                    contentStream.endText();

                    contentStream.drawImage(qrImage, 50, 400, QR_SIZE, QR_SIZE);
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("QR PDF 생성 실패", e);
        }
    }

    private PDFont resolveFont(PDDocument document) throws IOException {
        String fontPath = qrProperties.fontPath();
        if (fontPath != null && !fontPath.isBlank()) {
            return PDType0Font.load(document, new File(fontPath));
        }
        return new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    }

    // PDType1Font(Latin-1 only) 사용 시 비ASCII 문자 인코딩 오류 방지
    private String sanitize(PDFont font, String text) {
        if (font instanceof PDType0Font) {
            return text;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(c < 256 ? c : '?');
        }
        return sb.toString();
    }
}
