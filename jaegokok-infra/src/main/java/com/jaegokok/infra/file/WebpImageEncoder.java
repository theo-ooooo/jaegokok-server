package com.jaegokok.infra.file;

import com.jaegokok.common.ErrorCode;
import com.jaegokok.common.exception.CustomException;
import com.jaegokok.domain.file.ImageEncoderPort;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WebpImageEncoder implements ImageEncoderPort {

    @Override
    public byte[] toWebp(byte[] originalBytes) {
        try {
            ImmutableImage image = ImmutableImage.loader().fromBytes(originalBytes);
            return image.bytes(WebpWriter.DEFAULT);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.IMAGE_CONVERT_FAILED, e);
        }
    }
}
