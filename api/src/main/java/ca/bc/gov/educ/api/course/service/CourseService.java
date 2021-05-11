package ca.bc.gov.educ.api.course.service;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.entity.CourseEntity;
import ca.bc.gov.educ.api.course.model.entity.CourseId;
import ca.bc.gov.educ.api.course.model.transformer.CourseTransformer;
import ca.bc.gov.educ.api.course.repository.CourseRepository;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepo;

    Iterable<CourseEntity> courseEntities;

    @Autowired
    private CourseTransformer courseTransformer;

    private static Logger logger = LoggerFactory.getLogger(CourseService.class);

     /**
     * Get all courses in Course DTO
     * @param pageSize 
     * @param pageNo 
     *
     * @return Course 
     * @throws java.lang.Exception
     */
    public List<Course> getCourseList(Integer pageNo, Integer pageSize) {
        List<Course> course  = new ArrayList<Course>();

        try {  
        	//Pageable paging = PageRequest.of(pageNo, pageSize);        	 
           // Page<CourseEntity> pagedResult = courseRepo.findAll(paging);        	
        	course = courseTransformer.transformToDTO(courseRepo.findAll()); 
        } catch (Exception e) {
            logger.debug("Exception:" + e);
        }

        return course;
    }

	public Course getCourseDetails(String crseCode, String crseLvl) {
		CourseId key = new CourseId();
		key.setCourseCode(crseCode);
		key.setCourseLevel(crseLvl);
		return courseTransformer.transformToDTO(courseRepo.findByCourseKey(key));
	}

	public List<Course> getCourseSearchList(String courseCode, String courseLevel, String courseName) {
		return courseTransformer.transformToDTO(
		        courseRepo.searchForCourse(
		                StringUtils.toRootUpperCase(StringUtils.strip(courseCode, "*")),
                        StringUtils.toRootUpperCase(StringUtils.strip(courseLevel, "*")),
                        StringUtils.toRootUpperCase(StringUtils.strip(courseName, "*"))));
	}
}
