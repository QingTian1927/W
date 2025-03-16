package com.github.qingtian1927.w.utils;

import java.util.StringJoiner;

public final class ContentFormatter {
    public static final int DEFAULT_PREVIEW_SIZE = 20;
    public static final String CR_LF = "\r\n";
    public static final String LF = "\n";
    public static final String CR = "\r";

    private ContentFormatter() {}

    public static String formatLineBreak(String content) {
        if (content == null) {
            return null;
        }

        String result = content.replaceAll(CR_LF, "<br>");
        result = result.replaceAll(LF, "<br>");
        result = result.replaceAll(CR, "<br>");
        return result;
    }

    public static String getShortenedPreview(String content, int previewSize) {
        String[] words = content.split(" ");
        StringJoiner sj = new StringJoiner(" ");
        int wordCount = 0;

        for (String word : words) {
            if (wordCount <= previewSize) {
                sj.add(word);
                wordCount++;
            }
        }

        return sj.toString().concat("...");
    }

    public static String getShortenedPreview(String content) {
        return getShortenedPreview(content, DEFAULT_PREVIEW_SIZE);
    }
}
