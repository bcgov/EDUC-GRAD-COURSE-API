package ca.bc.gov.educ.api.course.model.dto.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public class YearMonthToLocalDateTimeMapper {

    /**
     * Map string.
     *
     * @param dateTime the date time
     * @return the string
     */
    public String map(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateTime);
    }

    /**
     * Map local date time.
     *
     * @param dateValue format YYYY-MM
     * @return the local date time
     */
    public LocalDateTime map(String dateValue) {
        if(StringUtils.isNotBlank(dateValue) && isValidYearMonth(dateValue)) {
            return LocalDateTime.parse(dateValue+"-01T00:00:00");
        }
        return null;
    }

    private boolean isValidYearMonth(String input) {
        return input.matches("^\\d{4}-(0[1-9]|1[0-2])$");
    }

}
