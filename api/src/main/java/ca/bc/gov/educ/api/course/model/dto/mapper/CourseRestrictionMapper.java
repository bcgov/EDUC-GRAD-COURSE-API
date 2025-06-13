package ca.bc.gov.educ.api.course.model.dto.mapper;

import ca.bc.gov.educ.api.course.model.dto.v2.CourseRestriction;
import ca.bc.gov.educ.api.course.model.entity.CourseRestrictionsEntity;
import io.micrometer.common.util.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(uses = {UUIDMapper.class, LocalDateTimeMapper.class, StringMapper.class}, componentModel = "spring")
public interface CourseRestrictionMapper {

    CourseRestrictionMapper mapper = Mappers.getMapper(CourseRestrictionMapper.class);

    CourseRestriction toStructure(CourseRestrictionsEntity entity);

    @Mapping(source = "restrictionStartDate",  target = "restrictionStartDate", qualifiedByName = "localDateTimeToString")
    @Mapping(source = "restrictionEndDate",  target = "restrictionEndDate", qualifiedByName = "localDateTimeToString")
    CourseRestrictionsEntity toEntity(CourseRestriction courseRestriction);

    @Named("localDateTimeToString")
    default LocalDateTime localDateTimeFromString(String dateTime) {
        if(StringUtils.isBlank(dateTime)) {
            return null;
        }
        if(isValidYearMonth(dateTime)) {
            return LocalDateTime.parse(dateTime+"-01T00:00:00");
        }
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    default boolean isValidYearMonth(String input) {
            return input.matches("^\\d{4}-(0[1-9]|1[0-2])$");
    }

}
