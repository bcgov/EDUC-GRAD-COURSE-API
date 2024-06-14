package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.entity.ExamSpecialCaseCodeEntity;
import ca.bc.gov.educ.api.course.repository.ExamSpecialCaseCodeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ExamSpecialCaseCodeServiceTest {

	@Autowired
	private ExamSpecialCaseCodeService examSpecialCaseCodeService;

	@MockBean
	private ExamSpecialCaseCodeRepository examSpecialCaseCodeRepository;
	
	@Test
	public void testGetExamSpecialCaseCodeList() {
		List<ExamSpecialCaseCodeEntity> examSpecialCaseCodes = new ArrayList<>();
		ExamSpecialCaseCodeEntity obj = new ExamSpecialCaseCodeEntity();
		obj.setExamSpecialCaseCode("examSpecialCaseCode");
		obj.setDescription("ExamSpecialCaseCode Description");
		obj.setCreateUser("GRADUATION");
		obj.setUpdateUser("GRADUATION");
		obj.setCreateDate(new Date(System.currentTimeMillis()));
		obj.setUpdateDate(new Date(System.currentTimeMillis()));
		examSpecialCaseCodes.add(obj);
		Mockito.when(examSpecialCaseCodeRepository.findAll()).thenReturn(examSpecialCaseCodes);
		var result = examSpecialCaseCodeService.getExamSpecialCaseCodeList();
		assertNotNull(result);
		assertThat(result).isNotEmpty();
	}
	
	@Test
	public void testGetExamSpecialCaseCode() {
		ExamSpecialCaseCodeEntity obj = new ExamSpecialCaseCodeEntity();
		obj.setExamSpecialCaseCode("examSpecialCaseCode");
		obj.setDescription("ExamSpecialCaseCode Description");
		obj.setCreateUser("GRADUATION");
		obj.setUpdateUser("GRADUATION");
		obj.setCreateDate(new Date(System.currentTimeMillis()));
		obj.setUpdateDate(new Date(System.currentTimeMillis()));
		Mockito.when(examSpecialCaseCodeRepository.findById("examSpecialCaseCode")).thenReturn(Optional.of(obj));
		var result = examSpecialCaseCodeService.getExamSpecialCaseCode("examSpecialCaseCode");
		assertNotNull(result);
	}
	
	@Test
	public void testGetExamSpecialCaseCode_noContent() {
		Mockito.when(examSpecialCaseCodeRepository.findById("examSpecialCaseCode")).thenReturn(Optional.empty());
		var result = examSpecialCaseCodeService.getExamSpecialCaseCode("examSpecialCaseCode");
		assertNull(result);
	}
}
