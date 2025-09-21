package org.shopme.common.util;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DecimalFormat;

public class NumberFormatter {
    public static final String FORMATTER_NAME = "numberFormater";
    private final DecimalFormat format;

    public NumberFormatter(String thousandPointType, String decimalPointType, int decimalDigits) {
        String pattern = "#" + thousandPointType + "###" + decimalPointType + "0".repeat(decimalDigits);
        this.format = new DecimalFormat(pattern);
    }

    public String format(double value) {
        return format.format(value);
    }

    public static NumberFormatter getFormatter(HttpServletRequest request) {
        return (NumberFormatter) request.getAttribute(FORMATTER_NAME);
    }
}
