package ca.bc.gov.educ.api.course.service.v2;

import ca.bc.gov.educ.api.course.exception.ServiceException;
import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.CourseRestrictionRuleData;
import ca.bc.gov.educ.api.course.model.dto.CourseRestrictionValidationIssue;
import ca.bc.gov.educ.api.course.model.dto.ValidationIssue;
import ca.bc.gov.educ.api.course.model.dto.mapper.CourseRestrictionMapper;
import ca.bc.gov.educ.api.course.model.dto.v2.CourseRestriction;
import ca.bc.gov.educ.api.course.model.entity.CourseRestrictionsEntity;
import ca.bc.gov.educ.api.course.repository.CourseRestrictionRepository;
import ca.bc.gov.educ.api.course.validation.rules.CourseRestrictionRulesProcessor;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("courseRestrictionServiceV2")
@Slf4j
public class CourseRestrictionService {

    private CourseRestrictionRulesProcessor courseRestrictionRulesProcessor;
    private CourseService courseService;

    private CourseRestrictionRepository courseRestrictionRepository;
    private CourseRestrictionMapper courseRestrictionMapper;

    @Autowired
    public CourseRestrictionService(CourseRestrictionRulesProcessor courseRestrictionRulesProcessor, CourseService courseService, CourseRestrictionRepository courseRestrictionRepository, CourseRestrictionMapper courseRestrictionMapper) {
        this.courseRestrictionRulesProcessor = courseRestrictionRulesProcessor;
        this.courseService = courseService;
        this.courseRestrictionRepository = courseRestrictionRepository;
        this.courseRestrictionMapper = courseRestrictionMapper;
    }

    public CourseRestriction getCourseRestriction(String mainCourseCode, String mainCourseLevel, String restrictedCourseCode, String restrictedCourseLevel) {
        Optional<CourseRestrictionsEntity>  courseRestrictionsEntity = courseRestrictionRepository.findByMainCourseAndMainCourseLevelAndRestrictedCourseAndRestrictedCourseLevel(
                mainCourseCode, mainCourseLevel, restrictedCourseCode, restrictedCourseLevel);
        if (courseRestrictionsEntity.isPresent()) {
            return courseRestrictionMapper.toStructure(courseRestrictionsEntity.get());
        }
        return null;
    }

    public CourseRestriction getCourseRestrictionById(UUID courseRestrictionId) {
        Optional<CourseRestrictionsEntity>  courseRestrictionsEntity = courseRestrictionRepository.findById(courseRestrictionId);
        if (courseRestrictionsEntity.isPresent()) {
            return courseRestrictionMapper.toStructure(courseRestrictionsEntity.get());
        }
        return null;
    }

    @Retry(name = "generalpostcall")
    public CourseRestrictionValidationIssue updateCourseRestriction(UUID courseRestrictionId, CourseRestriction courseRestriction) {
        try {
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
        } catch (Exception e) {
            throw new ServiceException("Unable to save course restriction.", e);
        }
    }


    @Retry(name = "generalpostcall")
    public CourseRestrictionValidationIssue saveCourseRestriction(CourseRestriction courseRestriction) {
        try {
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
        } catch (Exception e) {
            throw new ServiceException("Unable to save course restriction.", e);
        }
    }

    private void persistCourseRestriction(CourseRestriction courseRestriction, CourseRestrictionValidationIssue courseRestrictionValidationIssue) {
        CourseRestrictionsEntity savedRestriction = courseRestrictionRepository.saveAndFlush(courseRestrictionMapper.toEntity(courseRestriction));
        if(savedRestriction != null) {
            BeanUtils.copyProperties(savedRestriction, courseRestrictionValidationIssue);
            courseRestrictionValidationIssue.setCreateDate(new Date(savedRestriction.getCreateDate().getTime()));
            courseRestrictionValidationIssue.setUpdateDate(new Date(savedRestriction.getUpdateDate().getTime()));
            courseRestrictionValidationIssue.setHasPersisted(true);
        }
    }

    private CourseRestrictionRuleData prepareCourseRestrictionRuleData(CourseRestriction courseRestriction, boolean isUpdate) {
        Course mainCourse = courseService.getCourseInfo(courseRestriction.getMainCourse(), courseRestriction.getMainCourseLevel());
        Course restrictedCourse = courseService.getCourseInfo(courseRestriction.getRestrictedCourse(), courseRestriction.getRestrictedCourseLevel());
        CourseRestrictionRuleData courseRestrictionRuleData= new CourseRestrictionRuleData();
        courseRestrictionRuleData.setCourseRestriction(courseRestriction);
        courseRestrictionRuleData.setMainCourse(mainCourse);
        courseRestrictionRuleData.setRestrictedCourse(restrictedCourse);
        courseRestrictionRuleData.setUpdate(isUpdate);
        return courseRestrictionRuleData;
    }

    private CourseRestrictionValidationIssue createCourseValidationIssue(CourseRestriction courseRestriction, List<ValidationIssue> validationIssues){
        CourseRestrictionValidationIssue courseRestrictionValidationIssue = new CourseRestrictionValidationIssue();
        BeanUtils.copyProperties(courseRestriction, courseRestrictionValidationIssue);
        courseRestrictionValidationIssue.setValidationIssues(validationIssues);
        return courseRestrictionValidationIssue;
    }

}
