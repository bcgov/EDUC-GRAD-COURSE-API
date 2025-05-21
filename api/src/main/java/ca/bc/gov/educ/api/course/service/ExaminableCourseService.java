package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.exception.ServiceException;
import ca.bc.gov.educ.api.course.model.dto.*;
import ca.bc.gov.educ.api.course.model.transformer.ExaminableCourseTransformer;
import ca.bc.gov.educ.api.course.repository.ExaminableCourseRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Slf4j
public class ExaminableCourseService {

    @Autowired
    private ExaminableCourseRepository examinableCourseRepo;

    @Autowired
    private ExaminableCourseTransformer examinableCourseTransformer;


    /**
     * This method fetches all examinable courses from the database and transforms them into DTOs.
     * It also sorts the list of examinable courses by course code and level.
     *
     * @return List of ExaminableCourseDTO
     */
    @Retry(name = "generalgetcall")
    public List<ExaminableCourse> getAllExaminableCourses() {
        try {
            List<ExaminableCourse> examinableCourseList  = examinableCourseTransformer.transformToDTO (examinableCourseRepo.findAll());
            if (examinableCourseList == null || examinableCourseList.isEmpty()) {
                log.info("No examinable courses found");
                return Collections.emptyList();
            }
            return sort(examinableCourseList);
            } catch (Exception e) {
            log.error("Cannot fetch examinable courses: {}", e.getMessage());
            throw new ServiceException("Error while fetching examinable courses", e);
        }

    }

    private List<ExaminableCourse> sort(List<ExaminableCourse> examinableCourseList) {
        Collections.sort(examinableCourseList, Comparator.comparing(ExaminableCourse::getCourseCode)
                .thenComparing(ExaminableCourse::getCourseLevel, Comparator.nullsLast(String::compareTo)));
        return examinableCourseList;
    }
}
