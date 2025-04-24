package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.Course;
import ca.bc.gov.educ.api.course.model.dto.StudentCourse;
import ca.bc.gov.educ.api.course.model.dto.StudentExam;
import ca.bc.gov.educ.api.course.model.entity.StudentCourseEntity;
import ca.bc.gov.educ.api.course.model.entity.StudentExamEntity;
import ca.bc.gov.educ.api.course.model.transformer.StudentCourseTransformer;
import ca.bc.gov.educ.api.course.model.transformer.StudentExamTransformer;
import ca.bc.gov.educ.api.course.repository.StudentExamRepository;
import ca.bc.gov.educ.api.course.repository.StudentCourseRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class StudentCourseService {

    private static final String CREATE_USER = "createUser";
    private static final String CREATE_DATE = "createDate";

    private final StudentCourseRepository studentCourseRepository;

    private final StudentExamRepository studentExamRepository;

    private final StudentCourseTransformer studentCourseTransformer;

    private final StudentExamTransformer studentExamTransformer;

    private final ca.bc.gov.educ.api.course.service.v2.CourseService courseService;

    @Autowired
    public StudentCourseService(StudentCourseRepository studentCourseRepository, StudentExamRepository studentExamRepository, StudentCourseTransformer studentCourseTransformer, StudentExamTransformer studentExamTransformer,
        @Qualifier("courseServiceV2")ca.bc.gov.educ.api.course.service.v2.CourseService courseService) {
        this.studentCourseRepository = studentCourseRepository;
        this.studentExamRepository = studentExamRepository;
        this.studentCourseTransformer = studentCourseTransformer;
        this.studentExamTransformer = studentExamTransformer;
        this.courseService = courseService;
    }

    @Transactional(readOnly = true)
    public List<StudentCourse> getStudentCourses(UUID studentID, boolean sortForUI) {
        List<StudentCourse> studentCourses = new ArrayList<>();
        try {
            studentCourses = studentCourseTransformer.transformToDTO(studentCourseRepository.findByStudentID(studentID));
            getCourseDetails(studentCourses);
            getStudentExams(studentCourses);
        } catch (Exception e) {
            log.debug(String.format("Exception: %s",e));
        }
        getDataSorted(studentCourses,sortForUI);
        return studentCourses;
    }

    @Transactional
    public StudentCourse saveStudentCourse(StudentCourse studentCourse) {
        StudentCourse response;
        StudentExam studentExam = null;
        UUID studentCourseId = studentCourse.getId();
        Optional<StudentCourseEntity> studentCourseOptional = studentCourseId == null? Optional.empty() : studentCourseRepository.findById(studentCourseId);
        StudentCourseEntity sourceObject = studentCourseTransformer.transformToEntity(studentCourse);
        if (studentCourseOptional.isPresent()) {
            // StudentCourse
            StudentCourseEntity entity = studentCourseOptional.get();
            if (entity.getStudentExamId() != null && !entity.getStudentExamId().equals(studentCourse.getStudentExamId())) {
                // delete the current student exam
                studentExamRepository.deleteById(entity.getStudentExamId());
            }
            // StudentExam
            if (studentCourse.getStudentExamId() != null) {
                studentExam = buildStudentExam(studentCourse);
                saveStudentExam(studentExam);
                sourceObject.setStudentExamId(studentExam.getId());
            }
            entity.setUpdateDate(null);
            entity.setUpdateUser(null);
            BeanUtils.copyProperties(sourceObject, entity, CREATE_USER, CREATE_DATE);
            entity = studentCourseRepository.saveAndFlush(entity);
            response = studentCourseTransformer.transformToDTO(entity);

        } else {
            // StudentExam
            if ("Y".equalsIgnoreCase(studentCourse.getProvExamCourse())) {
                studentExam = buildStudentExam(studentCourse);
                studentExam.setId(UUID.randomUUID());
                studentExam = saveStudentExam(studentExam);
                sourceObject.setStudentExamId(studentExam.getId());
            }
            // StudentCourse
            if (sourceObject.getId() == null) {
                sourceObject.setId(UUID.randomUUID());
            }
            sourceObject = studentCourseRepository.saveAndFlush(sourceObject);
            response = studentCourseTransformer.transformToDTO(sourceObject);
        }
        getCourseDetail(response);
        if (studentExam != null) {
            populateStudentExamInStudentCourse(response, studentExam);
        }
        return response;
    }

    private StudentExam saveStudentExam(StudentExam studentExam) {
        Optional<StudentExamEntity> studentExamOptional = studentExamRepository.findById(studentExam.getId());
        StudentExamEntity sourceObject = studentExamTransformer.transformToEntity(studentExam);
        if (studentExamOptional.isPresent()) {
            StudentExamEntity entity = studentExamOptional.get();
            entity.setUpdateDate(null);
            entity.setUpdateUser(null);
            BeanUtils.copyProperties(sourceObject, entity, CREATE_USER, CREATE_DATE);
            entity = studentExamRepository.saveAndFlush(entity);
            return studentExamTransformer.transformToDTO(entity);
        } else {
            sourceObject = studentExamRepository.saveAndFlush(sourceObject);
            return studentExamTransformer.transformToDTO(sourceObject);
        }
    }

    @Transactional
    public int deleteStudentCourse(UUID studentCourseId) {
        int deleteCount = 0;
        Optional<StudentCourseEntity> scOptional = studentCourseRepository.findById(studentCourseId);
        if (scOptional.isPresent()) {
            StudentCourseEntity sc = scOptional.get();
            UUID studentExamId = sc.getStudentExamId();
            studentCourseRepository.delete(sc);
            deleteCount++;
            if (studentExamId != null) {
                studentExamRepository.deleteById(studentExamId);
                deleteCount++;
            }
        }
        return deleteCount;
    }

    private void getStudentExams(List<StudentCourse> studentCourses) {
        studentCourses.forEach(sc -> {
            if (sc.getStudentExamId() != null) {
                sc.setProvExamCourse("Y");
                Optional<StudentExamEntity> optional = studentExamRepository.findById(sc.getStudentExamId());
                optional.ifPresent(studentExamEntity -> populateStudentExamInStudentCourse(sc, studentExamTransformer.transformToDTO(studentExamEntity)));
            } else {
                sc.setProvExamCourse("N");
            }
        });
    }

    private void populateStudentExamInStudentCourse(StudentCourse sc, StudentExam se) {
        sc.setStudentExamId(se.getId());
        sc.setSchoolPercent(se.getSchoolPercentage());
        sc.setBestSchoolPercent(se.getBestSchoolPercentage());
        sc.setExamPercent(se.getExamPercentage());
        sc.setBestExamPercent(se.getBestExamPercentage());
        sc.setSpecialCase(se.getSpecialCase());
        sc.setToWriteFlag(se.getToWriteFlag());
    }

    private StudentExam buildStudentExam(StudentCourse sc) {
        StudentExam se = new StudentExam();
        se.setId(sc.getStudentExamId());
        se.setSchoolPercentage(sc.getSchoolPercent());
        se.setBestSchoolPercentage(sc.getBestSchoolPercent());
        se.setExamPercentage(sc.getExamPercent());
        se.setBestExamPercentage(sc.getBestExamPercent());
        se.setSpecialCase(sc.getSpecialCase());
        se.setToWriteFlag(sc.getToWriteFlag());
        return se;
    }

    private void getCourseDetails(List<StudentCourse> studentCourses) {
        for (StudentCourse sc : studentCourses) {
            getCourseDetail(sc);
        }
    }

    private void getCourseDetail(StudentCourse sc) {
        if (sc.getCourseID() != null && NumberUtils.isCreatable(sc.getCourseID())) {
            Course course = courseService.getCourseInfo(sc.getCourseID());
            if (course != null) {
                sc.setCourseDetails(course);
                sc.setCourseCode(course.getCourseCode());
                sc.setCourseLevel(course.getCourseLevel());
                sc.setCourseName(course.getCourseName());
                sc.setGenericCourseType(course.getGenericCourseType());
                sc.setLanguage(course.getLanguage());
                sc.setCourseDetails(course);
                sc.setOriginalCredits(course.getNumCredits());
            }
        }
        if (sc.getRelatedCourseId() != null && NumberUtils.isCreatable(sc.getRelatedCourseId())) {
            Course relatedCourse = courseService.getCourseInfo(sc.getRelatedCourseId());
            if (relatedCourse != null) {
                sc.setRelatedCourse(relatedCourse.getCourseCode());
                sc.setRelatedLevel(relatedCourse.getCourseLevel());
                sc.setRelatedCourseName(relatedCourse.getCourseName());
            }
        }
    }

    private void getDataSorted(List<StudentCourse> studentCourses, boolean sortForUI) {
        if(sortForUI) {
            Collections.sort(studentCourses, Comparator.comparing(StudentCourse::getStudentID)
                    .thenComparing(StudentCourse::getCourseCode)
                    .thenComparing(StudentCourse::getCourseLevel)
                    .thenComparing(StudentCourse::getSessionDate));
        }else {
            Collections.sort(studentCourses, Comparator.comparing(StudentCourse::getStudentID)
                    .thenComparing(StudentCourse::getCompletedCoursePercentage).reversed()
                    .thenComparing(StudentCourse::getCredits).reversed()
                    .thenComparing(StudentCourse::getCourseLevel).reversed());
        }
    }
}
