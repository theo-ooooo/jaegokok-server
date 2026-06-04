package com.jaegokok.domain.file;

public interface ImageEncoderPort {
    byte[] toWebp(byte[] originalBytes);
}
