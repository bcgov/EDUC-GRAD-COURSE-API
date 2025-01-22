package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.entity.CourseEntity;
import ca.bc.gov.educ.api.course.model.entity.CourseId;
import ca.bc.gov.educ.api.course.model.transformer.CourseTransformer;
import ca.bc.gov.educ.api.course.repository.CourseRepository;
import ca.bc.gov.educ.api.course.util.criteria.CriteriaHelper;
import ca.bc.gov.educ.api.course.util.criteria.GradCriteria.OperationEnum;
import ca.bc.gov.educ.api.course.util.criteria.CriteriaSpecification;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class CourseService {

	private static final String START_DATE = "startDate";
	private static final String END_DATE = "endDate";
	private static final String LANGUAGE= "language";
	private static final String COURSE_NAME= "courseName";
	
    private CourseRepository courseRepo;

    private CourseTransformer courseTransformer;

    @SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(CourseService.class);

    /**
     * Get all courses in Course DTO
     *
     * @return Course
     * @throws java.lang.Exception
     */
    @Retry(name = "generalgetcall")
    public List<Course> getCourseList() {
        return courseTransformer.transformToDTO(courseRepo.findAll());
    }

    public Course getCourseDetails(String crseCode, String crseLvl) {
        CourseId key = new CourseId();
        key.setCourseCode(crseCode);
        key.setCourseLevel(crseLvl);
        return courseTransformer.transformToDTO(courseRepo.findByCourseKey(key));
    }

    @Retry(name = "generalgetcall")
    public boolean hasFrenchLanguageCourse(String courseCode, String courseLevel) {
        return this.courseRepo.countTabCourses(courseCode, courseLevel, "F") > 0L;
    }

    @Retry(name = "generalgetcall")
    public boolean hasBlankLanguageCourse(String courseCode, String courseLevel) {
        return this.courseRepo.countTabCourses(courseCode, courseLevel, " ") > 0L;
    }

    @Retry(name = "generalgetcall")
    public List<Course> getCourseSearchList(String courseCode, String courseLevel, String courseName, String language, Date startDate, Date endDate) {
        CriteriaHelper criteria = new CriteriaHelper();
        getSearchCriteria("courseKey.courseCode", courseCode, "courseCode", criteria);
        getSearchCriteria("courseKey.courseLevel", courseLevel, "courseLevel", criteria);
        getSearchCriteria(COURSE_NAME, courseName, COURSE_NAME, criteria);
        getSearchCriteria(LANGUAGE, language, LANGUAGE, criteria);

        if (startDate != null) {
            getSearchCriteriaDate(START_DATE, startDate, null, START_DATE, criteria);
        }
        if (endDate != null) {
            getSearchCriteriaDate(END_DATE, startDate, endDate, END_DATE, criteria);
        }
        criteria.orderBy("courseKey.courseCode", true);
        criteria.orderBy("courseKey.courseLevel", true);

        CriteriaSpecification<CourseEntity> spec = new CriteriaSpecification<>(criteria);
        return courseTransformer.transformToDTO(courseRepo.findAll(Specification.where(spec), criteria.getSortBy()));
    }

    private void getSearchCriteriaDate(String rootElement, Date startDate, Date endDate, String paramterType,
                                                 CriteriaHelper criteria) {
        if (paramterType.equalsIgnoreCase(START_DATE)) {
            criteria.add(rootElement, OperationEnum.GREATER_THAN_EQUAL_TO, startDate);
        } else if (paramterType.equalsIgnoreCase(END_DATE)) {
            criteria.add(rootElement, OperationEnum.LESS_THAN_EQUAL_TO, endDate);
            criteria.add(rootElement, OperationEnum.GREATER_THAN, startDate);
        }
    }

    private void getSearchCriteria(String rootElement, String value, String paramterType, CriteriaHelper criteria) {
        if (StringUtils.isNotBlank(value)) {
            switch (paramterType) {
                case LANGUAGE:
                    if (StringUtils.equalsIgnoreCase("F", value)) {
                        criteria.add(rootElement, OperationEnum.EQUALS, value.toUpperCase());
                    } else {
                        criteria.add(rootElement, OperationEnum.NOT_EQUALS, "F");
                    }
                    break;
                case COURSE_NAME:
                    if (StringUtils.contains(value, "*")) {
                        criteria.add(rootElement, OperationEnum.LIKE, StringUtils.strip(value.toUpperCase(), "*"));
                    } else {
                        criteria.add(rootElement, OperationEnum.EQUALS, value.toUpperCase());
                    }
                    break;
                default:
                    if (StringUtils.contains(value, "*")) {
                        criteria.add(rootElement, OperationEnum.STARTS_WITH_IGNORE_CASE, StringUtils.strip(value.toUpperCase(), "*"));
                    } else {
                        criteria.add(rootElement, OperationEnum.EQUALS, value.toUpperCase());
                    }
                    break;
            }
        }
    }
}
