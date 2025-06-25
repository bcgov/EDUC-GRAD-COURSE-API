package ca.bc.gov.educ.api.course.service.v2;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.CourseRestrictionRuleData;
import ca.bc.gov.educ.api.course.model.dto.CourseRestrictionValidationIssue;
import ca.bc.gov.educ.api.course.model.dto.ValidationIssue;
import ca.bc.gov.educ.api.course.model.dto.mapper.CourseRestrictionMapper;
import ca.bc.gov.educ.api.course.model.dto.v2.CourseRestriction;
import ca.bc.gov.educ.api.course.model.entity.CourseRestrictionsEntity;
import ca.bc.gov.educ.api.course.repository.CourseRestrictionRepository;
import ca.bc.gov.educ.api.course.validation.rules.CourseRestrictionRulesProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("courseRestrictionServiceV2")
@Slf4j
@AllArgsConstructor
public class CourseRestrictionService {

    private static final CourseRestrictionMapper courseRestrictionMapper = CourseRestrictionMapper.mapper;

    private CourseRestrictionRulesProcessor courseRestrictionRulesProcessor;
    private CourseService courseService;
    private CourseRestrictionRepository courseRestrictionRepository;

    public CourseRestriction getCourseRestriction(String mainCourseCode, String mainCourseLevel, String restrictedCourseCode, String restrictedCourseLevel) {
        Optional<CourseRestrictionsEntity> courseRestrictionsEntity = courseRestrictionRepository.findByMainCourseAndMainCourseLevelAndRestrictedCourseAndRestrictedCourseLevel(
                mainCourseCode, mainCourseLevel, restrictedCourseCode, restrictedCourseLevel);
        if (courseRestrictionsEntity.isPresent()) {
            return courseRestrictionMapper.toStructure(courseRestrictionsEntity.get());
        }
        return null;
    }

    public CourseRestriction getCourseRestrictionById(UUID courseRestrictionId) {
        Optional<CourseRestrictionsEntity> courseRestrictionsEntity = courseRestrictionRepository.findById(courseRestrictionId);
        if (courseRestrictionsEntity.isPresent()) {
            return courseRestrictionMapper.toStructure(courseRestrictionsEntity.get());
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CourseRestrictionValidationIssue updateCourseRestriction(UUID courseRestrictionId, CourseRestriction courseRestriction) {
        CourseRestriction existingCourseRestriction = getCourseRestrictionById(courseRestrictionId);
        CourseRestrictionRuleData courseRestrictionRuleData = prepareCourseRestrictionRuleData(courseRestriction, true);
        courseRestrictionRuleData.setExistingCourseRestriction(existingCourseRestriction);
        List<ValidationIssue> validationIssues = courseRestrictionRulesProcessor.processRules(courseRestrictionRuleData);
        CourseRestrictionValidationIssue courseRestrictionValidationIssue = createCourseValidationIssue(courseRestriction, validationIssues);

        boolean hasError = validationIssues.stream().anyMatch(issue -> "ERROR".equals(issue.getValidationIssueSeverityCode()));
        if (hasError) {
            courseRestrictionValidationIssue.setHasPersisted(false);
        } else {
            courseRestriction.setCourseRestrictionId(existingCourseRestriction.getCourseRestrictionId());
            courseRestriction.setCreateDate(existingCourseRestriction.getCreateDate());
            courseRestriction.setCreateUser(existingCourseRestriction.getCreateUser());
            persistCourseRestriction(courseRestriction, courseRestrictionValidationIssue);
        }
        return courseRestrictionValidationIssue;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CourseRestrictionValidationIssue saveCourseRestriction(CourseRestriction courseRestriction) {
        CourseRestriction existingCourseRestriction = this.getCourseRestriction(courseRestriction.getMainCourse(), courseRestriction.getMainCourseLevel(),
                courseRestriction.getRestrictedCourse(), courseRestriction.getRestrictedCourseLevel());
        CourseRestrictionRuleData courseRestrictionRuleData = prepareCourseRestrictionRuleData(courseRestriction, false);
        courseRestrictionRuleData.setExistingCourseRestriction(existingCourseRestriction);

        List<ValidationIssue> validationIssues = courseRestrictionRulesProcessor.processRules(courseRestrictionRuleData);
        CourseRestrictionValidationIssue courseRestrictionValidationIssue = createCourseValidationIssue(courseRestriction, validationIssues);

        boolean hasError = validationIssues.stream().anyMatch(issue -> "ERROR".equals(issue.getValidationIssueSeverityCode()));
        if (hasError) {
            courseRestrictionValidationIssue.setHasPersisted(false);
        } else {
            persistCourseRestriction(courseRestriction, courseRestrictionValidationIssue);
        }
        return courseRestrictionValidationIssue;
    }

    private void persistCourseRestriction(CourseRestriction courseRestriction, CourseRestrictionValidationIssue courseRestrictionValidationIssue) {
        CourseRestrictionsEntity savedRestriction = courseRestrictionRepository.saveAndFlush(courseRestrictionMapper.toEntity(courseRestriction));
        BeanUtils.copyProperties(savedRestriction, courseRestrictionValidationIssue);
        courseRestrictionValidationIssue.setCreateDate(new Date(savedRestriction.getCreateDate().getTime()));
        courseRestrictionValidationIssue.setUpdateDate(new Date(savedRestriction.getUpdateDate().getTime()));
        courseRestrictionValidationIssue.setHasPersisted(true);
    }

    private CourseRestrictionRuleData prepareCourseRestrictionRuleData(CourseRestriction courseRestriction, boolean isUpdate) {
        Course mainCourse = courseService.getCourseInfo(courseRestriction.getMainCourse(), courseRestriction.getMainCourseLevel());
        Course restrictedCourse = courseService.getCourseInfo(courseRestriction.getRestrictedCourse(), courseRestriction.getRestrictedCourseLevel());
        CourseRestrictionRuleData courseRestrictionRuleData = new CourseRestrictionRuleData();
        courseRestrictionRuleData.setCourseRestriction(courseRestriction);
        courseRestrictionRuleData.setMainCourse(mainCourse);
        courseRestrictionRuleData.setRestrictedCourse(restrictedCourse);
        courseRestrictionRuleData.setUpdate(isUpdate);
        return courseRestrictionRuleData;
    }

    private CourseRestrictionValidationIssue createCourseValidationIssue(CourseRestriction courseRestriction, List<ValidationIssue> validationIssues) {
        CourseRestrictionValidationIssue courseRestrictionValidationIssue = new CourseRestrictionValidationIssue();
        BeanUtils.copyProperties(courseRestriction, courseRestrictionValidationIssue);
        courseRestrictionValidationIssue.setValidationIssues(validationIssues);
        return courseRestrictionValidationIssue;
    }

}
