package ca.bc.gov.educ.api.course.model.transformer;

import ca.bc.gov.educ.api.course.model.dto.StudentCourse;
import ca.bc.gov.educ.api.course.model.entity.StudentCourseEntity;
import ca.bc.gov.educ.api.course.util.EducCourseApiUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class StudentCourseTransformer {

    @Autowired
    ModelMapper modelMapper;

    public StudentCourse transformToDTO (StudentCourseEntity studentCourseEntity) {
        StudentCourse sc = modelMapper.map(studentCourseEntity, StudentCourse.class);
        sc.setCourseID(studentCourseEntity.getCourseID() != null? studentCourseEntity.getCourseID().toString() : null);
        sc.setSessionDate(EducCourseApiUtils.formatDate(studentCourseEntity.getSessionDate()));
        sc.setProvExamCourse(studentCourseEntity.getStudentExamId() != null? "Y" : "N");
        sc.setHasRelatedCourse(studentCourseEntity.getRelatedCourseId() != null? "Y" : "N");
        sc.setRelatedCourseId(studentCourseEntity.getRelatedCourseId() != null? studentCourseEntity.getRelatedCourseId().toString() : null);
        return sc;
    }

    public StudentCourse transformToDTO (Optional<StudentCourseEntity> courseAchievementEntity ) {
        StudentCourseEntity cae = new StudentCourseEntity();
        if (courseAchievementEntity.isPresent())
            cae = courseAchievementEntity.get();
        return transformToDTO(cae);
    }

    public List<StudentCourse> transformToDTO (Iterable<StudentCourseEntity> courseAchievementEntities ) {
        List<StudentCourse> courseAchievementList = new ArrayList<>();

        for (StudentCourseEntity courseAchievementEntity : courseAchievementEntities) {
            StudentCourse courseAchievement = transformToDTO(courseAchievementEntity);
            courseAchievementList.add(courseAchievement);
        }
        return courseAchievementList;
    }

    public StudentCourseEntity  transformToEntity(StudentCourse studentCourse) {
        return modelMapper.map(studentCourse, StudentCourseEntity.class);
    }
}
