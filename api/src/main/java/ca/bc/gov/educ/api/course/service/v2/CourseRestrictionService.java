package ca.bc.gov.educ.api.course.service.v2;

import ca.bc.gov.educ.api.course.exception.CourseAPIRuntimeException;
import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.CourseRestrictionRuleData;
import ca.bc.gov.educ.api.course.model.dto.CourseRestrictionValidationIssue;
import ca.bc.gov.educ.api.course.model.dto.ValidationIssue;
import ca.bc.gov.educ.api.course.model.dto.mapper.CourseRestrictionMapper;
import ca.bc.gov.educ.api.course.model.dto.v2.CourseRestriction;
import ca.bc.gov.educ.api.course.model.entity.CourseRestrictionsEntity;
import ca.bc.gov.educ.api.course.repository.CourseRestrictionRepository;
import ca.bc.gov.educ.api.course.repository.CourseRestrictionRepositoryStream;
import ca.bc.gov.educ.api.course.validation.rules.CourseRestrictionRulesProcessor;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service("courseRestrictionServiceV2")
@Slf4j
@AllArgsConstructor
public class CourseRestrictionService {

    private static final CourseRestrictionMapper courseRestrictionMapper = CourseRestrictionMapper.mapper;

    private CourseRestrictionRulesProcessor courseRestrictionRulesProcessor;
    private CourseService courseService;
    private CourseRestrictionRepository courseRestrictionRepository;
    private CourseRestrictionRepositoryStream courseRestrictionRepositoryStream;

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

    /**
     * Generate course restrictions CSV and stream directly to HTTP response.
     *
     * @param response HTTP response to stream CSV to
     * @throws IOException if writing to response fails
     */
    @Transactional(readOnly = true)
    public void generateCourseRestrictionsReportStream(HttpServletResponse response) throws IOException {
        log.debug("Generating Course Restrictions CSV download stream");

        List<String> headers = Arrays.asList(
                "Course Code Main",
                "Course Level Main",
                "Course Code Restricted",
                "Course Level Restricted",
                "Restriction Start Date",
                "Restriction End Date"
        );

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"CourseRestrictions_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv\"");

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder().build();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
             CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);
             Stream<CourseRestrictionsEntity> restrictionStream = courseRestrictionRepositoryStream.streamAll()) {

            csvPrinter.printRecord(headers);

            restrictionStream
                    .map(restriction -> prepareCourseRestrictionDataForCsv(restriction, dateFormatter))
                    .forEach(csvRowData -> {
                        try {
                            csvPrinter.printRecord(csvRowData);
                            csvPrinter.flush();
                        } catch (IOException e) {
                            log.error("Error writing CSV row", e);
                            throw new CourseAPIRuntimeException("Failed to write CSV row: " + e);
                        }
                    });

            csvPrinter.flush();
            log.debug("Course Restrictions CSV stream completed successfully");
        }
    }

    private List<String> prepareCourseRestrictionDataForCsv(CourseRestrictionsEntity restriction, DateTimeFormatter dateFormatter) {
        List<String> csvRowData = new ArrayList<>();

        csvRowData.add(restriction.getMainCourse() != null ? restriction.getMainCourse() : "");
        csvRowData.add(restriction.getMainCourseLevel() != null ? restriction.getMainCourseLevel() : "");
        csvRowData.add(restriction.getRestrictedCourse() != null ? restriction.getRestrictedCourse() : "");
        csvRowData.add(restriction.getRestrictedCourseLevel() != null ? restriction.getRestrictedCourseLevel() : "");
        csvRowData.add(restriction.getRestrictionStartDate() != null ? restriction.getRestrictionStartDate().format(dateFormatter) : "");
        String endDate = "";
        if (restriction.getRestrictionEndDate() != null) {
            String formattedEndDate = restriction.getRestrictionEndDate().format(dateFormatter);
            if (!"2099-12-31".equals(formattedEndDate)) {
                endDate = formattedEndDate;
            }
        }
        csvRowData.add(endDate);

        return csvRowData;
    }
}
