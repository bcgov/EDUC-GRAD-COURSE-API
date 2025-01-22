package ca.bc.gov.educ.api.course.repository;

import java.util.List;
import java.util.UUID;

import ca.bc.gov.educ.api.course.model.entity.CourseRequirementCodeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.course.model.entity.CourseRequirementEntity;

@Repository
public interface CourseRequirementRepository extends JpaRepository<CourseRequirementEntity, UUID>, JpaSpecificationExecutor<CourseRequirementEntity> {

    List<CourseRequirementEntity> findAll();

	Page<CourseRequirementEntity> findByRuleCode(CourseRequirementCodeEntity ruleCode, Pageable paging);

	List<CourseRequirementEntity> findByCourseCodeAndCourseLevel(String courseCode, String courseLevel);

	List<CourseRequirementEntity> findByCourseCodeIn(List<String> courseCodes);

	@Query(value="select cr from CourseRequirementEntity cr where cr.courseCode = :courseCode and cr.courseLevel = :courseLevel and cr.ruleCode.courseRequirementCode = :ruleCode")
	List<CourseRequirementEntity> findByCourseCodeAndCourseLevelAndRuleCode(
			@Param("courseCode") String courseCode,
			@Param("courseLevel") String courseLevel,
			@Param("ruleCode") String ruleCode);

	@Query(value="select count(cr) from CourseRequirementEntity cr where cr.courseCode = :courseCode and cr.courseLevel = :courseLevel and cr.ruleCode.courseRequirementCode = :ruleCode")
	Long countByCourseCodeAndCourseLevelAndRuleCode(
			@Param("courseCode") String courseCode,
			@Param("courseLevel") String courseLevel,
			@Param("ruleCode") String ruleCode);
}
