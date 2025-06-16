package ca.bc.gov.educ.api.course.constants;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum CourseRestrictionValidationIssueTypeCode {

    MAIN_COURSE_INVALID("mainCourse", "Invalid Course code/level - course code/level does not exist in the ministry course registry" , ValidationIssueSeverityCode.ERROR),
    RESTRICTED_COURSE_INVALID("restrictedCourse", "Invalid Course code/level - course code/level does not exist in the ministry course registry" , ValidationIssueSeverityCode.ERROR),
    MAIN_COURSE_EQUALS_RESTRICTED_COURSE("mainCourse", "Main Course and Restricted Course cannot be the same" , ValidationIssueSeverityCode.ERROR),
    RESTRICTION_START_DATE_INVALID("restrictionStartDate", "Restriction Start Date is earlier than the latest start date of the two courses" , ValidationIssueSeverityCode.ERROR),
    RESTRICTION_END_DATE_INVALID("restrictionEndDate", "Restriction End Date is later than the latest completion date of the two courses" , ValidationIssueSeverityCode.ERROR),
    RESTRICTION_END_DATE_RANGE_INVALID("restrictionEndDate", "Restriction End Date is before Restriction Start Date" , ValidationIssueSeverityCode.ERROR),
    RESTRICTION_NOT_FOUND("courseRestriction", "Course restriction does not exist" , ValidationIssueSeverityCode.ERROR),
    RESTRICTION_DUPLICATE("courseRestriction", "Course restriction already exists" , ValidationIssueSeverityCode.ERROR);

    private static final Map<String, CourseRestrictionValidationIssueTypeCode> CODE_MAP = new HashMap<>();

    static {
        for (CourseRestrictionValidationIssueTypeCode type : values()) {
            CODE_MAP.put(type.getCode(), type);
        }
    }

    /**
     * The Code.
     */
    @Getter
    private final String code;

    /**
     * Validation message
     */
    @Getter
    private final String message;

    @Getter
    private final ValidationIssueSeverityCode severityCode;

    /**
     * Instantiates a new course restriction validation issue type code.
     *
     * @param code the code
     */
    CourseRestrictionValidationIssueTypeCode(String code, String message, ValidationIssueSeverityCode severityCode) {
        this.code = code;
        this.message = message;
        this.severityCode = severityCode;
    }
    public static CourseRestrictionValidationIssueTypeCode findByValue(String value) {
        return CODE_MAP.get(value);
    }
}
