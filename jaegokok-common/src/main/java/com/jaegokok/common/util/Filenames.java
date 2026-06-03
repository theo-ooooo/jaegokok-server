package com.jaegokok.common.util;

public final class Filenames {

    private Filenames() {}

    public static String stripExtension(String filename) {
        if (filename == null) return "image";
        int idx = filename.lastIndexOf('.');
        return idx > 0 ? filename.substring(0, idx) : filename;
    }
}
