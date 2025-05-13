package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.*;

import ca.bc.gov.educ.api.course.model.entity.ExaminableCourseEntity;
import ca.bc.gov.educ.api.course.service.v2.CourseService;
import ca.bc.gov.educ.api.course.model.transformer.ExaminableCourseTransformer;
import ca.bc.gov.educ.api.course.repository.ExaminableCourseRepository;
import ca.bc.gov.educ.api.course.util.criteria.CriteriaHelper;
import ca.bc.gov.educ.api.course.util.criteria.CriteriaSpecification;
import ca.bc.gov.educ.api.course.util.criteria.GradCriteria;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Slf4j
public class ExaminableCourseService {

    @Autowired
    private ExaminableCourseRepository examinableCourseRepo;

    @Autowired
    private ExaminableCourseTransformer examinableCourseTransformer;

    @Autowired
    private CourseService courseService;

    private static final String COURSE_NAME = "courseName";
    private static final String COURSE_CODE = "courseCode";
    private static final String COURSE_LEVEL = "courseLevel";
    private static final String COURSE_ID = "courseID";

    private static final String EXAMINABLE_START_DATE = "examinableStart";
    private static final String EXAMINABLE_END_DATE = "examinableEnd";
    private static final String OPTIONAL_START_DATE = "optionalStart";
    private static final String OPTIONAL_END_DATE = "optionalEnd";

    @Retry(name = "generalgetcall")
    public List<ExaminableCourse> getAllExaminableCourses() {
        try {
            List<ExaminableCourse> examinableCourseList  = examinableCourseTransformer.transformToDTO (examinableCourseRepo.findAll());
            List<ExaminableCourse> examinableCourses = new ArrayList<>();
            if (!examinableCourseList.isEmpty() && examinableCourseList != null) {
                for (ExaminableCourse courseDetails : examinableCourseList) {
                    if (courseDetails.getCourseID() != null && NumberUtils.isCreatable(courseDetails.getCourseID())) {
                        Course course = courseService.getCourseInfo(courseDetails.getCourseID());
                        if (course != null) {
                            courseDetails.setCourseName(course.getCourseName());
                            courseDetails.setCourseCode(course.getCourseCode());
                            courseDetails.setCourseLevel(course.getCourseLevel());
                        }
                    }
                    examinableCourses.add(courseDetails);
                }
            }
            return sort(examinableCourseList);
            } catch (Exception e) {
            log.error("Error while fetching examinable courses: {}", e.getMessage());
            throw new RuntimeException("Error while fetching examinable courses", e);
        }

    }

    private List<ExaminableCourse> sort(List<ExaminableCourse> examinableCourseList) {
        Collections.sort(examinableCourseList, Comparator.comparing(ExaminableCourse::getCourseCode)
                .thenComparing(ExaminableCourse::getCourseLevel, Comparator.nullsLast(String::compareTo)));
        return examinableCourseList;
    }

    @Retry(name = "generalgetcall")
    public List<ExaminableCourse> getExaminableCourseSearchList(String courseID, String courseName, String courseCode, String courseLevel, Date  examinableStart, Date examinableEnd, Date optionalStart, Date optionalEnd) {
        CriteriaHelper criteria = new CriteriaHelper();
        getSearchCriteria(COURSE_ID, courseID, COURSE_ID, criteria);
        getSearchCriteria(COURSE_NAME, courseName, COURSE_NAME, criteria);
        getSearchCriteria(COURSE_CODE, courseCode, COURSE_CODE, criteria);
        getSearchCriteria(COURSE_LEVEL, courseLevel, COURSE_LEVEL, criteria);

        if (examinableStart != null) {
            getSearchCriteriaDate(EXAMINABLE_START_DATE, examinableStart, null, EXAMINABLE_START_DATE, criteria);
        }
        if (examinableEnd != null) {
            getSearchCriteriaDate(EXAMINABLE_END_DATE, examinableEnd, examinableEnd, EXAMINABLE_END_DATE, criteria);
        }
        if (optionalStart != null) {
            getSearchCriteriaDate(OPTIONAL_START_DATE, optionalStart, null, OPTIONAL_START_DATE, criteria);
        }
        if (optionalEnd != null) {
            getSearchCriteriaDate(OPTIONAL_END_DATE, optionalEnd, optionalEnd, OPTIONAL_END_DATE, criteria);
        }
        List<ExaminableCourse> ecList = new ArrayList<>();
        criteria.orderBy("courseID", true);
        CriteriaSpecification<ExaminableCourseEntity> spec = new CriteriaSpecification<>(criteria);
        List<ExaminableCourse> examinableCourseSearchList = examinableCourseTransformer.transformToDTO(examinableCourseRepo.findAll(Specification.where(spec), criteria.getSortBy()));
        if (!examinableCourseSearchList.isEmpty()) {
            examinableCourseSearchList.forEach(ec -> {
                ExaminableCourse obj = new ExaminableCourse();
                Course course = courseService.getCourseInfo(ec.getCourseID());
                if(course != null) {
                    obj.setExaminableCourseID(ec.getExaminableCourseID());
                    obj.setCourseID(ec.getCourseID());
                    obj.setCourseName(course.getCourseName());
                    obj.setCourseCode(course.getCourseCode());
                    obj.setCourseLevel(course.getCourseLevel());
                    obj.setExaminableStart(ec.getExaminableStart());
                    obj.setExaminableEnd(ec.getExaminableEnd());
                    obj.setOptionalStart(ec.getOptionalStart());
                    obj.setOptionalEnd(ec.getOptionalEnd());
                    obj.setCreateUser(ec.getCreateUser());
                    obj.setCreateDate(ec.getCreateDate());
                    obj.setUpdateUser(ec.getUpdateUser());
                    obj.setUpdateDate(ec.getUpdateDate());
                }
                ecList.add(obj);
            });
            Collections.sort(ecList, Comparator.comparing(ExaminableCourse::getCourseCode)
                    .thenComparing(ExaminableCourse::getCourseLevel));
        }
        return ecList;
    }

    private void getSearchCriteria(String rootElement, String value, String paramterType, CriteriaHelper criteria) {
        if (StringUtils.isNotBlank(value)) {
            switch (paramterType) {
                case COURSE_NAME:
                    if (StringUtils.contains(value, "*")) {
                        criteria.add(rootElement, GradCriteria.OperationEnum.LIKE, StringUtils.strip(value.toUpperCase(), "*"));
                    } else {
                        criteria.add(rootElement, GradCriteria.OperationEnum.EQUALS, value.toUpperCase());
                    }
                    break;
                default:
                    if (StringUtils.contains(value, "*")) {
                        criteria.add(rootElement, GradCriteria.OperationEnum.STARTS_WITH_IGNORE_CASE, StringUtils.strip(value.toUpperCase(), "*"));
                    } else {
                        criteria.add(rootElement, GradCriteria.OperationEnum.EQUALS, value.toUpperCase());
                    }
                    break;
            }
        }
    }

    private void getSearchCriteriaDate(String rootElement, Date startDate, Date endDate, String paramterType,
                                       CriteriaHelper criteria) {
        if (paramterType.equalsIgnoreCase(EXAMINABLE_START_DATE)) {
            criteria.add(rootElement, GradCriteria.OperationEnum.GREATER_THAN_EQUAL_TO, startDate);
        } else if (paramterType.equalsIgnoreCase(EXAMINABLE_END_DATE)) {
            criteria.add(rootElement, GradCriteria.OperationEnum.LESS_THAN_EQUAL_TO, endDate);
            criteria.add(rootElement, GradCriteria.OperationEnum.GREATER_THAN, startDate);
        }
        if (paramterType.equalsIgnoreCase(OPTIONAL_START_DATE)) {
            criteria.add(rootElement, GradCriteria.OperationEnum.GREATER_THAN_EQUAL_TO, startDate);
        } else if (paramterType.equalsIgnoreCase(OPTIONAL_END_DATE)) {
            criteria.add(rootElement, GradCriteria.OperationEnum.LESS_THAN_EQUAL_TO, endDate);
            criteria.add(rootElement, GradCriteria.OperationEnum.GREATER_THAN, startDate);
        }
    }
}
