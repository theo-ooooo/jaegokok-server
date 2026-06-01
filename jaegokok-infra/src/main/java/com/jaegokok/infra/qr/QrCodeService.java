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
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QrCodeService implements QrCodePort {

    private final QrProperties qrProperties;

    @Override
    public byte[] generateQrPng(String qrCode) {
        String url = qrProperties.baseUrl() + "/" + qrCode;
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, 300, 300);
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
            for (QrItem item : items) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                byte[] qrImageBytes = generateQrPng(item.qrCode());
                PDImageXObject qrImage = PDImageXObject.createFromByteArray(document, qrImageBytes, "qr");

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, 750);
                    contentStream.showText(item.productName());
                    contentStream.endText();

                    contentStream.drawImage(qrImage, 50, 400, 300, 300);
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("QR PDF 생성 실패", e);
        }
    }
}
