package ca.bc.gov.educ.api.course.controller;

import ca.bc.gov.educ.api.course.model.dto.ExaminableCourse;
import ca.bc.gov.educ.api.course.service.ExaminableCourseService;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class ExaminableCourseControllerTest {

	@Mock
	private ExaminableCourseService examinableCourseService;
	
	@InjectMocks
	private ExaminableCourseController examinableCourseController;
	
	@Test
	public void testGetAllExaminableCourses() {
		List<ExaminableCourse> ecList = new ArrayList<>();
		ExaminableCourse obj = new ExaminableCourse();
		obj.setExaminableCourseID(UUID.randomUUID());
		obj.setCourseCode("GRADUATION");
		obj.setCourseLevel("12");
		obj.setCreateDate(new Date(System.currentTimeMillis()));
		obj.setUpdateDate(new Date(System.currentTimeMillis()));
		ecList.add(obj);
		Mockito.when(examinableCourseService.getAllExaminableCourses()).thenReturn(ecList);
		examinableCourseController.getAllExaminableCourses();
		Mockito.verify(examinableCourseService).getAllExaminableCourses();
	}

}
