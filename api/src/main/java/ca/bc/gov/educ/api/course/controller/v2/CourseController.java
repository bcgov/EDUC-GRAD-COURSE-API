package ca.bc.gov.educ.api.course.controller.v2;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.CourseDetail;
import ca.bc.gov.educ.api.course.model.dto.CourseRestrictionValidationIssue;
import ca.bc.gov.educ.api.course.model.dto.v2.CourseRestriction;
import ca.bc.gov.educ.api.course.model.dto.CourseSearchRequest;
import ca.bc.gov.educ.api.course.service.v2.CourseRestrictionService;
import ca.bc.gov.educ.api.course.service.v2.CourseService;
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
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController("CourseControllerV2")
@Slf4j
@RequestMapping(EducCourseApiConstants.GRAD_COURSE_URL_MAPPING_V2)
@OpenAPIDefinition(info = @Info(title = "API for Student Course Data.", description = "This API is for Reading Student Course data.", version = "2"),
        security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_GRAD_STUDENT_COURSE_DATA"})})
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    CourseService courseService;

    CourseRestrictionService courseRestrictionService;

    GradValidation validation;

    ResponseHelper response;

    @Autowired
    public CourseController(@Qualifier("courseServiceV2") CourseService courseService,@Qualifier("courseRestrictionServiceV2") CourseRestrictionService courseRestrictionService, GradValidation validation, ResponseHelper response) {
        this.courseService = courseService;
        this.courseRestrictionService = courseRestrictionService;
        this.validation = validation;
        this.response = response;
    }
    @GetMapping(EducCourseApiConstants.GET_COURSE_BY_COURSE_ID_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Find a Course by Course ID",
            description = "Get a Course by Course ID", tags = { "Courses" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")})
    public ResponseEntity<Course> getCourseDetails(@PathVariable String courseID) {
        log.debug("#getCourseDetails : courseID={}", courseID);
        return response.GET(courseService.getCourseInfo(courseID));
    }

    @GetMapping(EducCourseApiConstants.GET_COURSE_BY_CODE_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Find a Course by Course Code and Course Level",
            description = "Get a Course by Course Code and Course Level", tags = { "Courses" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")})
    public ResponseEntity<Course> getCourseDetails(@PathVariable String courseCode, @PathVariable String courseLevel) {
        log.debug("#getCourseDetails : courseCode={}, courseLevel={}", courseCode, courseLevel);
        return response.GET(courseService.getCourseInfo(courseCode, courseLevel));
    }

    @PostMapping(EducCourseApiConstants.GET_COURSE_BY_SEARCH_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Get Course by IDs",
            description = "Get Course by IDs", tags = { "Courses" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")})
    public ResponseEntity<List<CourseDetail>> getCourseDetails(@RequestBody CourseSearchRequest courseSearchRequest) {
        log.debug("#getCourseDetails search");
        return response.GET(courseService.getCourseDetails(courseSearchRequest));
    }

    @PostMapping (EducCourseApiConstants.SAVE_COURSE_RESTRICTION)
    @PreAuthorize(PermissionsConstants.UPDATE_GRAD_COURSE_RESTRICTION)
    @Operation(summary = "Save Course Restriction - v2", description = "Save Course Restriction", tags = { "Course Restrictions" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<CourseRestrictionValidationIssue> saveCourseRestriction(@Valid @RequestBody CourseRestriction courseRestriction) {
        logger.debug("Save Course Restriction");
        return response.GET(courseRestrictionService.saveCourseRestriction(courseRestriction));
    }

    @PutMapping (EducCourseApiConstants.UPDATE_COURSE_RESTRICTION)
    @PreAuthorize(PermissionsConstants.UPDATE_GRAD_COURSE_RESTRICTION)
    @Operation(summary = "Update Course Restriction - v2", description = "Update Course Restriction", tags = { "Course Restrictions" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<CourseRestrictionValidationIssue> updateCourseRestriction(@PathVariable UUID courseRestrictionId, @RequestBody @Valid CourseRestriction courseRestriction) {
        logger.debug("Update Course Restriction");
        return response.GET(courseRestrictionService.updateCourseRestriction(courseRestrictionId, courseRestriction));
    }

}
