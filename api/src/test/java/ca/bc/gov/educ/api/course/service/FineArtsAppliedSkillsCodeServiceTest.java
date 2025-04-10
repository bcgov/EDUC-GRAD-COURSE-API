package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.course.model.entity.FineArtsAppliedSkillsCodeEntity;
import ca.bc.gov.educ.api.course.repository.FineArtsAppliedSkillsCodeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FineArtsAppliedSkillsCodeServiceTest {

	@Autowired
	private FineArtsAppliedSkillsCodeService fineArtsAppliedSkillsCodeService;

	@MockBean
	private FineArtsAppliedSkillsCodeRepository fineArtsAppliedSkillsCodeRepository;

	@MockBean
	public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

	@MockBean
	public OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

	@MockBean
	public ClientRegistrationRepository clientRegistrationRepository;

	@MockBean
	@Qualifier("default")
	public WebClient webClient;
	
	@Test
	public void testGetFineArtsAppliedSkillsCodeList() {
		List<FineArtsAppliedSkillsCodeEntity> fineArtsAppliedSkillsCodes = new ArrayList<>();
		FineArtsAppliedSkillsCodeEntity obj = new FineArtsAppliedSkillsCodeEntity();
		obj.setFineArtsAppliedSkillsCode("fineArtsAppliedSkillsCode");
		obj.setDescription("FineArtsAppliedSkillsCode Description");
		obj.setCreateUser("GRADUATION");
		obj.setUpdateUser("GRADUATION");
		obj.setCreateDate(new Date(System.currentTimeMillis()));
		obj.setUpdateDate(new Date(System.currentTimeMillis()));
		fineArtsAppliedSkillsCodes.add(obj);
		Mockito.when(fineArtsAppliedSkillsCodeRepository.findAll()).thenReturn(fineArtsAppliedSkillsCodes);
		var result = fineArtsAppliedSkillsCodeService.getFineArtsAppliedSkillsCodeList();
		assertNotNull(result);
		assertThat(result).isNotEmpty();
	}
	
	@Test
	public void testGetFineArtsAppliedSkillsCode() {
		FineArtsAppliedSkillsCodeEntity obj = new FineArtsAppliedSkillsCodeEntity();
		obj.setFineArtsAppliedSkillsCode("fineArtsAppliedSkillsCode");
		obj.setDescription("FineArtsAppliedSkillsCode Description");
		obj.setCreateUser("GRADUATION");
		obj.setUpdateUser("GRADUATION");
		obj.setCreateDate(new Date(System.currentTimeMillis()));
		obj.setUpdateDate(new Date(System.currentTimeMillis()));
		Mockito.when(fineArtsAppliedSkillsCodeRepository.findById("fineArtsAppliedSkillsCode")).thenReturn(Optional.of(obj));
		var result = fineArtsAppliedSkillsCodeService.getFineArtsAppliedSkillsCode("fineArtsAppliedSkillsCode");
		assertNotNull(result);
	}

	@Test(expected= EntityNotFoundException.class)
	public void testGetFineArtsAppliedSkillsCode_noContent() {
		Mockito.when(fineArtsAppliedSkillsCodeRepository.findById("fineArtsAppliedSkillsCode")).thenReturn(Optional.empty());
		fineArtsAppliedSkillsCodeService.getFineArtsAppliedSkillsCode("fineArtsAppliedSkillsCode");
	}
}
