package ca.bc.gov.educ.api.course.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.CourseRequirement;
import ca.bc.gov.educ.api.course.service.CourseRequirementService;
import ca.bc.gov.educ.api.course.service.CourseService;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;

@CrossOrigin
@RestController
@RequestMapping(EducCourseApiConstants.GRAD_COURSE_API_ROOT_MAPPING)
public class CourseController {

    private static Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    CourseService courseService;
    
    @Autowired
    CourseRequirementService courseRequirementService;

    @GetMapping
    public List<Course> getAllCourses(
    		@RequestParam(value = "pageNo", required = false,defaultValue = "0") Integer pageNo, 
            @RequestParam(value = "pageSize", required = false,defaultValue = "150") Integer pageSize) { 
    	logger.debug("getAllCourses : ");
        return courseService.getCourseList(pageNo,pageSize);
    }
    
    @GetMapping(EducCourseApiConstants.GET_COURSE_REQUIREMENT_MAPPING)
    public List<CourseRequirement> getAllCoursesRequirement(
    		@RequestParam(value = "pageNo", required = false,defaultValue = "0") Integer pageNo, 
            @RequestParam(value = "pageSize", required = false,defaultValue = "150") Integer pageSize) { 
    	logger.debug("getAllCoursesRequirement : ");
        return courseRequirementService.getAllCourseRequirementList(pageNo,pageSize);
    }
    
    @GetMapping(EducCourseApiConstants.GET_COURSE_REQUIREMENT_BY_RULE_MAPPING)
    public List<CourseRequirement> getAllCoursesRequirementByRule(
    		@RequestParam(value = "rule", required = true) String rule,
    		@RequestParam(value = "pageNo", required = false,defaultValue = "0") Integer pageNo, 
            @RequestParam(value = "pageSize", required = false,defaultValue = "150") Integer pageSize) { 
    	logger.debug("getAllCoursesRequirementByRule : ");
        return courseRequirementService.getAllCourseRequirementListByRule(rule, pageNo, pageSize);
    }
}
