package org.shopme.common.util;

import java.text.DecimalFormat;

public class NumberFormatter {
    private final DecimalFormat format;

    public NumberFormatter(String thousandPointType, String decimalPointType, int decimalDigits) {
        String pattern = "#" + thousandPointType + "###" + decimalPointType + "0".repeat(decimalDigits);
        this.format = new DecimalFormat(pattern);
    }

    public String format(double value) {
        return format.format(value);
    }
}
