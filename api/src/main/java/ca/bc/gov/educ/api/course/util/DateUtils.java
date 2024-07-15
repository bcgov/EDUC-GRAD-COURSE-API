package ca.bc.gov.educ.api.course.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

    private DateUtils(){}

    public static LocalDate toLocalDate(Date date) {
        if(date == null) return null;
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        if(date == null) return null;
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static Date toDate(LocalDate localDate) {
        if(localDate == null) return null;
        return Date.from(localDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public static Date toDate(LocalDateTime localDateTime) {
        if(localDateTime == null) return null;
        return Date.from(localDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public static java.sql.Date toSqlDate(LocalDate localDate) {
        if(localDate == null) return null;
        return java.sql.Date.valueOf(localDate);
    }

    public static java.sql.Date toSqlDate(LocalDateTime localDateTime) {
        if(localDateTime == null) return null;
        return java.sql.Date.valueOf(localDateTime.toLocalDate());
    }

}
