package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.ExaminableCourse;
import ca.bc.gov.educ.api.course.model.dto.coreg.CourseAllowableCredits;
import ca.bc.gov.educ.api.course.model.dto.coreg.CourseCharacteristics;
import ca.bc.gov.educ.api.course.model.dto.coreg.CourseCode;
import ca.bc.gov.educ.api.course.model.dto.coreg.Courses;
import ca.bc.gov.educ.api.course.model.entity.ExaminableCourseEntity;
import ca.bc.gov.educ.api.course.model.transformer.ExaminableCourseTransformer;
import ca.bc.gov.educ.api.course.repository.CourseRepository;
import ca.bc.gov.educ.api.course.repository.ExaminableCourseRepository;
import ca.bc.gov.educ.api.course.service.v2.CourseService;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import ca.bc.gov.educ.api.course.util.EducCourseApiUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ExaminableCourseServiceTest {

	@Autowired
	private ExaminableCourseService examinableCourseService;

	@Autowired
	EducCourseApiConstants constants;

	@MockBean
	private ExaminableCourseRepository examinableCourseRepository;

	@MockBean
	CourseService courseService;

	@MockBean
	RESTService restService;

	@MockBean
	ExaminableCourseTransformer examinableCourseTransformer;

	@MockBean
	@Qualifier("courseApiClient")
	public WebClient courseApiWebClient;

	@MockBean
	@Qualifier("gradCoregApiClient")
	public WebClient coregApiWebClient;


	@Autowired
    private ExaminableCourse examinableCourses;

	@Test
	public void testGetAllExaminableCourses() {
		// Course
		Course course = new Course();
		course.setCourseID("1234567");
		course.setCourseCode("CH");
		course.setCourseLevel("12");
		course.setCourseName("Test Course Name");

		ExaminableCourseEntity ecEntity = new ExaminableCourseEntity();
		ecEntity.setExaminableCourseID(UUID.randomUUID());
		ecEntity.setCourseID("1234567");
		ecEntity.setExaminableStart(new Date(System.currentTimeMillis()));
		ecEntity.setOptionalStart(new Date(System.currentTimeMillis()));

		ExaminableCourse ec = new ExaminableCourse();
		ec.setCourseID("1234567");
		ec.setExaminableCourseID(ecEntity.getExaminableCourseID());
		ec.setExaminableStart(String.valueOf(ecEntity.getExaminableStart()));
		ec.setOptionalStart(String.valueOf(ecEntity.getOptionalStart()));

		when(courseService.getCourseInfo(course.getCourseID())).thenReturn(course);
		when(examinableCourseTransformer.transformToDTO(Arrays.asList(ecEntity)))
				.thenReturn(Arrays.asList(ec));
		when(examinableCourseRepository.findAll()).thenReturn(Arrays.asList(ecEntity));
		var result = examinableCourseService.getAllExaminableCourses();
		assertThat(result).isNotNull().hasSize(1);
		ExaminableCourse ecDetails = result.get(0);
		assertThat(ecDetails.getCourseID()).isEqualTo(ecEntity.getCourseID());
		assertThat(ecDetails.getExaminableCourseID()).isEqualTo(ecEntity.getExaminableCourseID());
	}
}