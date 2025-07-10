package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.exception.ServiceException;
import ca.bc.gov.educ.api.course.model.dto.ExaminableCourse;
import ca.bc.gov.educ.api.course.model.entity.ExaminableCourseEntity;
import ca.bc.gov.educ.api.course.model.transformer.ExaminableCourseTransformer;
import ca.bc.gov.educ.api.course.repository.ExaminableCourseRepository;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
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
		ecEntity.setCourseCode("MAT");
		ecEntity.setCourseLevel("12");
		ecEntity.setCourseTitle("Mathematics 12");
		ecEntity.setSchoolWeightPercent(81.0);
		ecEntity.setExamWeightPercent(35.5);
		ecEntity.setExaminableStart("2023-01");
		ecEntity.setExamWeightPercentPre1989(35.5);
		ecEntity.setSchoolWeightPercentPre1989(81.0);


		ExaminableCourse ec = new ExaminableCourse();
		ec.setExaminableCourseID(ecEntity.getExaminableCourseID());
		ec.setCourseCode(ecEntity.getCourseCode());
		ec.setCourseLevel(ecEntity.getCourseLevel());
		ec.setCourseTitle(ecEntity.getCourseTitle());
		ec.setSchoolWeightPercent(ecEntity.getSchoolWeightPercent());
		ec.setExamWeightPercent(ecEntity.getExamWeightPercent());
		ec.setExaminableStart(ecEntity.getExaminableStart());
		ec.setExamWeightPercentPre1989(35.5);
		ec.setSchoolWeightPercentPre1989(81.0);

		when(examinableCourseTransformer.transformToDTO(Arrays.asList(ecEntity)))
				.thenReturn(Arrays.asList(ec));
		when(examinableCourseRepository.findAll()).thenReturn(Arrays.asList(ecEntity));
		var result = examinableCourseService.getAllExaminableCourses();
		assertThat(result).isNotNull().hasSize(1);
		ExaminableCourse ecDetails = result.get(0);
		assertThat(ecDetails.getExaminableCourseID()).isEqualTo(ecEntity.getExaminableCourseID());
		assertThat(ecDetails.getCourseTitle()).isEqualTo(ecEntity.getCourseTitle());
	}

	@Test
	public void testGetAllExaminableCourses_TransformerReturnsNullOREmpty() {
		when(examinableCourseRepository.findAll()).thenReturn(Arrays.asList(new ExaminableCourseEntity()));
		var result = examinableCourseService.getAllExaminableCourses();
		when(examinableCourseTransformer.transformToDTO(anyList())).thenReturn(null);
		assertThat(result).isNotNull().isEmpty();

		when(examinableCourseTransformer.transformToDTO(anyList())).thenReturn(Collections.emptyList());
		assertThat(result).isNotNull().isEmpty();
	}

	@Test
	public void shouldThrowServiceExceptionWhenRepositoryFails() {
		when(examinableCourseRepository.findAll()).thenThrow(new RuntimeException());

		assertThatThrownBy(examinableCourseService::getAllExaminableCourses)
				.isInstanceOf(ServiceException.class);
	}
}