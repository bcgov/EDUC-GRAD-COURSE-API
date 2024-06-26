package ca.bc.gov.educ.api.course.util;

public class PermissionsConstants {

	private PermissionsConstants() {}

	public static final String PREFIX = "hasAuthority('";
	public static final String SUFFIX = "')";

	public static final String READ_GRAD_COURSE = PREFIX + "SCOPE_READ_GRAD_COURSE_DATA" + SUFFIX
			+ " and " + PREFIX + "SCOPE_READ_GRAD_STUDENT_COURSE_DATA" + SUFFIX;
	public static final String READ_GRAD_COURSE_REQUIREMENT = PREFIX + "SCOPE_READ_GRAD_COURSE_REQUIREMENT_DATA" + SUFFIX;
	public static final String READ_GRAD_COURSE_RESTRICTION = PREFIX + "SCOPE_READ_GRAD_COURSE_RESTRICTION_DATA" + SUFFIX;
	public static final String UPDATE_GRAD_COURSE_RESTRICTION = PREFIX + "SCOPE_UPDATE_GRAD_COURSE_RESTRICTION_DATA" + SUFFIX;
	public static final String READ_GRAD_STUDENT_EXAM = PREFIX + "SCOPE_READ_GRAD_STUDENT_EXAM_DATA" + SUFFIX;
	public static final String READ_EQUIVALENT_OR_CHALLENGE_CODE = PREFIX + "SCOPE_READ_EQUIVALENT_OR_CHALLENGE_CODE" + SUFFIX;
	public static final String READ_EXAM_SPECIAL_CASE_CODE = PREFIX + "SCOPE_READ_EXAM_SPECIAL_CASE_CODE" + SUFFIX;
	public static final String READ_FINE_ART_APPLIED_SKILLS_CODE = PREFIX + "SCOPE_READ_FINE_ART_APPLIED_SKILLS_CODE" + SUFFIX;
}
