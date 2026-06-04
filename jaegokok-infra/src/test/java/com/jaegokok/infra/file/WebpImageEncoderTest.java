package com.jaegokok.infra.file;

import com.jaegokok.common.exception.CustomException;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WebpImageEncoderTest {

    private final WebpImageEncoder encoder = new WebpImageEncoder();

    @Test
    void toWebp_convertsValidJpegBytes() throws IOException {
        byte[] jpeg = sampleJpeg();
        byte[] webp = encoder.toWebp(jpeg);
        assertThat(webp).isNotEmpty();
        // RIFF....WEBP magic header
        assertThat(new String(webp, 0, 4)).isEqualTo("RIFF");
        assertThat(new String(webp, 8, 4)).isEqualTo("WEBP");
    }

    @Test
    void toWebp_throwsCustomExceptionOnGarbageInput() {
        byte[] garbage = "not an image".getBytes();
        assertThatThrownBy(() -> encoder.toWebp(garbage))
                .isInstanceOf(CustomException.class);
    }

    private byte[] sampleJpeg() throws IOException {
        BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, 8, 8);
        g.dispose();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", out);
        return out.toByteArray();
    }
}
