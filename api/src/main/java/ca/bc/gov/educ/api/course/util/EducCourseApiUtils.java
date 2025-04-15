package ca.bc.gov.educ.api.course.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.coreg.CourseAllowableCredits;
import ca.bc.gov.educ.api.course.model.dto.coreg.CourseCode;
import ca.bc.gov.educ.api.course.model.dto.coreg.Courses;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpHeaders;

public class EducCourseApiUtils {

    private EducCourseApiUtils() {

    }

    public static String formatDate (Date date) {
        if (date == null)
            return null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(EducCourseApiConstants.DEFAULT_DATE_FORMAT);
        return simpleDateFormat.format(date);
    }

    public static String formatDate (Date date, String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(date);
    }

    public static Date parseDate (String dateString) {
        if (dateString == null || "".compareTo(dateString) == 0)
            return null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(EducCourseApiConstants.DEFAULT_DATE_FORMAT);
        Date date = new Date();

        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static Date parseDate (String dateString, String dateFormat) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Date date = new Date();

        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
    
    public static String parseDateFromString (String sessionDate) {
        if (sessionDate == null)
            return null;
        return parseDateByFormat(sessionDate, EducCourseApiConstants.DEFAULT_DATE_FORMAT);
    }
    
    public static String parseTraxDate (String sessionDate) {
        if (sessionDate == null)
            return null;
        return parseDateByFormat(sessionDate, EducCourseApiConstants.TRAX_DATE_FORMAT);
    }

    private static String parseDateByFormat(final String sessionDate, final String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try {
            Date date = simpleDateFormat.parse(sessionDate);
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return localDate.getYear() +"/"+ String.format("%02d", localDate.getMonthValue());

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

	public static HttpHeaders getHeaders (String accessToken)
    {
		HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.setBearerAuth(accessToken);
        return httpHeaders;
    }

    /**
     *
     * @param courseCode    (5 chars)
     * @param courseLevel   (2 or 3 chars)
     * @return externalCode (7 or 8 chars)
     */
    public static String getExternalCodeByCourseCodeAndLevel(String courseCode, String courseLevel) {
        if (StringUtils.isBlank(courseCode) || StringUtils.isBlank(courseLevel)) {
            return "";
        }
        String externalCode = StringUtils.rightPad(courseCode, 5, " ") + courseLevel;
        if (externalCode.length() > 7) {
            return StringUtils.substring(externalCode, 0, 8);
        }
        return externalCode;
    }

    /**
     *
     * @param externalCode (7 or 8 chars)
     * @return Pair of courseCode(5 chars) & courseLevel(2 or 3 chars)
     */
    public static Pair<String, String> getCourseCodeAndLevelByExternalCode(String externalCode) {
        if (StringUtils.isBlank(externalCode) ||
            (externalCode.length() != 7 && externalCode.length() != 8)) {
            return Pair.of("", "");
        }

        String courseCode = StringUtils.substring(externalCode, 0, 5);
        String courseLevel = StringUtils.substring(externalCode, 5);

        return Pair.of(courseCode.trim(), courseLevel.trim());
    }

    public static Course convertCoregCourseIntoGradCourse(Courses source) {
        Course course = new Course();
        course.setCourseID(source.getCourseID());
        course.setCourseName(source.getCourseTitle());
        course.setGenericCourseType("N/A"); // Setting generic course type later
        course.setLanguage(source.getCourseCharacteristics().getDescription());
        course.setStartDate(DateUtils.toSqlDate(source.getStartDate()));
        course.setEndDate(DateUtils.toSqlDate(source.getEndDate()));
        // Number of Credits
        if (!source.getCourseAllowableCredit().isEmpty()) {
            course.setNumCredits(getMaxCreditValue(source.getCourseAllowableCredit()));
        }
        // Course Code & Course Level
        if (!source.getCourseCode().isEmpty()) {
            Optional<CourseCode> courseCodeOptional = source.getCourseCode().stream().filter(cc -> "39".equalsIgnoreCase(cc.getOriginatingSystem())).findAny();
            Pair<String, String> res;
            if (courseCodeOptional.isPresent()) {
                res = EducCourseApiUtils.getCourseCodeAndLevelByExternalCode(courseCodeOptional.get().getExternalCode());
                course.setCourseCode(res.getLeft().trim());
                course.setCourseLevel(res.getRight().trim());
            }
        }
        return course;
    }

    private static Integer getMaxCreditValue(List<CourseAllowableCredits> courseAllowableCredits) {
        if (courseAllowableCredits == null || courseAllowableCredits.isEmpty()) {
            return 0;
        }
        List<Integer> credits = courseAllowableCredits.stream()
                .map(cc -> Integer.parseInt(NumberUtils.isCreatable(cc.getCreditValue())? cc.getCreditValue() : "0")).toList();
        Optional<Integer> max = credits.stream().max(Integer::compareTo);
        return max.orElse(0);
    }
}
