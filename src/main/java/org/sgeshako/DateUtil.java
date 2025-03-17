package org.sgeshako;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public final class DateUtil {

    private DateUtil() {}

    private static final List<String> SUPPORTED_FORMATS = List.of(
            "MMMM d, yyyy",    // October 7, 1970
            "d MMM yy",        // 7 oct 70
            "d MMM yyyy",      // 7 oct 1970
            "dd MMMM yyyy",    // 03 February 2013
            "d MMMM yyyy",     // 1 July 2013
            "yyyy-MMM-dd",     // 2013-Feb-03
            "dd-MM-yyyy",      // 31-12-2020
            "dd/MM/yyyy",      // 31/12/2020
            "dd.MM.yyyy",      // 31.12.2020
            "yyyy/MM/dd",      // 2020/12/31
            "yyyy-MM-dd",      // 2020-12-31
            "yyyy.MM.dd",      // 2020.12.31
            "d-M-yyyy",        // 1-1-2020
            "d/M/yyyy",        // 1/1/2020
            "d.M.yyyy",        // 1.1.2020
            "dd-MMM-yyyy",     // 31-Dec-2020
            "d-MMM-yy",        // 1-Jan-20
            "M/d/yyyy",        // 3/31/2014
            "MM/dd/yyyy",      // 03/31/2014
            "MM/dd/yy",        // 08/21/71
            "M/d/yy"           // 8/1/71
    );

    private static final List<DateTimeFormatter> FORMATTERS = SUPPORTED_FORMATS.stream().map(DateTimeFormatter::ofPattern).toList();

    public static LocalDate parse(String dateString) {
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                // Try next format
            }
        }
        throw new IllegalArgumentException("No supported format found for " + dateString);
    }
}
