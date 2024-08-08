package ca.bc.gov.educ.api.course.controller;

import ca.bc.gov.educ.api.course.model.dto.TraxStudentCourse;
import ca.bc.gov.educ.api.course.service.TraxStudentCourseService;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import ca.bc.gov.educ.api.course.util.GradValidation;
import ca.bc.gov.educ.api.course.util.PermissionsConstants;
import ca.bc.gov.educ.api.course.util.ResponseHelper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(EducCourseApiConstants.STUDENT_COURSE_URL_MAPPING)
@OpenAPIDefinition(info = @Info(title = "API for Student Course Data from TRAX.", description = "This API is for Reading Student Course data from TRAX.", version = "1"),
        security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_GRAD_STUDENT_COURSE_DATA"})})
public class StudentCourseController {

    private static final Logger logger = LoggerFactory.getLogger(StudentCourseController.class);

    @Autowired
    TraxStudentCourseService traxStudentCourseService;

    @Autowired
    GradValidation validation;

    @Autowired
    ResponseHelper response;

    @GetMapping(EducCourseApiConstants.GET_STUDENT_COURSE_BY_PEN_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Find All Student Courses by PEN", description = "Get All Student Courses by PEN", tags = {"Student Courses"})
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "204", description = "NO CONTENT")})
    public ResponseEntity<List<TraxStudentCourse>> getStudentCourseByPEN(
            @PathVariable String pen, @RequestParam(value = "sortForUI", required = false, defaultValue = "false") boolean sortForUI) {
        validation.requiredField(pen, "Pen");
        if (validation.hasErrors()) {
            validation.stopOnErrors();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            String penNumber = pen.substring(5);
            logger.debug("#Get All Student Course by PEN: *****{}", penNumber);
            List<TraxStudentCourse> traxStudentCourseList = traxStudentCourseService.getStudentCourseList(pen, sortForUI);
            if (traxStudentCourseList.isEmpty()) {
                return response.NO_CONTENT();
            }
            return response.GET(traxStudentCourseList);
        }
    }

    @GetMapping(EducCourseApiConstants.CHECK_FRENCH_IMMERSION_COURSE)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Check if student is taking any of french immersion courses", description = "Check if student is taking any of french immersion courses", tags = { "Student Courses" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<Boolean> checkFrenchImmersionCourse(@PathVariable String pen) {
        logger.debug("Check French Immersion Course : pen = {}", pen);
        return response.GET(traxStudentCourseService.checkFrenchImmersionCourse(pen));
    }

    @GetMapping(EducCourseApiConstants.CHECK_FRENCH_IMMERSION_COURSE_BY_PEN_AND_LEVEL_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Check if student is taking any of french immersion courses by pen# and course level", description = "Check if student is taking any of french immersion courses by pen# and course level", tags = { "Student Courses" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<Boolean> checkFrenchImmersionCourse(
            @RequestParam(value = "pen") String pen,
            @RequestParam(value = "courseLevel") String courseLevel) {
        logger.debug("Check French Immersion Course : pen = {}, course level [{}]", pen, courseLevel);
        return response.GET(traxStudentCourseService.checkFrenchImmersionCourse(pen, courseLevel));
    }

    @GetMapping(EducCourseApiConstants.CHECK_FRENCH_IMMERSION_COURSE_FOR_EN_BY_PEN_AND_LEVEL_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Check if student with EN program is taking any of french immersion courses", description = "Check if student with EN program is taking any of french immersion courses", tags = { "Student Courses" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<Boolean> checkFrenchImmersionCourseForEN(
            @RequestParam(value = "pen") String pen,
            @RequestParam(value = "courseLevel") String courseLevel) {
        logger.debug("Check French Immersion Course for 1986 EN : pen = {}, course level [{}]", pen, courseLevel);
        return response.GET(traxStudentCourseService.checkFrenchImmersionCourseForEN(pen, courseLevel));
    }
}
