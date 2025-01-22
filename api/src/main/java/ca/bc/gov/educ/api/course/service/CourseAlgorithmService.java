package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.*;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseAlgorithmService {
    private final TraxStudentCourseService traxStudentCourseService;
    private final CourseRequirementService courseRequirementService;
    private final CourseRestrictionService courseRestrictionService;

    public CourseAlgorithmService(TraxStudentCourseService traxStudentCourseService,
                                  CourseRequirementService courseRequirementService,
                                  CourseRestrictionService courseRestrictionService) {
        this.traxStudentCourseService = traxStudentCourseService;
        this.courseRequirementService = courseRequirementService;
        this.courseRestrictionService = courseRestrictionService;
    }

    @Retry(name = "generalgetcall")
    public CourseAlgorithmData getCourseAlgorithmData(String pen, boolean sortForUI) {
        CourseAlgorithmData courseAlgorithmData = new CourseAlgorithmData();

        // Student Courses
        List<TraxStudentCourse> traxStudentCours = traxStudentCourseService.getStudentCourseList(pen, sortForUI);
        courseAlgorithmData.setStudentCourses(traxStudentCours);
        if (!traxStudentCours.isEmpty()) {
            List<String> courseCodes = traxStudentCours.stream()
                    .map(TraxStudentCourse::getCourseCode)
                    .distinct()
                    .toList();
            CourseList courseList = new CourseList();
            courseList.setCourseCodes(courseCodes);

            // Course Requirements
            CourseRequirements courseRequirements = courseRequirementService.getCourseRequirementListByCourses(courseList);
            if (courseRequirements != null && !courseRequirements.getCourseRequirementList().isEmpty()) {
                courseAlgorithmData.setCourseRequirements(courseRequirements.getCourseRequirementList());
            }

            // Course Restrictions
            CourseRestrictions courseRestrictions = courseRestrictionService.getCourseRestrictionsListByCourses(courseList);
            if (courseRestrictions != null && !courseRestrictions.getCourseRestrictionList().isEmpty()) {
                courseAlgorithmData.setCourseRestrictions(courseRestrictions.getCourseRestrictionList());
            }
        }
        return courseAlgorithmData;
    }
}
