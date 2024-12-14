package org.shopme.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CustomDateTimeFormatter {
    private final DateTimeFormatter formatter;

    public CustomDateTimeFormatter(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    public CustomDateTimeFormatter() {
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");
    }

    public String format(ZonedDateTime dateTime) {
        return formatter.format(dateTime);
    }

    public String format(LocalDateTime dateTime) {
        return formatter.format(dateTime);
    }

    public String format(LocalDate dateTime) {
        return formatter.format(dateTime);
    }
}
