package ca.bc.gov.educ.api.course.repository;

import ca.bc.gov.educ.api.course.model.entity.CourseRequirementEntity;
import ca.bc.gov.educ.api.course.model.entity.CourseRestrictionsEntity;
import ca.bc.gov.educ.api.course.model.entity.ExaminableCourseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExaminableCourseRepository extends JpaRepository<ExaminableCourseEntity, UUID> {
    List<ExaminableCourseEntity> findAll();
}
