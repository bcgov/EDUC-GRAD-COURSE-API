package ca.bc.gov.educ.api.course.controller;

import ca.bc.gov.educ.api.course.model.dto.ExaminableCourse;
import ca.bc.gov.educ.api.course.service.ExaminableCourseService;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import ca.bc.gov.educ.api.course.util.PermissionsConstants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(EducCourseApiConstants.GRAD_COURSE_URL_MAPPING)
@OpenAPIDefinition(info = @Info(title = "API for Examinable Course Data.", description = "This API is for Reading Examinable courses.", version = "1"),
        security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_GRAD_COURSE_DATA"})})
public class ExaminableCourseController {

    private final ExaminableCourseService examinableCourseService;

    public ExaminableCourseController(ExaminableCourseService examinableCourseService) {
        this.examinableCourseService = examinableCourseService;
    }

    @GetMapping(EducCourseApiConstants.GET_ALL_EXAMINABLE_COURSES)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Finds examinable courses", description = "Gets all examinable courses", tags = {"Examinable Course"})
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "204", description = "NO CONTENT")})
    public ResponseEntity<List<ExaminableCourse>> getAllExaminableCourses() {
        log.debug("#get all ExaminableCourses");
        return ResponseEntity.ok().body(examinableCourseService.getAllExaminableCourses());
    }

}
