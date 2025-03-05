package com.github.qingtian1927.w.utils;

public final class ContentFormatter {
    public static final String CR_LF = "\r\n";
    public static final String LF = "\n";
    public static final String CR = "\r";

    private ContentFormatter() {}

    public static String formatLineBreak(String content) {
        String result = content.replaceAll(CR_LF, "<br>");
        result = result.replaceAll(LF, "<br>");
        result = result.replaceAll(CR, "<br>");
        return result;
    }
}
