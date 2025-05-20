package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.ExaminableCourse;
import ca.bc.gov.educ.api.course.model.entity.ExaminableCourseEntity;
import ca.bc.gov.educ.api.course.model.transformer.ExaminableCourseTransformer;
import ca.bc.gov.educ.api.course.repository.ExaminableCourseRepository;
import ca.bc.gov.educ.api.course.service.v2.CourseService;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
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
	ExaminableCourseTransformer examinableCourseTransformer;

	@MockBean
	@Qualifier("courseApiClient")
	public WebClient courseApiWebClient;

	@MockBean
	@Qualifier("gradCoregApiClient")
	public WebClient coregApiWebClient;

	@Test
	public void testGetAllExaminableCourses() {
		ExaminableCourseEntity ecEntity = new ExaminableCourseEntity();
		ecEntity.setExaminableCourseID(UUID.randomUUID());
		ecEntity.setCourseID("1234567");
		ecEntity.setExaminableStart(new Date(System.currentTimeMillis()).toLocalDate());
		ecEntity.setOptionalStart(new Date(System.currentTimeMillis()).toLocalDate());

		ExaminableCourse ec = new ExaminableCourse();
		ec.setCourseID("1234567");
		ec.setExaminableCourseID(ecEntity.getExaminableCourseID());
		ec.setExaminableStart(String.valueOf(ecEntity.getExaminableStart()));
		ec.setOptionalStart(String.valueOf(ecEntity.getOptionalStart()));

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