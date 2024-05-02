package ca.bc.gov.educ.api.course.controller;

import ca.bc.gov.educ.api.course.config.GradDateEditor;
import ca.bc.gov.educ.api.course.model.dto.*;
import ca.bc.gov.educ.api.course.service.CourseRequirementService;
import ca.bc.gov.educ.api.course.service.CourseRestrictionService;
import ca.bc.gov.educ.api.course.service.CourseService;
import ca.bc.gov.educ.api.course.service.InstituteService;
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
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(EducCourseApiConstants.GRAD_COURSE_URL_MAPPING)
@OpenAPIDefinition(info = @Info(title = "API for Course Management.",
        description = "This API is for Managing Course data.", version = "1"),
        security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_GRAD_COURSE_DATA","READ_GRAD_COURSE_REQUIREMENT_DATA"})})
public class InstituteController {

    private static final Logger logger = LoggerFactory.getLogger(InstituteController.class);

    @InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
	    binder.registerCustomEditor(Date.class, null,  new GradDateEditor());
	}
    
    @Autowired
    InstituteService instituteService;
    
    @Autowired
    CourseRequirementService courseRequirementService;
    
    @Autowired
    CourseRestrictionService courseRestrictionService;
    
    @Autowired
	GradValidation validation;
    
    @Autowired
	ResponseHelper response;

    @GetMapping("/school")
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Find All Schools", description = "Get All Schools", tags = { "Institute" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<List<School>> getAllSchools(@RequestHeader(name="Authorization") String accessToken) {
        return response.GET(instituteService.getCommonSchools(accessToken.replaceAll("Bearer ", "")));
    }

    @GetMapping("/school/{schoolId}")
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Find School details by ID", description = "Get School details by ID", tags = { "Institute" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<SchoolDetail> getSchoolDetailsById(
            @PathVariable String schoolId, @RequestHeader(name="Authorization") String accessToken) {
        logger.debug("START getSchoolDetailsById");
        return response.GET(instituteService.getCommonSchoolDetailById(schoolId, accessToken.replaceAll("Bearer ", "")));
    }

    @GetMapping("/school-details")
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Find All School details", description = "Get All School details", tags = { "Institute" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<List<SchoolDetail>> getAllSchoolDetails(@RequestHeader(name="Authorization") String accessToken) {
        return response.GET(instituteService.getAllSchoolDetails());
    }
}
