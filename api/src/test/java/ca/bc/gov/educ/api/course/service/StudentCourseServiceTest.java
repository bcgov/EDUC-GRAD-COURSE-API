package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.StudentCourse;
import ca.bc.gov.educ.api.course.model.dto.StudentExam;
import ca.bc.gov.educ.api.course.model.entity.StudentCourseEntity;
import ca.bc.gov.educ.api.course.model.entity.StudentExamEntity;
import ca.bc.gov.educ.api.course.model.transformer.StudentCourseTransformer;
import ca.bc.gov.educ.api.course.model.transformer.StudentExamTransformer;
import ca.bc.gov.educ.api.course.repository.StudentCourseRepository;
import ca.bc.gov.educ.api.course.repository.StudentExamRepository;
import ca.bc.gov.educ.api.course.service.v2.CourseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class StudentCourseServiceTest {

    @Autowired
    StudentCourseService studentCourseService;

    @MockBean
    StudentCourseRepository studentCourseRepository;

    @MockBean
    StudentExamRepository studentExamRepository;

    @Autowired
    StudentCourseTransformer studentCourseTransformer;

    @Autowired
    StudentExamTransformer studentExamTransformer;

    @MockBean
    CourseService courseServiceV2;

    @MockBean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    @MockBean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @MockBean
    public ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    public WebClient webClient;

    @Test
    public void testGetStudentCourses() {
        Course course = new Course();
        course.setCourseID("1234567");
        course.setCourseCode("CH");
        course.setCourseLevel("12");
        course.setCourseName("Test Course Name");

        StudentCourse sc = new StudentCourse();
        sc.setStudentID(UUID.randomUUID());
        sc.setCourseID(course.getCourseID());
        sc.setCourseCode(course.getCourseCode());
        sc.setCourseLevel(course.getCourseLevel());

        StudentCourseEntity scEntity1 = studentCourseTransformer.transformToEntity(sc);

        when(courseServiceV2.getCourseInfo(course.getCourseCode(), course.getCourseLevel())).thenReturn(course);
        when(courseServiceV2.getCourseInfo(course.getCourseID())).thenReturn(course);
        when(studentCourseRepository.findByStudentID(sc.getStudentID())).thenReturn(Arrays.asList(scEntity1));

        var results = studentCourseService.getStudentCourses(sc.getStudentID(), false);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStudentID()).isEqualTo(sc.getStudentID());
        assertThat(results.get(0).getCourseDetails()).isNotNull();
    }

    @Test
    public void testGetStudentCoursesWithExaminableCourse() {
        Course course = new Course();
        course.setCourseID("1234567");
        course.setCourseCode("CH");
        course.setCourseLevel("12");
        course.setCourseName("Test Course Name");

        StudentExam se = new StudentExam();
        se.setId(UUID.randomUUID());
        se.setExamPercentage(85.0);
        se.setSchoolPercentage(90.0);
        se.setSpecialCase("C");
        se.setToWriteFlag("Y");

        StudentCourse sc = new StudentCourse();
        sc.setStudentID(UUID.randomUUID());
        sc.setCourseID("1234567");
        sc.setCourseCode("CH");
        sc.setCourseLevel("12");
        sc.setProvExamCourse("Y");
        sc.setStudentExamId(se.getId());
        sc.setExamPercent(se.getExamPercentage());
        sc.setSchoolPercent(se.getSchoolPercentage());
        sc.setSpecialCase(se.getSpecialCase());
        sc.setToWriteFlag(se.getToWriteFlag());

        StudentCourseEntity scEntity1 = studentCourseTransformer.transformToEntity(sc);

        when(courseServiceV2.getCourseInfo(course.getCourseCode(), course.getCourseLevel())).thenReturn(course);
        when(courseServiceV2.getCourseInfo(course.getCourseID())).thenReturn(course);
        when(studentCourseRepository.findByStudentID(sc.getStudentID())).thenReturn(Arrays.asList(scEntity1));

        var results = studentCourseService.getStudentCourses(sc.getStudentID(), true);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStudentID()).isEqualTo(sc.getStudentID());
        assertThat(results.get(0).getCourseDetails()).isNotNull();
        assertThat(results.get(0).getStudentExamId()).isNotNull();
    }

    @Test
    public void testSaveStudentCourse_when_StudentCourse_isCreated() {
        Course course = new Course();
        course.setCourseID("1234567");
        course.setCourseCode("CH");
        course.setCourseLevel("12");
        course.setCourseName("Test Course Name");

        StudentCourse sc = new StudentCourse();
        sc.setStudentID(UUID.randomUUID());
        sc.setCourseID(course.getCourseID());
        sc.setCourseCode(course.getCourseCode());
        sc.setCourseLevel(course.getCourseLevel());
        sc.setOriginalCredits(4);
        sc.setHasRelatedCourse("N");
        sc.setProvExamCourse("N");
        sc.setCourseDetails(course);

        StudentCourseEntity scEntity = studentCourseTransformer.transformToEntity(sc);

        StudentCourseEntity responseEntity = new StudentCourseEntity();
        BeanUtils.copyProperties(scEntity, responseEntity);
        responseEntity.setId(UUID.randomUUID());
        responseEntity.setCustomizedCourseName(course.getCourseName());

        when(studentCourseRepository.saveAndFlush(any())).thenReturn(responseEntity);

        var result = studentCourseService.saveStudentCourse(sc);
        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isEqualTo(sc.getStudentID());
        assertThat(result.getCourseID()).isEqualTo(course.getCourseID());
    }

    @Test
    public void testSaveStudentCourse_when_StudentCourse_isCreated_as_Examinable() {
        Course course = new Course();
        course.setCourseID("1234567");
        course.setCourseCode("CH");
        course.setCourseLevel("12");
        course.setCourseName("Test Course Name");

        StudentExam se = new StudentExam();
        se.setExamPercentage(85.0);
        se.setSchoolPercentage(90.0);
        se.setSpecialCase("C");
        se.setToWriteFlag("Y");

        StudentCourse sc = new StudentCourse();
        sc.setStudentID(UUID.randomUUID());
        sc.setCourseID(course.getCourseID());
        sc.setCourseCode(course.getCourseCode());
        sc.setCourseLevel(course.getCourseLevel());
        sc.setOriginalCredits(4);
        sc.setHasRelatedCourse("N");
        sc.setCourseDetails(course);
        // Examinable Course
        sc.setProvExamCourse("Y");
        sc.setSchoolPercent(se.getSchoolPercentage());
        sc.setExamPercent(se.getExamPercentage());
        sc.setSpecialCase(se.getSpecialCase());
        sc.setToWriteFlag(se.getToWriteFlag());

        StudentExamEntity seEntity = studentExamTransformer.transformToEntity(se);
        StudentCourseEntity scEntity = studentCourseTransformer.transformToEntity(sc);

        StudentExamEntity savedSeEntity = new StudentExamEntity();
        BeanUtils.copyProperties(seEntity, savedSeEntity);
        savedSeEntity.setId(UUID.randomUUID());

        StudentCourseEntity savedScEntity = new StudentCourseEntity();
        BeanUtils.copyProperties(scEntity, savedScEntity);
        savedScEntity.setId(UUID.randomUUID());
        savedScEntity.setCustomizedCourseName(course.getCourseName());
        savedScEntity.setStudentExamId(savedSeEntity.getId());

        when(courseServiceV2.getCourseInfo(course.getCourseCode(), course.getCourseLevel())).thenReturn(course);
        when(studentExamRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        when(studentExamRepository.saveAndFlush(any())).thenReturn(savedSeEntity);
        when(studentCourseRepository.saveAndFlush(any())).thenReturn(savedScEntity);

        var result = studentCourseService.saveStudentCourse(sc);
        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isEqualTo(sc.getStudentID());
        assertThat(result.getCourseID()).isEqualTo(course.getCourseID());
        assertThat(result.getStudentExamId()).isNotNull();
        assertThat(result.getExamPercent()).isEqualTo(se.getExamPercentage());
        assertThat(result.getSchoolPercent()).isEqualTo(se.getSchoolPercentage());
        assertThat(result.getSpecialCase()).isEqualTo(se.getSpecialCase());
        assertThat(result.getToWriteFlag()).isEqualTo(se.getToWriteFlag());
    }

    @Test
    public void testSaveStudentCourse_when_StudentCourse_isCreated_as_Examinable_withRelatedCourse() {
        Course course = new Course();
        course.setCourseID("1234567");
        course.setCourseCode("CH");
        course.setCourseLevel("12");
        course.setCourseName("Test Course Name 1");

        Course relatedCourse = new Course();
        relatedCourse.setCourseID("7654321");
        relatedCourse.setCourseCode("PH");
        relatedCourse.setCourseLevel("12");
        relatedCourse.setCourseName("Test Course Name 2");

        StudentExam se = new StudentExam();
        se.setExamPercentage(85.0);
        se.setSchoolPercentage(90.0);
        se.setSpecialCase("C");
        se.setToWriteFlag("Y");

        StudentCourse sc = new StudentCourse();
        sc.setStudentID(UUID.randomUUID());
        sc.setCourseID(course.getCourseID());
        sc.setCourseCode(course.getCourseCode());
        sc.setCourseLevel(course.getCourseLevel());
        sc.setOriginalCredits(4);
        // Related Course
        sc.setHasRelatedCourse("Y");
        sc.setRelatedCourseId(relatedCourse.getCourseID());
        sc.setRelatedCourse(relatedCourse.getCourseCode());
        sc.setRelatedLevel(relatedCourse.getCourseLevel());
        sc.setCourseDetails(course);
        // Examinable Course
        sc.setProvExamCourse("Y");
        sc.setSchoolPercent(se.getSchoolPercentage());
        sc.setExamPercent(se.getExamPercentage());
        sc.setSpecialCase(se.getSpecialCase());
        sc.setToWriteFlag(se.getToWriteFlag());

        StudentExamEntity seEntity = studentExamTransformer.transformToEntity(se);
        StudentCourseEntity scEntity = studentCourseTransformer.transformToEntity(sc);

        StudentExamEntity savedSeEntity = new StudentExamEntity();
        BeanUtils.copyProperties(seEntity, savedSeEntity);
        savedSeEntity.setId(UUID.randomUUID());

        StudentCourseEntity savedScEntity = new StudentCourseEntity();
        BeanUtils.copyProperties(scEntity, savedScEntity);
        savedScEntity.setId(UUID.randomUUID());
        savedScEntity.setCustomizedCourseName(course.getCourseName());
        savedScEntity.setStudentExamId(savedSeEntity.getId());
        savedScEntity.setRelatedCourseId(Integer.valueOf(relatedCourse.getCourseID()));

        when(courseServiceV2.getCourseInfo(relatedCourse.getCourseID())).thenReturn(relatedCourse);
        when(studentExamRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        when(studentExamRepository.saveAndFlush(any())).thenReturn(savedSeEntity);
        when(studentCourseRepository.saveAndFlush(any())).thenReturn(savedScEntity);

        var result = studentCourseService.saveStudentCourse(sc);
        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isEqualTo(sc.getStudentID());
        assertThat(result.getCourseID()).isEqualTo(course.getCourseID());
        assertThat(result.getStudentExamId()).isNotNull();
        assertThat(result.getExamPercent()).isEqualTo(se.getExamPercentage());
        assertThat(result.getSchoolPercent()).isEqualTo(se.getSchoolPercentage());
        assertThat(result.getSpecialCase()).isEqualTo(se.getSpecialCase());
        assertThat(result.getToWriteFlag()).isEqualTo(se.getToWriteFlag());
        assertThat(result.getRelatedCourseId()).isEqualTo(relatedCourse.getCourseID());
        assertThat(result.getRelatedCourse()).isEqualTo(relatedCourse.getCourseCode());
    }

    @Test
    public void testSaveStudentCourse_when_StudentCourse_isUpdated_as_Examinable() {
        Course course = new Course();
        course.setCourseID("1234567");
        course.setCourseCode("CH");
        course.setCourseLevel("12");
        course.setCourseName("Test Course Name");

        StudentExam se = new StudentExam();
        se.setId(UUID.randomUUID());
        se.setExamPercentage(85.0);
        se.setSchoolPercentage(90.0);
        se.setSpecialCase("C");
        se.setToWriteFlag("Y");

        StudentCourse sc = new StudentCourse();
        sc.setId(UUID.randomUUID());
        sc.setStudentID(UUID.randomUUID());
        sc.setCourseID(course.getCourseID());
        sc.setCourseCode(course.getCourseCode());
        sc.setCourseLevel(course.getCourseLevel());
        sc.setOriginalCredits(4);
        sc.setHasRelatedCourse("N");
        sc.setProvExamCourse("N");
        sc.setCourseDetails(course);
        // Examinable Course
        sc.setProvExamCourse("Y");
        sc.setStudentExamId(se.getId());
        sc.setSchoolPercent(se.getSchoolPercentage());
        sc.setExamPercent(se.getExamPercentage());
        sc.setSpecialCase(se.getSpecialCase());
        sc.setToWriteFlag(se.getToWriteFlag());

        StudentExamEntity seEntity = studentExamTransformer.transformToEntity(se);
        StudentCourseEntity scEntity = studentCourseTransformer.transformToEntity(sc);

        StudentExamEntity savedSeEntity = new StudentExamEntity();
        BeanUtils.copyProperties(seEntity, savedSeEntity);

        StudentCourseEntity savedScEntity = new StudentCourseEntity();
        BeanUtils.copyProperties(scEntity, savedScEntity);
        savedScEntity.setId(UUID.randomUUID());
        savedScEntity.setCustomizedCourseName(course.getCourseName());
        savedScEntity.setStudentExamId(savedSeEntity.getId());

        when(studentCourseRepository.findById(sc.getId())).thenReturn(Optional.of(scEntity));
        when(courseServiceV2.getCourseInfo(course.getCourseCode(), course.getCourseLevel())).thenReturn(course);
        when(studentExamRepository.findById(any(UUID.class))).thenReturn(Optional.of(seEntity));
        when(studentExamRepository.saveAndFlush(any())).thenReturn(savedSeEntity);
        when(studentCourseRepository.saveAndFlush(any())).thenReturn(savedScEntity);

        var result = studentCourseService.saveStudentCourse(sc);
        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isEqualTo(sc.getStudentID());
        assertThat(result.getCourseID()).isEqualTo(course.getCourseID());
        assertThat(result.getStudentExamId()).isNotNull();
        assertThat(result.getExamPercent()).isEqualTo(se.getExamPercentage());
        assertThat(result.getSchoolPercent()).isEqualTo(se.getSchoolPercentage());
        assertThat(result.getSpecialCase()).isEqualTo(se.getSpecialCase());
        assertThat(result.getToWriteFlag()).isEqualTo(se.getToWriteFlag());
    }


    @Test
    public void testDeleteStudentCourse() {
        UUID studentCourseId = UUID.randomUUID();

        StudentCourseEntity sce = new StudentCourseEntity();
        sce.setId(studentCourseId);
        sce.setStudentID(UUID.randomUUID());
        sce.setCourseID(1234567);

        when(studentCourseRepository.findById(studentCourseId)).thenReturn(Optional.of(sce));
        var result = studentCourseService.deleteStudentCourse(studentCourseId);
        assertThat(result).isEqualTo(1);
    }

    @Test
    public void testDeleteStudentCourseWithStudentExam() {
        UUID studentCourseId = UUID.randomUUID();

        StudentCourseEntity sce = new StudentCourseEntity();
        sce.setId(studentCourseId);
        sce.setStudentID(UUID.randomUUID());
        sce.setCourseID(1234567);
        sce.setStudentExamId(UUID.randomUUID());

        when(studentCourseRepository.findById(studentCourseId)).thenReturn(Optional.of(sce));
        var result = studentCourseService.deleteStudentCourse(studentCourseId);
        assertThat(result).isEqualTo(2);
    }


}
