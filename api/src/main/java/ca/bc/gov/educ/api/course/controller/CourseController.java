package ca.bc.gov.educ.api.course.controller;

import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import ca.bc.gov.educ.api.course.config.GradDateEditor;
import ca.bc.gov.educ.api.course.model.dto.AllCourseRequirements;
import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.CourseList;
import ca.bc.gov.educ.api.course.model.dto.CourseRequirement;
import ca.bc.gov.educ.api.course.model.dto.CourseRequirements;
import ca.bc.gov.educ.api.course.model.dto.CourseRestriction;
import ca.bc.gov.educ.api.course.model.dto.CourseRestrictions;
import ca.bc.gov.educ.api.course.service.CourseRequirementService;
import ca.bc.gov.educ.api.course.service.CourseRestrictionService;
import ca.bc.gov.educ.api.course.service.CourseService;
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

@CrossOrigin
@RestController
@RequestMapping(EducCourseApiConstants.GRAD_COURSE_URL_MAPPING)
@OpenAPIDefinition(info = @Info(title = "API for Course Management.",
        description = "This API is for Managing Course data.", version = "1"),
        security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_GRAD_COURSE_DATA","READ_GRAD_COURSE_REQUIREMENT_DATA"})})
@AllArgsConstructor
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    @InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
	    binder.registerCustomEditor(Date.class, null,  new GradDateEditor());
	}
    
    CourseService courseService;
    
    CourseRequirementService courseRequirementService;
    
    CourseRestrictionService courseRestrictionService;
    
	GradValidation validation;
    
	ResponseHelper response;

    @GetMapping
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Find All Courses", description = "Get All Courses", tags = { "Courses" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<List<Course>> getAllCourses(
    		@RequestParam(value = "pageNo", required = false,defaultValue = "0") Integer pageNo, 
            @RequestParam(value = "pageSize", required = false,defaultValue = "150") Integer pageSize) { 
    	logger.debug("getAllCourses : ");
        return response.GET(courseService.getCourseList());
    }
    
    @GetMapping(EducCourseApiConstants.GET_COURSE_BY_CODE_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Find a Course by Course Code and Course Level",
            description = "Get a Course by Course Code and Course Level", tags = { "Courses" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<Course> getCourseDetails(@PathVariable String courseCode, @PathVariable String courseLevel) {
    	logger.debug("getCourseDetails : ");
        return response.GET(courseService.getCourseDetails(courseCode,courseLevel));
    }
    
    @GetMapping(EducCourseApiConstants.GET_COURSE_BY_SEARCH_PARAMS_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Search for a Course", description = "Search for a Course", tags = { "Courses" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST.")})
    public ResponseEntity<List<Course>> getCoursesSearch(
            @RequestParam(value = "courseCode", required = false) String courseCode,
            @RequestParam(value = "courseLevel", required = false) String courseLevel,
            @RequestParam(value = "courseName", required = false) String courseName,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "startDate", required = false) Date startDate,
			@RequestParam(value = "endDate", required = false) Date endDate) { 
    	logger.debug("getCoursesSearch : ");
        return response.GET(courseService.getCourseSearchList(courseCode,courseLevel,courseName,language,startDate,endDate));
    }
    
    @GetMapping(EducCourseApiConstants.GET_COURSE_DETAILS_BY_CODE_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Find a Course by Course Code", description = "Get a Course by Course Code", tags = { "Courses" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<Course> getCourseDetailsByCourse(@PathVariable String courseCode) {
    	logger.debug("getCourseDetails with Code : ");
        return response.GET(courseService.getCourseDetails(courseCode," "));

    }
    
    @GetMapping(EducCourseApiConstants.GET_COURSE_REQUIREMENT_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE_REQUIREMENT)
    @Operation(summary = "Find All Course Requirements",
            description = "Get All Course Requirements", tags = { "Course Requirements" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<List<AllCourseRequirements>> getAllCoursesRequirement(
    		@RequestParam(value = "pageNo", required = false,defaultValue = "0") Integer pageNo, 
            @RequestParam(value = "pageSize", required = false,defaultValue = "50") Integer pageSize) {
    	logger.debug("getAllCoursesRequirement : ");
        return response.GET(courseRequirementService.getAllCourseRequirementList(pageNo,pageSize));
    }
    
    @GetMapping(EducCourseApiConstants.GET_COURSE_REQUIREMENT_BY_RULE_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE_REQUIREMENT)
    @Operation(summary = "Find all Course Requirements by Rule",
            description = "Get all Course Requirements by Rule", tags = { "Course Requirements" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<List<CourseRequirement>> getAllCoursesRequirementByRule(
    		@RequestParam(value = "rule", required = true) String rule,
    		@RequestParam(value = "pageNo", required = false,defaultValue = "0") Integer pageNo, 
            @RequestParam(value = "pageSize", required = false,defaultValue = "150") Integer pageSize) { 
    	logger.debug("getAllCoursesRequirementByRule : ");
        return response.GET(courseRequirementService.getAllCourseRequirementListByRule(rule, pageNo, pageSize));
    }
    
    @GetMapping(EducCourseApiConstants.GET_COURSE_REQUIREMENT_BY_CODE_AND_LEVEL_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE_REQUIREMENT)
    @Operation(summary = "Find all Course Requirements by Course Code and Level",
            description = "Get all Course Requirements by Course Code and Level", tags = { "Course Requirements" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<CourseRequirements> getCourseRequirements(
            @RequestParam(value = "courseCode", required = false) String courseCode,
            @RequestParam(value = "courseLevel", required = false) String courseLevel) {

        CourseRequirements courseRequirements = null;

        if ((courseCode == null || courseCode.isEmpty()) && (courseLevel == null || courseLevel.isEmpty())) {
            logger.debug("**** CourseCode and CourseLevel Not Specified. Retreiving all CourseRequirements.");
            courseRequirements = courseRequirementService.getCourseRequirements();
        } else {
            logger.debug("**** Retrieving CourseRequirements for CourseCode= {} and CourseLevel= {}.", courseCode, courseLevel);
            courseRequirements = courseRequirementService.getCourseRequirements(courseCode, courseLevel);
        }

        return response.GET(courseRequirements);
    }
    
    @GetMapping(EducCourseApiConstants.GET_COURSE_REQUIREMENTS_BY_SEARCH_PARAMS_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE_REQUIREMENT)
    @Operation(summary = "Search for a Course Requirements", description = "Search for a Course Requirements", tags = { "Course Requirements" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST.")})
    public ResponseEntity<List<AllCourseRequirements>> getCoursesRequirementSearch(
            @RequestParam(value = "courseCode", required = false) String courseCode,
            @RequestParam(value = "courseLevel", required = false) String courseLevel,
            @RequestParam(value = "rule", required = false) String rule) {
    	logger.debug("getCoursesRequirementSearch : ");
        return response.GET(courseRequirementService.getCourseRequirementSearchList(courseCode,courseLevel,rule));
    }
    
    @PostMapping(EducCourseApiConstants.GET_COURSE_REQUIREMENT_BY_COURSE_LIST_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE_REQUIREMENT)
    @Operation(summary = "Find all Course Requirements by Course Code list",
            description = "Get all Course Requirements by Course Code list", tags = { "Course Requirements" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<CourseRequirements> getCoursesRequirementByCourse(@RequestBody CourseList courseList) { 
    	logger.debug("getAllCoursesRequirement : ");
        return response.GET(courseRequirementService.getCourseRequirementListByCourses(courseList));
    }

    @GetMapping(EducCourseApiConstants.CHECK_COURSE_REQUIREMENT_EXISTENCE)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE_REQUIREMENT)
    @Operation(summary = "Check Course Requirement exists", description = "Check Course Requirement exists", tags = { "Course Requirements" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<Boolean> checkCourseRequirementExists(
            @RequestParam(value = "courseCode", required = false)  String courseCode,
            @RequestParam(value = "courseLevel", required = false)  String courseLevel,
            @RequestParam(value = "ruleCode", required = false)  String ruleCode) {
        logger.debug("checkCourseRequirementExists : ");
        return response.GET(courseRequirementService.checkCourseRequirementExists(courseCode,courseLevel,ruleCode));
    }

    @PostMapping (EducCourseApiConstants.SAVE_COURSE_REQUIREMENT)
    @PreAuthorize(PermissionsConstants.UPDATE_GRAD_COURSE_RESTRICTION)
    @Operation(summary = "Save Course Requirement", description = "Save Course Requirement", tags = { "Course Requirements" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<CourseRequirement> saveCourseRequirement(@RequestBody CourseRequirement courseRequirement) {
        logger.debug("Save Course Requirement");
        if(StringUtils.isBlank(courseRequirement.getCourseCode())) {
            validation.addError("Course Code is required");
        }
        if(courseRequirement.getCourseLevel() == null) { // Blank level is allowed
            validation.addError("Course Level is required.");
        }
        if(courseRequirement.getRuleCode() == null || StringUtils.isBlank(courseRequirement.getRuleCode().getCourseRequirementCode())) {
            validation.addError("Rule Code is required.");
        }
        if(validation.hasErrors()) {
            validation.stopOnErrors();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return response.GET(courseRequirementService.saveCourseRequirement(courseRequirement));
    }
    
    @GetMapping(EducCourseApiConstants.GET_COURSE_RESTRICTION_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE_RESTRICTION)
    @Operation(summary = "Find All Course Restrictions",
            description = "Get All Course Restrictions", tags = { "Course Restrictions" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<List<CourseRestriction>> getAllCoursesRestriction(
    		@RequestParam(value = "pageNo", required = false,defaultValue = "0") Integer pageNo, 
            @RequestParam(value = "pageSize", required = false,defaultValue = "150") Integer pageSize) { 
    	logger.debug("getAllCoursesRestriction : ");
        return response.GET(courseRestrictionService.getAllCourseRestrictionList());
    }
    
    @GetMapping(EducCourseApiConstants.GET_COURSE_RESTRICTION_BY_SEARCH_PARAMS_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE_RESTRICTION)
    @Operation(summary = "Find All Course Restrictions",
            description = "Get All Course Restrictions", tags = { "Course Restrictions" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST.")})
    public ResponseEntity<List<CourseRestriction>> getCourseRestrictionsSearch(
            @RequestParam(value = "mainCourseCode", required = false) String mainCourseCode,
            @RequestParam(value = "mainCourseLevel", required = false) String mainCourseLevel) { 
    	logger.debug("getCourseRestrictionsSearch : ");
    	if((StringUtils.isNotBlank(mainCourseCode) && mainCourseCode.length() < 2)) {
    		validation.addError("Course Code should be minimum 2 digits for Course Search");
    	}
    	if((StringUtils.isNotBlank(mainCourseLevel) && mainCourseLevel.length() < 2)) {
    		validation.addError("Course Level should be minimum 2 digits for Course Search");
    	}
    	if(validation.hasErrors()) {
    		validation.stopOnErrors();
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}
        return response.GET(courseRestrictionService.getCourseRestrictionsSearchList(mainCourseCode,mainCourseLevel));
    }
    
    @GetMapping(EducCourseApiConstants.GET_COURSE_RESTRICTIONS_BY_CODE_AND_LEVEL_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE_RESTRICTION)
    @Operation(summary = "Find All Course Restrictions by Course Code and Level",
            description = "Get All Course Restrictions by Course Code and Level", tags = { "Course Restrictions" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<CourseRestrictions> getCourseRestrictions(
            @RequestParam(value = "courseCode", required = false) String courseCode,
            @RequestParam(value = "courseLevel", required = false) String courseLevel) {

        CourseRestrictions courseRestrictions = null;

        if ((courseCode == null || courseCode.isEmpty()) && (courseLevel == null || courseLevel.isEmpty())) {
            logger.debug("**** CourseCode and CourseLevel Not Specified. Retreiving all CourseRestrictions.");
            courseRestrictions = courseRestrictionService.getCourseRestrictions();
        } else {
            logger.debug("**** Retrieving CourseRestrictions for CourseCode= {} and CourseLevel= {}.", courseCode, courseLevel);
            courseRestrictions = courseRestrictionService.getCourseRestrictions(courseCode, courseLevel);
        }

        return response.GET(courseRestrictions);
    }
    
    @PostMapping(EducCourseApiConstants.GET_COURSE_RESTRICTIONS_BY_COURSE_LIST_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE_RESTRICTION)
    @Operation(summary = "Find all Course Restrictions by Course Code list", description = "Get all Course Restrictions by Course Code list", tags = { "Course Restrictions" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<CourseRestrictions> getCoursesRestrictionsByCourse(@RequestBody CourseList courseList) { 
    	logger.debug("getCoursesRestrictionsByCourse : ");
        return response.GET(courseRestrictionService.getCourseRestrictionsListByCourses(courseList));
    }

    @GetMapping(EducCourseApiConstants.GET_COURSE_RESTRICTION_BY_CODE_AND_LEVEL_AND_RESTRICTED_CODE_AND_LEVEL_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE_RESTRICTION)
    @Operation(summary = "Find Course Restriction by Course Code, Course Level, Restricted Course Code, and Restricted Course Level",
            description = "Get Course Restriction by Course Code, Course Level, Restricted Course Code, and Restricted Course Level", tags = { "Course Restrictions" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "NO CONTENT.")})
    public ResponseEntity<CourseRestriction> getCourseRestriction(
            @RequestParam(value = "courseCode")  String courseCode,
            @RequestParam(value = "courseLevel")  String courseLevel,
            @RequestParam(value = "restrictedCourseCode")  String restrictedCourseCode,
            @RequestParam(value = "restrictedCourseLevel")  String restrictedCourseLevel) {

        CourseRestriction courseRestriction = courseRestrictionService.getCourseRestriction(
                courseCode, courseLevel, restrictedCourseCode, restrictedCourseLevel);
        if (courseRestriction != null) {
            return response.GET(courseRestriction);
        }
        return response.NO_CONTENT();
    }

    @PostMapping (EducCourseApiConstants.SAVE_COURSE_RESTRICTION)
    @PreAuthorize(PermissionsConstants.UPDATE_GRAD_COURSE_RESTRICTION)
    @Operation(summary = "Save Course Restriction", description = "Save Course Restriction", tags = { "Course Restrictions" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<CourseRestriction> saveCourseRestriction(@RequestBody CourseRestriction courseRestriction) {
        logger.debug("Save Course Restriction");
        return response.GET(courseRestrictionService.saveCourseRestriction(courseRestriction));
    }

    @GetMapping(EducCourseApiConstants.CHECK_BLANK_LANGUAGE_COURSE_BY_CODE_AND_LEVEL_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Check if course is blank language", description = "Check if course is blank language", tags = { "Courses" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<Boolean> checkBlankLanguageCourse(
            @RequestParam(value = "courseCode") String courseCode,
            @RequestParam(value = "courseLevel") String courseLevel) {
        logger.debug("Check Blank Language Course : courseCode = {}, courseLevel = {}", courseCode, courseLevel);
        return response.GET(courseService.hasBlankLanguageCourse(courseCode, courseLevel));
    }

    @GetMapping(EducCourseApiConstants.CHECK_FRENCH_LANGUAGE_COURSE_BY_CODE_AND_LEVEL_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Check if course is french language", description = "Check if course is french language", tags = { "Courses" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<Boolean> checkFrenchLanguageCourse(
            @RequestParam(value = "courseCode") String courseCode,
            @RequestParam(value = "courseLevel") String courseLevel) {
        logger.debug("Check French Language Course : courseCode = {}, courseLevel = {}", courseCode, courseLevel);
        return response.GET(courseService.hasFrenchLanguageCourse(courseCode, courseLevel));
    }
}
