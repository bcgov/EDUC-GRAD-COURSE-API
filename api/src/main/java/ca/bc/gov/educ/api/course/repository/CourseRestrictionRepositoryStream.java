package ca.bc.gov.educ.api.course.repository;

import ca.bc.gov.educ.api.course.model.entity.CourseRestrictionsEntity;
import java.util.stream.Stream;

public interface CourseRestrictionRepositoryStream {
    /**
     * Stream all course restrictions
     * @return Stream of CourseRestrictionsEntity
     */
    Stream<CourseRestrictionsEntity> streamAll();
}
