package ca.bc.gov.educ.api.course.service;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.bc.gov.educ.api.course.model.dto.CourseList;
import ca.bc.gov.educ.api.course.model.dto.CourseRestriction;
import ca.bc.gov.educ.api.course.model.dto.CourseRestrictions;
import ca.bc.gov.educ.api.course.model.transformer.CourseRestrictionsTransformer;
import ca.bc.gov.educ.api.course.repository.CourseRestrictionRepository;

@Service
public class CourseRestrictionService {

    @Autowired
    private CourseRestrictionRepository courseRestrictionRepository;

    @Autowired
    private CourseRestrictionsTransformer courseRestrictionTransformer;
    
    @Autowired
    CourseRestrictions courseRestrictions;

    @SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(CourseRestrictionService.class);

     /**
     * Get all course requirements in Course Restriction DTO
     *
     * @return Course 
     * @throws java.lang.Exception
     */
    public List<CourseRestriction> getAllCourseRestrictionList() {
    	List<CourseRestriction> restrictionList = courseRestrictionTransformer.transformToDTO(courseRestrictionRepository.findAll());
    	if(!restrictionList.isEmpty()) {    		
    		Collections.sort(restrictionList, Comparator.comparing(CourseRestriction::getMainCourse)
    				.thenComparing(CourseRestriction::getMainCourseLevel,Comparator.nullsLast(String::compareTo)));	    	
    	}
    	return restrictionList;
    }
    
    public CourseRestrictions getCourseRestrictions() {
    	List<CourseRestriction> restrictionList = courseRestrictionTransformer.transformToDTO(courseRestrictionRepository.findAll());
    	if(!restrictionList.isEmpty()) {
    		Collections.sort(restrictionList, Comparator.comparing(CourseRestriction::getMainCourse)
    				.thenComparing(CourseRestriction::getMainCourseLevel,Comparator.nullsLast(String::compareTo)));
    	}
    	courseRestrictions.setCourseRestrictions(restrictionList);
        return courseRestrictions;
    }

    public CourseRestrictions getCourseRestrictions(String courseCode, String courseLevel) {
        courseRestrictions.setCourseRestrictions(
                courseRestrictionTransformer.transformToDTO(
                        courseRestrictionRepository.findByMainCourseAndMainCourseLevel(courseCode, courseLevel)));
        return courseRestrictions;
    }

	public List<CourseRestriction> getCourseRestrictionsSearchList(String mainCourseCode, String mainCourseLevel) {
		return courseRestrictionTransformer.transformToDTO(
		        courseRestrictionRepository.searchForCourseRestriction(
		                StringUtils.toRootUpperCase(StringUtils.strip(mainCourseCode, "*")),
                        StringUtils.toRootUpperCase(StringUtils.strip(mainCourseLevel, "*"))));
	}

	public CourseRestrictions getCourseRestrictionsListByCourses(CourseList courseList) {
		courseRestrictions.setCourseRestrictions(
				courseRestrictionTransformer.transformToDTO(
						courseRestrictionRepository.findByMainCourseIn(courseList.getCourseCodes())));
        return courseRestrictions;
	}
}
