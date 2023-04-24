package ca.bc.gov.educ.api.course.service;

import java.util.*;

import ca.bc.gov.educ.api.course.model.dto.*;
import ca.bc.gov.educ.api.course.model.entity.CourseRequirementCodeEntity;
import ca.bc.gov.educ.api.course.repository.CourseRequirementCodeRepository;
import ca.bc.gov.educ.api.course.util.ThreadLocalStateUtil;
import ca.bc.gov.educ.api.course.util.criteria.CriteriaSpecification;
import io.github.resilience4j.retry.annotation.Retry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ca.bc.gov.educ.api.course.model.entity.CourseRequirementEntity;
import ca.bc.gov.educ.api.course.model.transformer.CourseRequirementTransformer;
import ca.bc.gov.educ.api.course.repository.CourseRequirementRepository;
import ca.bc.gov.educ.api.course.util.criteria.CriteriaHelper;
import ca.bc.gov.educ.api.course.util.criteria.GradCriteria.OperationEnum;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;

@Service
public class CourseRequirementService {

    @Autowired
    private CourseRequirementRepository courseRequirementRepository;

    @Autowired
    private CourseRequirementTransformer courseRequirementTransformer;

    @Autowired
    private CourseRequirementCodeRepository courseRequirementCodeRepository;
    
    @Autowired
    CourseRequirements courseRequirements;

    @Autowired
    CourseService courseService;

    @Autowired
    EducCourseApiConstants constants;
    
    @Autowired
    WebClient webClient;

    private static Logger logger = LoggerFactory.getLogger(CourseRequirementService.class);

    private static final String COURSE_REQUIREMENT_ID = "courseRequirementId";
    private static final String CREATE_USER = "createUser";
    private static final String CREATE_DATE = "createDate";

     /**
     * Get all course requirements in Course Requirement DTO
     * @param pageSize 
     * @param pageNo 
     *
     * @return Course 
     * @throws java.lang.Exception
     */

     @Retry(name = "generalgetcall")
     public List<AllCourseRequirements> getAllCourseRequirementList(Integer pageNo, Integer pageSize,String accessToken) {
        List<CourseRequirement> courseReqList  = new ArrayList<>();
        List<AllCourseRequirements> allCourseRequiremntList = new ArrayList<>();
        try {  
        	Pageable paging = PageRequest.of(pageNo, pageSize);        	 
            Page<CourseRequirementEntity> pagedResult = courseRequirementRepository.findAll(paging);        	
            courseReqList = courseRequirementTransformer.transformToDTO(pagedResult.getContent());
            courseReqList.forEach(cR -> {
            	AllCourseRequirements obj = new AllCourseRequirements();
            	BeanUtils.copyProperties(cR, obj);
                obj.setRuleCode(cR.getRuleCode().getCourseRequirementCode());
            	Course course = courseService.getCourseDetails(cR.getCourseCode(), cR.getCourseLevel());
        		if(course != null) {
        			obj.setCourseName(course.getCourseName());
        		}
            	List<GradRuleDetails> ruleList = webClient.get()
                        .uri(String.format(constants.getRuleDetailProgramManagementApiUrl(),cR.getRuleCode().getCourseRequirementCode()))
                        .headers(h -> {
                          h.setBearerAuth(accessToken);
                          h.set(EducCourseApiConstants.CORRELATION_ID, ThreadLocalStateUtil.getCorrelationID());
                        })
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<GradRuleDetails>>() {})
                        .block();
            	StringBuilder requirementProgram = getRequirementProgram(ruleList,obj);
            	
            	obj.setRequirementProgram(requirementProgram.toString());
            	allCourseRequiremntList.add(obj);
            });
            
        } catch (Exception e) {
            logger.debug("Exception: {0}", e);
        }

        return allCourseRequiremntList;
    }
    
    private StringBuilder getRequirementProgram(List<GradRuleDetails> ruleList, AllCourseRequirements obj) {
    	StringBuilder requirementProgram = new StringBuilder();
    	for(GradRuleDetails rL: ruleList) {
    		obj.setRequirementName(rL.getRequirementName());
    		if(rL.getProgramCode() != null) {
    			if(requirementProgram.length() == 0) {
    				requirementProgram.append(rL.getProgramCode());
    			}else {
    				requirementProgram.append("|");
    				requirementProgram.append(rL.getProgramCode());
    			}
    		}
    		if(rL.getOptionalProgramCode() != null) {
    			if(requirementProgram.length() == 0) {
    				requirementProgram.append(rL.getOptionalProgramCode());
    			}else {
    				requirementProgram.append("|");
    				requirementProgram.append(rL.getOptionalProgramCode());
    			}
    			
    		}
    	}
    	return requirementProgram;
    }
    
    /**
     * Get all course requirements in Course Requirement DTO by Rule
     * @param pageSize 
     * @param pageNo 
     *
     * @return Course 
     * @throws java.lang.Exception
     */
    @Retry(name = "generalgetcall")
    public List<CourseRequirement> getAllCourseRequirementListByRule(String rule,Integer pageNo, Integer pageSize) {
        List<CourseRequirement> courseReqList  = new ArrayList<>();

        try {  
        	Pageable paging = PageRequest.of(pageNo, pageSize);
        	Optional<CourseRequirementCodeEntity> ruleOptional = courseRequirementCodeRepository.findById(rule);
        	if (ruleOptional.isPresent()) {
                Page<CourseRequirementEntity> pagedResult = courseRequirementRepository.findByRuleCode(ruleOptional.get(), paging);
                courseReqList = courseRequirementTransformer.transformToDTO(pagedResult.getContent());
                courseReqList.forEach(cR -> {
                    Course course = courseService.getCourseDetails(cR.getCourseCode(),
                            cR.getCourseLevel().equalsIgnoreCase("") ? " " : cR.getCourseLevel());
                    if (course != null) {
                        cR.setCourseName(course.getCourseName());
                        cR.setStartDate(course.getStartDate());
                        cR.setEndDate(course.getEndDate());
                    }
                });
            }
        } catch (Exception e) {
            logger.debug("Exception: {0}", e);
        }

        return courseReqList;
    }

    @Retry(name = "generalgetcall")
	public CourseRequirements getCourseRequirements() {
        courseRequirements.setCourseRequirementList(
                courseRequirementTransformer.transformToDTO(courseRequirementRepository.findAll()));
        return courseRequirements;
    }

    @Retry(name = "generalgetcall")
    public CourseRequirements getCourseRequirements(String courseCode, String courseLevel) {
        courseRequirements.setCourseRequirementList(
                courseRequirementTransformer.transformToDTO(
                        courseRequirementRepository.findByCourseCodeAndCourseLevel(courseCode, courseLevel)));
        return courseRequirements;
    }

	public CourseRequirements getCourseRequirementListByCourses(CourseList courseList) {
		courseRequirements.setCourseRequirementList(
                courseRequirementTransformer.transformToDTO(
                        courseRequirementRepository.findByCourseCodeIn(courseList.getCourseCodes())));
        return courseRequirements;
	}

    @Retry(name = "searchcoursecall")
	public List<AllCourseRequirements> getCourseRequirementSearchList(String courseCode, String courseLevel, String rule,String accessToken) {
		CriteriaHelper criteria = new CriteriaHelper();
        getSearchCriteria("courseCode", courseCode, criteria);
        getSearchCriteria("courseLevel", courseLevel, criteria);
        getSearchCriteria("ruleCode.courseRequirementCode", rule, criteria);
        List<AllCourseRequirements> allCourseRequiremntList = new ArrayList<>();

        CriteriaSpecification<CourseRequirementEntity> spec = new CriteriaSpecification<>(criteria);
        List<CourseRequirement> courseReqList = courseRequirementTransformer.transformToDTO(courseRequirementRepository.findAll(Specification.where(spec)));
        if (!courseReqList.isEmpty()) {
        	courseReqList.forEach(cR -> {
        		AllCourseRequirements obj = new AllCourseRequirements();
            	BeanUtils.copyProperties(cR, obj);
                obj.setRuleCode(cR.getRuleCode().getCourseRequirementCode());
            	Course course = courseService.getCourseDetails(cR.getCourseCode(), cR.getCourseLevel());
        		if(course != null) {
        			obj.setCourseName(course.getCourseName());
        		}
            	List<GradRuleDetails> ruleList = webClient.get()
                        .uri(String.format(constants.getRuleDetailProgramManagementApiUrl(),cR.getRuleCode().getCourseRequirementCode()))
                        .headers(h -> {
                          h.setBearerAuth(accessToken);
                          h.set(EducCourseApiConstants.CORRELATION_ID, ThreadLocalStateUtil.getCorrelationID());
                        })
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<GradRuleDetails>>() {})
                        .block();
            	StringBuilder requirementProgram = getRequirementProgram(ruleList,obj);
            	obj.setTraxReqNumber(!ruleList.isEmpty()?ruleList.get(0).getTraxReqNumber():null);
            	obj.setRequirementProgram(requirementProgram.toString());
            	allCourseRequiremntList.add(obj);
            });
            Collections.sort(allCourseRequiremntList, Comparator.comparing(AllCourseRequirements::getCourseCode)
                    .thenComparing(AllCourseRequirements::getCourseLevel));
        }
        return allCourseRequiremntList;
	}
	
	private void getSearchCriteria(String rootElement, String value, CriteriaHelper criteria) {
        if (StringUtils.isNotBlank(value)) {
            if (StringUtils.contains(value, "*")) {
                criteria.add(rootElement, OperationEnum.STARTS_WITH_IGNORE_CASE, StringUtils.strip(value.toUpperCase(), "*"));
            } else {
                criteria.add(rootElement, OperationEnum.EQUALS, value.toUpperCase());
            }
        }
    }

    @Retry(name = "generalgetcall")
    public boolean checkCourseRequirementExists(String courseCode, String courseLevel, String ruleCode) {
        if (StringUtils.isBlank(courseLevel)) {
            courseLevel = " ";
        }
        return courseRequirementRepository.countByCourseCodeAndCourseLevelAndRuleCode(
                courseCode, courseLevel, ruleCode) > 0L;
    }

    @Retry(name = "generalpostcall")
    public CourseRequirement saveCourseRequirement(CourseRequirement courseRequirement) {
        if (StringUtils.isBlank(courseRequirement.getCourseLevel())) {
            courseRequirement.setCourseLevel(" ");
        }

        CourseRequirementEntity currentEntity = null;
        if (courseRequirement.getCourseRequirementId() != null) {
            Optional<CourseRequirementEntity> optional = courseRequirementRepository.findById(courseRequirement.getCourseRequirementId());
            if (optional.isPresent()) {
                currentEntity = optional.get();
            }
        }
        if (currentEntity == null) {
            List<CourseRequirementEntity> list = courseRequirementRepository.findByCourseCodeAndCourseLevelAndRuleCode(
                    courseRequirement.getCourseCode(), courseRequirement.getCourseLevel(), courseRequirement.getRuleCode().getCourseRequirementCode()
            );
            if (list != null && !list.isEmpty()) {
                currentEntity = list.get(0);
            }
        }

        CourseRequirementEntity sourceObject = courseRequirementTransformer.transformToEntity(courseRequirement);
        if (StringUtils.isBlank(sourceObject.getCourseLevel())) {
            sourceObject.setCourseLevel(" ");
        }
        Optional<CourseRequirementCodeEntity> optional = courseRequirementCodeRepository.findById(courseRequirement.getRuleCode().getCourseRequirementCode());
        if (optional.isPresent()) {
            sourceObject.setRuleCode(optional.get());
        }

        if (currentEntity != null) {
            BeanUtils.copyProperties(sourceObject, currentEntity, COURSE_REQUIREMENT_ID, CREATE_USER, CREATE_DATE);
            return courseRequirementTransformer.transformToDTO(courseRequirementRepository.save(currentEntity));
        } else {
            sourceObject.setCourseRequirementId(UUID.randomUUID());
            return courseRequirementTransformer.transformToDTO(courseRequirementRepository.save(sourceObject));
        }
    }

}
