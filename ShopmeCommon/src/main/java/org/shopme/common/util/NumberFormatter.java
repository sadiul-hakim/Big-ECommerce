package org.shopme.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.shopme.common.entity.Setting;

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

    public static NumberFormatter generate(SettingBag settingBag) {

        String DECIMAL_POINT_TYPE = "";
        String THOUSAND_POINT_TYPE = "";
        int DECIMAL_DIGITS = 1;

        for (Setting setting : settingBag.getSettings()) {
            switch (setting.getKey()) {
                case SettingBag.DECIMAL_POINT_TYPE -> DECIMAL_POINT_TYPE = setting.getValue();
                case SettingBag.THOUSAND_POINT_TYPE -> THOUSAND_POINT_TYPE = setting.getValue();
                case SettingBag.DECIMAL_DIGITS -> DECIMAL_DIGITS = Integer.parseInt(setting.getValue());
            }
        }

        DECIMAL_POINT_TYPE = DECIMAL_POINT_TYPE.equals("COMMA") ? "," : ".";
        THOUSAND_POINT_TYPE = THOUSAND_POINT_TYPE.equals("COMMA") ? "," : ".";

        return new NumberFormatter(THOUSAND_POINT_TYPE, DECIMAL_POINT_TYPE, DECIMAL_DIGITS);
    }
}
