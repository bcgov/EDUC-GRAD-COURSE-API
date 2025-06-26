package ca.bc.gov.educ.api.course.model.dto.mapper;

import ca.bc.gov.educ.api.course.model.dto.v2.CourseRestriction;
import ca.bc.gov.educ.api.course.model.entity.CourseRestrictionsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, YearMonthToLocalDateTimeMapper.class})
public interface CourseRestrictionMapper {

    CourseRestrictionMapper mapper = Mappers.getMapper(CourseRestrictionMapper.class);

    CourseRestriction toStructure(CourseRestrictionsEntity entity);

    @Mapping(source="mainCourse", target = "mainCourse")
    @Mapping(source="mainCourseLevel", target = "mainCourseLevel")
    @Mapping(source="restrictedCourse", target = "restrictedCourse")
    @Mapping(source="restrictedCourseLevel", target = "restrictedCourseLevel")
    CourseRestrictionsEntity toEntity(CourseRestriction courseRestriction);

}
