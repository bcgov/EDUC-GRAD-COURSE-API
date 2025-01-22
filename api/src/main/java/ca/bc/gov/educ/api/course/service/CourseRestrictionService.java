package ca.bc.gov.educ.api.course.service;


import java.util.*;

import ca.bc.gov.educ.api.course.model.entity.CourseRestrictionsEntity;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import ca.bc.gov.educ.api.course.model.dto.CourseList;
import ca.bc.gov.educ.api.course.model.dto.CourseRestriction;
import ca.bc.gov.educ.api.course.model.dto.CourseRestrictions;
import ca.bc.gov.educ.api.course.model.transformer.CourseRestrictionsTransformer;
import ca.bc.gov.educ.api.course.repository.CourseRestrictionRepository;

@Service
@AllArgsConstructor
public class CourseRestrictionService {

    private CourseRestrictionRepository courseRestrictionRepository;

    private CourseRestrictionsTransformer courseRestrictionTransformer;

	private CourseRestrictions courseRestrictions;

    @SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(CourseRestrictionService.class);

    private static final String COURSE_RESTRICTION_ID = "courseRestrictionId";
	private static final String CREATE_USER = "createUser";
	private static final String CREATE_DATE = "createDate";

     /**
     * Get all course requirements in Course Restriction DTO
     *
     * @return Course 
     * @throws java.lang.Exception
     */

	 @Retry(name = "generalgetcall")
	 public List<CourseRestriction> getAllCourseRestrictionList() {
    	List<CourseRestriction> restrictionList = courseRestrictionTransformer.transformToDTO(courseRestrictionRepository.findAll());
    	if(!restrictionList.isEmpty()) {    		
    		Collections.sort(restrictionList, Comparator.comparing(CourseRestriction::getMainCourse)
    				.thenComparing(CourseRestriction::getMainCourseLevel,Comparator.nullsLast(String::compareTo)));	    	
    	}
    	return restrictionList;
    }

	@Retry(name = "generalgetcall")
    public CourseRestrictions getCourseRestrictions() {
    	List<CourseRestriction> restrictionList = courseRestrictionTransformer.transformToDTO(courseRestrictionRepository.findAll());
    	if(!restrictionList.isEmpty()) {
    		Collections.sort(restrictionList, Comparator.comparing(CourseRestriction::getMainCourse)
    				.thenComparing(CourseRestriction::getMainCourseLevel,Comparator.nullsLast(String::compareTo)));
    	}
    	courseRestrictions.setCourseRestrictionList(restrictionList);
        return courseRestrictions;
    }

	@Retry(name = "generalgetcall")
    public CourseRestrictions getCourseRestrictions(String courseCode, String courseLevel) {
        courseRestrictions.setCourseRestrictionList(
                courseRestrictionTransformer.transformToDTO(
                        courseRestrictionRepository.findByMainCourseAndMainCourseLevel(courseCode, courseLevel)));
        return courseRestrictions;
    }

	@Retry(name = "generalgetcall")
	public CourseRestrictions getCourseRestrictionsByMainCourseAndRestrictedCourse(String courseCode, String restrictedCourseCode) {
		courseRestrictions.setCourseRestrictionList(
				courseRestrictionTransformer.transformToDTO(
						courseRestrictionRepository.findByMainCourseAndRestrictedCourse(courseCode, restrictedCourseCode)));
		return courseRestrictions;
	}

	@Retry(name = "generalgetcall")
	public CourseRestriction getCourseRestriction(String mainCourseCode, String mainCourseLevel, String restrictedCourseCode, String restrictedCourseLevel) {
    	if (StringUtils.isBlank(mainCourseLevel)) {
			mainCourseLevel = " ";
		}
		if (StringUtils.isBlank(restrictedCourseLevel)) {
			restrictedCourseLevel = " ";
		}
		 Optional<CourseRestrictionsEntity> courseRestrictionOptional = courseRestrictionRepository
				.findByMainCourseAndMainCourseLevelAndRestrictedCourseAndRestrictedCourseLevel(mainCourseCode, mainCourseLevel, restrictedCourseCode, restrictedCourseLevel);
    	if (courseRestrictionOptional.isPresent()) {
    		return courseRestrictionTransformer.transformToDTO(courseRestrictionOptional.get());
		}
    	return null;
	}

	@Retry(name = "generalpostcall")
	public CourseRestriction saveCourseRestriction(CourseRestriction courseRestriction) {
    	Optional<CourseRestrictionsEntity> courseRestrictionOptional = courseRestriction.getCourseRestrictionId() != null? courseRestrictionRepository.findById(courseRestriction.getCourseRestrictionId()) :
			courseRestrictionRepository.findByMainCourseAndMainCourseLevelAndRestrictedCourseAndRestrictedCourseLevel(
				courseRestriction.getMainCourse(), courseRestriction.getMainCourseLevel(), courseRestriction.getRestrictedCourse(), courseRestriction.getRestrictedCourseLevel()
			);
		CourseRestrictionsEntity sourceObject = courseRestrictionTransformer.transformToEntity(courseRestriction);
		if (courseRestrictionOptional.isPresent()) {
			CourseRestrictionsEntity courseRestrictionsEntity = courseRestrictionOptional.get();
			BeanUtils.copyProperties(sourceObject, courseRestrictionsEntity, COURSE_RESTRICTION_ID, CREATE_USER, CREATE_DATE);
			return courseRestrictionTransformer.transformToDTO(courseRestrictionRepository.save(courseRestrictionsEntity));
		} else {
			sourceObject.setCourseRestrictionId(UUID.randomUUID());
			return courseRestrictionTransformer.transformToDTO(courseRestrictionRepository.save(sourceObject));
		}
	}

	@Retry(name = "generalgetcall")
	public List<CourseRestriction> getCourseRestrictionsSearchList(String mainCourseCode, String mainCourseLevel) {
		return courseRestrictionTransformer.transformToDTO(
		        courseRestrictionRepository.searchForCourseRestriction(
		                StringUtils.toRootUpperCase(StringUtils.strip(mainCourseCode, "*")),
                        StringUtils.toRootUpperCase(StringUtils.strip(mainCourseLevel, "*"))));
	}

	public CourseRestrictions getCourseRestrictionsListByCourses(CourseList courseList) {
		courseRestrictions.setCourseRestrictionList(
				courseRestrictionTransformer.transformToDTO(
						courseRestrictionRepository.findByMainCourseIn(courseList.getCourseCodes())));
        return courseRestrictions;
	}
}
