package ca.bc.gov.educ.api.course.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.TraxStudentExam;
import ca.bc.gov.educ.api.course.model.transformer.TraxStudentExamTransformer;
import ca.bc.gov.educ.api.course.repository.TraxStudentExamRepository;

@Service
public class TraxStudentExamService {
    private static final Logger logger = LoggerFactory.getLogger(TraxStudentExamService.class);

    private final CourseService courseService;
    private final TraxStudentExamRepository studentExamRepo;
    private final TraxStudentExamTransformer traxStudentExamTransformer;

    public TraxStudentExamService(TraxStudentExamRepository studentExamRepo, TraxStudentExamTransformer traxStudentExamTransformer, CourseService courseService) {
        this.studentExamRepo = studentExamRepo;
        this.traxStudentExamTransformer = traxStudentExamTransformer;
        this.courseService = courseService;
    }

    /**
     * Get all student courses by PEN populated in Student Course DTO
     *
     * @param pen           PEN #
     * @param sortForUI     Sort For UI
     * @return Student Course
     */
    @Retry(name = "generalgetcall")
    public List<TraxStudentExam> getStudentExamList(String pen, boolean sortForUI) {
        List<TraxStudentExam> traxStudentExams = new ArrayList<>();
        try {
        	traxStudentExams = traxStudentExamTransformer.transformToDTO(studentExamRepo.findByPen(pen));
        	traxStudentExams.forEach(sC -> {
        		if(sC.getCourseLevel() != null) {
	        		if(sC.getCourseLevel().trim().equalsIgnoreCase("")) {
	        			getCourseDetails(sC.getCourseCode()," ", sC);
	        		}else {
	        			getCourseDetails(sC.getCourseCode(), sC.getCourseLevel(), sC);
	        		}
        		}
        	});
        } catch (Exception e) {
            logger.debug(String.format("Exception: %s",e));
        }
        getDataSorted(traxStudentExams,sortForUI);
        return traxStudentExams;
    }
    
    private void getDataSorted(List<TraxStudentExam> traxStudentExams, boolean sortForUI) {
    	if(sortForUI) {
        	Collections.sort(traxStudentExams, Comparator.comparing(TraxStudentExam::getPen)
                .thenComparing(TraxStudentExam::getCourseCode)
                .thenComparing(TraxStudentExam::getCourseLevel)
                .thenComparing(TraxStudentExam::getSessionDate));
        }else {
        	Collections.sort(traxStudentExams, Comparator.comparing(TraxStudentExam::getPen)
                .thenComparing(TraxStudentExam::getCompletedCourseFinalPercentage).reversed()
                .thenComparing(TraxStudentExam::getCourseLevel).reversed());
        }
    }

	private void getCourseDetails(String courseCode, String courseLevel, TraxStudentExam sC) {
		Course course = courseService.getCourseDetails(courseCode, courseLevel);
    	if(course != null) {
			sC.setCourseName(course.getCourseName());
			sC.setCourseDetails(course);
		  }
    }
}
