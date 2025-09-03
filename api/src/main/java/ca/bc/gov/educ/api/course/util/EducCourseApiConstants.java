package ca.bc.gov.educ.api.course.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Component
@Getter
@Setter
public class EducCourseApiConstants {

    private EducCourseApiConstants() {}

    public static final String CORRELATION_ID = "correlationID";
    public static final String USER_NAME = "User-Name";
    public static final String REQUEST_SOURCE = "Request-Source";
    public static final String API_NAME = "EDUC-GRAD-COURSE-API";

    //API end-point Mapping constants
    public static final String API_ROOT_MAPPING = "";
    public static final String API_VERSION_V1 = "v1";
    public static final String API_VERSION_V2 = "v2";

    // API Root Mapping
    public static final String GRAD_COURSE_API_ROOT_MAPPING = "/api/" + API_VERSION_V1+"/course";
    public static final String GRAD_COURSE_API_ROOT_MAPPING_V2 = "/api/" + API_VERSION_V2+"/course";

    // Controller Mappings
    public static final String GRAD_COURSE_URL_MAPPING = GRAD_COURSE_API_ROOT_MAPPING;
    public static final String STUDENT_COURSE_URL_MAPPING = GRAD_COURSE_API_ROOT_MAPPING + "/studentcourse";
    public static final String STUDENT_EXAM_URL_MAPPING = GRAD_COURSE_API_ROOT_MAPPING + "/studentexam";
    public static final String COURSE_ALGORITHM_URL_MAPPING = GRAD_COURSE_API_ROOT_MAPPING + "/course-algorithm";

    //Examinable Courses
    public static final String GET_ALL_EXAMINABLE_COURSES = "/examinablecourses";

    // Service Method Mappings
    public static final String GET_STUDENT_COURSE_BY_PEN_MAPPING = "/pen/{pen}";
    public static final String GET_STUDENT_EXAM_BY_PEN_MAPPING = "/pen/{pen}";

    // Controller Mappings - Version 2
    public static final String GRAD_COURSE_URL_MAPPING_V2 = GRAD_COURSE_API_ROOT_MAPPING_V2;
    public static final String STUDENT_COURSE_URL_MAPPING_V2 = GRAD_COURSE_API_ROOT_MAPPING_V2 + "/studentcourse";
    public static final String COURSE_ALGORITHM_URL_MAPPING_V2 = GRAD_COURSE_API_ROOT_MAPPING_V2 + "/course-algorithm";

    // Service Method Mappings - Version 2
    public static final String GET_STUDENT_COURSES_BY_STUDENT_ID_MAPPING = "/studentid/{studentID}";
    public static final String STUDENT_COURSE_ID_MAPPING = "{studentCourseID}";
    public static final String GET_COURSE_BY_COURSE_ID_MAPPING ="/{courseID}";
    public static final String GET_COURSE_BY_SEARCH_MAPPING = "/search";


    public static final String GET_COURSE_BY_SEARCH_PARAMS_MAPPING = "/coursesearch";
    public static final String GET_COURSE_DETAILS_BY_CODE_MAPPING = "/{courseCode}";
    public static final String GET_COURSE_BY_CODE_MAPPING="/{courseCode}/level/{courseLevel}";
    public static final String GET_COURSE_REQUIREMENT_MAPPING = "/requirement";
    public static final String GET_COURSE_REQUIREMENT_BY_COURSE_LIST_MAPPING = "/course-requirement/course-list";
    public static final String GET_COURSE_REQUIREMENT_BY_RULE_MAPPING = "/requirement/rule";
    public static final String GET_COURSE_REQUIREMENT_BY_CODE_AND_LEVEL_MAPPING = "/course-requirement";
    public static final String GET_COURSE_REQUIREMENTS_BY_SEARCH_PARAMS_MAPPING = "/courserequirementsearch";
    public static final String SAVE_COURSE_REQUIREMENT = "/save-course-requirement";
    public static final String GET_COURSE_RESTRICTION_MAPPING = "/restriction";
    public static final String GET_COURSE_RESTRICTION_BY_SEARCH_PARAMS_MAPPING = "/courserestrictionsearch";
    public static final String GET_COURSE_RESTRICTIONS_BY_CODE_AND_LEVEL_MAPPING = "/course-restriction";
    public static final String GET_COURSE_RESTRICTIONS_BY_COURSE_LIST_MAPPING = "/course-restriction/course-list";
    public static final String GET_COURSE_RESTRICTION_BY_CODE_AND_LEVEL_AND_RESTRICTED_CODE_AND_LEVEL_MAPPING = "/get-course-restriction";
    public static final String SAVE_COURSE_RESTRICTION = "/save-course-restriction";
    public static final String UPDATE_COURSE_RESTRICTION = "/save-course-restriction/{courseRestrictionId}";
    public static final String CHECK_COURSE_REQUIREMENT_EXISTENCE = "/check-course-requirement";
    public static final String CHECK_FRENCH_IMMERSION_COURSE = "/check-french-immersion-course/pen/{pen}";
    public static final String CHECK_FRENCH_IMMERSION_COURSE_BY_PEN_AND_LEVEL_MAPPING = "/check-french-immersion-course";
    public static final String CHECK_FRENCH_IMMERSION_COURSE_FOR_EN_BY_PEN_AND_LEVEL_MAPPING = "/check-french-immersion-course-for-en";
    public static final String CHECK_BLANK_LANGUAGE_COURSE_BY_CODE_AND_LEVEL_MAPPING = "/check-blank-language-course";
    public static final String CHECK_FRENCH_LANGUAGE_COURSE_BY_CODE_AND_LEVEL_MAPPING = "/check-french-language-course";
    public static final String EQUIVALENT_OR_CHALLENGE_CODES_MAPPING = "/equivalentOrChallengeCodes";
    public static final String EQUIVALENT_OR_CHALLENGE_CODE_MAPPING = "/equivalentOrChallengeCodes/{equivalentOrChallengeCode}";
    public static final String EXAM_SPECIAL_CASE_CODES_MAPPING = "/examSpecialCaseCodes";
    public static final String EXAM_SPECIAL_CASE_CODE_MAPPING = "/examSpecialCaseCodes/{examSpecialCaseCode}";
    public static final String FINE_ART_APPLIED_SKILLS_CODES_MAPPING = "/fineArtsAppliedSkillsCodes";
    public static final String FINE_ART_APPLIED_SKILLS_CODE_MAPPING = "/fineArtsAppliedSkillsCodes/{fineArtsAppliedSkillsCode}";


    public static final String GET_COURSE_ALGORITHM_DATA_BY_PEN_MAPPING = "/pen/{pen}";

    public static final String EVENT_HISTORY_MAPPING_V1 = GRAD_COURSE_API_ROOT_MAPPING + "/event/history";

    //Attribute Constants
    public static final String COURSE_ID_ATTRIBUTE = "courseID";
    public static final String STUDENT_COURSE_ID_ATTRIBUTE = "studentCourseID";

    //Default Attribute value constants
    public static final String DEFAULT_CREATED_BY = "API_COURSE";
    public static final String DEFAULT_UPDATED_BY = "API_COURSE";

    //Default Date format constants
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String TRAX_DATE_FORMAT = "yyyyMM";
    public static final DateFormat DEFAULT_DATE_FORMAT_INSTANCE = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

    // Messaging
    public static final String GRAD_COURSE_API = "GRAD-COURSE-API";
    public static final String STREAM_NAME = "GRAD_COURSE_EVENTS";
    public static final String EVENTS_TOPIC_DURABLE = "GRAD-COURSE-API-COURSES-EVENTS-TOPIC-DURABLE";

    public static final String COREG_STREAM_NAME = "COREG_EVENTS";
    public static final String COREG_EVENTS_TOPIC_DURABLE = "GRAD-COURSE-API-COREG-EVENTS-TOPIC-DURABLE";

    //Endpoints
    @Value("${endpoint.grad-program-api.rule-detail.url}")
    private String ruleDetailProgramManagementApiUrl;

    @Value("${endpoint.coreg-api.course-info-by-id.url}")
    private String courseDetailByCourseIdUrl;

    @Value("${endpoint.coreg-api.course-info-by-external-code.url}")
    private String courseDetailByExternalCodeUrl;

    @Value("${endpoint.coreg-api.course-info-search.url}")
    private String courseDetailSearchUrl;

    // Splunk LogHelper Enabled
    @Value("${splunk.log-helper.enabled}")
    private boolean splunkLogHelperEnabled;

    // NATS
    @Value("${nats.url}")
    private String natsUrl;

    @Value("${nats.maxReconnect}")
    private Integer natsMaxReconnect;

    @Value("${endpoint.keycloak.token-uri}")
    private String tokenUrl;

    @Value("${coreg.searchUrl}")
    private String coregSearchUrl;

    @Value("${coreg.shouldConsumeEventsFlag}")
    private boolean coregShouldConsumeEventsFlag;
}
