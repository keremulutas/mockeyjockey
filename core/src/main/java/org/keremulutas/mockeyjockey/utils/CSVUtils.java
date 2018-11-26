package org.keremulutas.mockeyjockey.utils;

import java.util.Collection;

public class CSVUtils {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    public static String getLine(Collection<Object> values) {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (Object value : values) {
            if (!first) {
                sb.append(DEFAULT_SEPARATOR);
            }
            if(value == null) continue;
            if (value instanceof Number) {
                sb.append(value);
            } else {
                sb.append(DEFAULT_QUOTE).append(value).append(DEFAULT_QUOTE);
            }
            first = false;
        }
        sb.append("\n");
        return sb.toString();
    }

}
