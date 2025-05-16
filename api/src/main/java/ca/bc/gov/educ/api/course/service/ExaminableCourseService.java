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
}
