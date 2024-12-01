package org.pom.utils;

import java.util.Locale;

public class FormatUtil {
    public static String getString(Locale locale, String value, String cellFormat, String header) {
        var columnWidth = Integer.parseInt(cellFormat
                        .replace("%","")
                        .split("\\.")[0]);
        return String.format(locale, "%" + (Math.max(header.length(), columnWidth)) + "s", value);
    }
}
