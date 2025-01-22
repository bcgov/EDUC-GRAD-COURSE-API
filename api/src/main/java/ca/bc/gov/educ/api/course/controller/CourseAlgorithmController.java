package ca.bc.gov.educ.api.course.controller;

import ca.bc.gov.educ.api.course.config.GradDateEditor;
import ca.bc.gov.educ.api.course.model.dto.CourseAlgorithmData;
import ca.bc.gov.educ.api.course.service.CourseAlgorithmService;
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
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;

@CrossOrigin
@RestController
@RequestMapping(EducCourseApiConstants.COURSE_ALGORITHM_URL_MAPPING)
@OpenAPIDefinition(info = @Info(title = "API for Course Algorithm Data.",
        description = "This API is for Course Algorithm data.", version = "1"),
        security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_GRAD_COURSE_DATA","READ_GRAD_COURSE_REQUIREMENT_DATA"})})
@AllArgsConstructor
public class CourseAlgorithmController {

    private static Logger logger = LoggerFactory.getLogger(CourseAlgorithmController.class);

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        binder.registerCustomEditor(Date.class, null,  new GradDateEditor());
    }

    CourseAlgorithmService courseAlgorithmService;

    GradValidation validation;

    ResponseHelper response;

    @GetMapping(EducCourseApiConstants.GET_COURSE_ALGORITHM_DATA_BY_PEN_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Find Course Algorithm Data by pen", description = "Get Course Algorithm Data by pen", tags = { "Courses" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<CourseAlgorithmData> getCourseAlgorithmData(
            @PathVariable String pen) {
        logger.debug("getCourseAlgorithmData : ");

        return response.GET(courseAlgorithmService.getCourseAlgorithmData(pen, false));
    }
}
