package com.facturacion.api_core.shared.security;

import java.util.regex.Pattern;

public final class DataSanitizer {

    private static final Pattern SCRIPT_PATTERN = Pattern.compile("(?i)<script.*?>.*?</script.*?>");
    private static final Pattern HTML_TAGS_PATTERN = Pattern.compile("<[^>]*>");
    private static final Pattern CONTROL_CHARS = Pattern.compile("[\\p{Cntrl}&&[^\r\n\t]]");

    private DataSanitizer() {
    }

    public static String sanitizeText(String input) {
        if (input == null) {
            return null;
        }

        String cleaned = CONTROL_CHARS.matcher(input).replaceAll("");
        cleaned = SCRIPT_PATTERN.matcher(cleaned).replaceAll("");
        cleaned = HTML_TAGS_PATTERN.matcher(cleaned).replaceAll("");

        return cleaned.trim()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    public static String normalizeDocument(String document) {
        if (document == null) {
            return null;
        }
        return document.replaceAll("[^a-zA-Z0-9]", "").trim().toUpperCase();
    }
}
