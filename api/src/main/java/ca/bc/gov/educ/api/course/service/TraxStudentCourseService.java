package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.TraxStudentCourse;
import ca.bc.gov.educ.api.course.model.transformer.TraxStudentCourseTransformer;
import ca.bc.gov.educ.api.course.repository.TraxStudentCourseRepository;
import io.github.resilience4j.retry.annotation.Retry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class TraxStudentCourseService {
    private static final Logger logger = LoggerFactory.getLogger(TraxStudentCourseService.class);

    private final CourseService courseService;
    private final TraxStudentCourseRepository traxStudentCourseRepository;
    private final TraxStudentCourseTransformer traxStudentCourseTransformer;

    public TraxStudentCourseService(TraxStudentCourseRepository traxStudentCourseRepository, TraxStudentCourseTransformer traxStudentCourseTransformer, CourseService courseService) {
        this.traxStudentCourseRepository = traxStudentCourseRepository;
        this.traxStudentCourseTransformer = traxStudentCourseTransformer;
        this.courseService = courseService;
    }

    /**
     * Get all student courses by PEN populated in Student Course DTO
     *
     * @param pen           PEN #
     * @param sortForUI     Sort For UI
     * @return Student Course
     */
    public List<TraxStudentCourse> getStudentCourseList(String pen, boolean sortForUI) {
        List<TraxStudentCourse> traxStudentCours = new ArrayList<>();
        try {
        	traxStudentCours = traxStudentCourseTransformer.transformToDTO(traxStudentCourseRepository.findByPen(pen));
        	traxStudentCours.forEach(this::populate);
        } catch (Exception e) {
            logger.debug(String.format("Exception: %s",e));
        }
        getDataSorted(traxStudentCours,sortForUI);
        return traxStudentCours;
    }

    private void populate(TraxStudentCourse traxStudentCourse) {
		if(StringUtils.isNotBlank(traxStudentCourse.getRelatedCourse())
			|| StringUtils.isNotBlank(traxStudentCourse.getRelatedLevel())
			|| StringUtils.isNotBlank(traxStudentCourse.getCustomizedCourseName())
			|| StringUtils.isNotBlank(traxStudentCourse.getBestSchoolPercent() != null? traxStudentCourse.getBestSchoolPercent().toString() : null)
			|| StringUtils.isNotBlank(traxStudentCourse.getBestExamPercent() != null? traxStudentCourse.getBestExamPercent().toString() : null)
			|| StringUtils.isNotBlank(traxStudentCourse.getMetLitNumRequirement())) {
			traxStudentCourse.setHasRelatedCourse("Y");
		}else {
			traxStudentCourse.setHasRelatedCourse("N");
		}
		if(traxStudentCourse.getCourseLevel() != null) {
			if(traxStudentCourse.getCourseLevel().trim().equalsIgnoreCase("")) {
				getCourseDetails(traxStudentCourse.getCourseCode()," ", traxStudentCourse);
			}else {
				getCourseDetails(traxStudentCourse.getCourseCode(), traxStudentCourse.getCourseLevel(), traxStudentCourse);
			}
		}
		if((StringUtils.isNotBlank(traxStudentCourse.getRelatedCourse()) || StringUtils.isNotBlank(traxStudentCourse.getRelatedLevel())) && traxStudentCourse.getRelatedLevel() != null) {
			checkForMoreOptions(traxStudentCourse);
		}
	}
    
    private void checkForMoreOptions(TraxStudentCourse sC) {
		if(sC.getRelatedLevel().trim().equalsIgnoreCase("")) {
			Course course = courseService.getCourseDetails(sC.getRelatedCourse(), " ");
			if(course != null) {
				sC.setRelatedCourseName(course.getCourseName());
			}
		} else {
			Course course = courseService.getCourseDetails(sC.getRelatedCourse(), sC.getRelatedLevel());
			if(course != null) {
				sC.setRelatedCourseName(course.getCourseName());
			}
		}
	 }
    
    private void getDataSorted(List<TraxStudentCourse> traxStudentCours, boolean sortForUI) {
    	if(sortForUI) {
        	Collections.sort(traxStudentCours, Comparator.comparing(TraxStudentCourse::getPen)
                .thenComparing(TraxStudentCourse::getCourseCode)
                .thenComparing(TraxStudentCourse::getCourseLevel)
                .thenComparing(TraxStudentCourse::getSessionDate));
        }else {
        	Collections.sort(traxStudentCours, Comparator.comparing(TraxStudentCourse::getPen)
                .thenComparing(TraxStudentCourse::getCompletedCoursePercentage).reversed()
                .thenComparing(TraxStudentCourse::getCredits).reversed()
                .thenComparing(TraxStudentCourse::getCourseLevel).reversed());
        }
    }

	private void getCourseDetails(String courseCode, String courseLevel, TraxStudentCourse sC) {
		Course course = courseService.getCourseDetails(courseCode, courseLevel);
    	if(course != null) {
			sC.setCourseName(course.getCourseName());
			sC.setGenericCourseType(course.getGenericCourseType());
			sC.setLanguage(course.getLanguage());
			sC.setCourseDetails(course);
			sC.setOriginalCredits(course.getNumCredits());
		  }
    }

	@Retry(name = "generalgetcall")
	public boolean checkFrenchImmersionCourse(String pen) {
		return traxStudentCourseRepository.countFrenchImmersionCourses(pen) > 0;
	}

	@Retry(name = "generalgetcall")
	public boolean checkFrenchImmersionCourse(String pen, String courseLevel) {
		return traxStudentCourseRepository.countFrenchImmersionCourses(pen, courseLevel) > 0;
	}

	@Retry(name = "generalgetcall")
	public boolean checkFrenchImmersionCourseForEN(String pen, String courseLevel) {
		return traxStudentCourseRepository.countFrenchImmersionCourse(pen, courseLevel) > 0;
	}

}
