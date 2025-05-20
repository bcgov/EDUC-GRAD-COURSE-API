package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.ExaminableCourse;
import ca.bc.gov.educ.api.course.model.entity.ExaminableCourseEntity;
import ca.bc.gov.educ.api.course.model.transformer.ExaminableCourseTransformer;
import ca.bc.gov.educ.api.course.repository.ExaminableCourseRepository;
import ca.bc.gov.educ.api.course.service.v2.CourseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ExaminableCourseServiceTest {

	@Autowired
	private ExaminableCourseService examinableCourseService;

	@MockBean
	private ExaminableCourseRepository examinableCourseRepository;

	@MockBean
	CourseService courseServiceV2;

	@MockBean
	ExaminableCourseTransformer examinableCourseTransformer;

	@MockBean
	@Qualifier("courseApiClient")
	public WebClient courseApiWebClient;
    @Autowired
    private ExaminableCourse examinableCourses;

	@Test
	public void testGetAllExaminableCourses() {
		Course course = new Course();
		course.setCourseID("1234567");
		course.setCourseCode("CH");
		course.setCourseLevel("12");

		List<ExaminableCourseEntity> ecEntities = new ArrayList<>();
		ExaminableCourseEntity ecEntity = new ExaminableCourseEntity();
		ecEntity.setExaminableCourseID(UUID.randomUUID());
		ecEntity.setCourseID("1234567");
		ecEntity.setCreateDate(new Date(System.currentTimeMillis()));
		ecEntity.setUpdateDate(new Date(System.currentTimeMillis()));
		ecEntities.add(ecEntity);
		when(courseServiceV2.getCourseInfo(course.getCourseID())).thenReturn(course);
		Mockito.when(examinableCourseRepository.findAll()).thenReturn(ecEntities);
		var result = examinableCourseService.getAllExaminableCourses();
		assertNotNull(result);
	}
}
