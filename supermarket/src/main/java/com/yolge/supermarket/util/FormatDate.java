package com.yolge.supermarket.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatDate {

    private static final DateTimeFormatter formatIso = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String formatToIso(LocalDateTime date) {
        return date.toLocalDate().format(formatIso);
    }

    public static LocalDateTime formatFromIso(String date) {
        return LocalDate.parse(date, formatIso).atStartOfDay();
    }
}
