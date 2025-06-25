package ca.bc.gov.educ.api.course.model.dto.mapper;

import ca.bc.gov.educ.api.course.model.dto.v2.CourseRestriction;
import ca.bc.gov.educ.api.course.model.entity.CourseRestrictionsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, StringMapper.class, YearMonthToLocalDateTimeMapper.class}, componentModel = "spring")
public interface CourseRestrictionMapper {

    CourseRestrictionMapper mapper = Mappers.getMapper(CourseRestrictionMapper.class);

    CourseRestriction toStructure(CourseRestrictionsEntity entity);

    CourseRestrictionsEntity toEntity(CourseRestriction courseRestriction);

}
