package ca.bc.gov.educ.api.course.model.transformer;

import ca.bc.gov.educ.api.course.model.dto.StudentExam;
import ca.bc.gov.educ.api.course.model.entity.StudentExamEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class StudentExamTransformer {

    @Autowired
    ModelMapper modelMapper;

    public StudentExam transformToDTO (StudentExamEntity studentExamEntity) {
       return modelMapper.map(studentExamEntity, StudentExam.class);
    }

    public StudentExam transformToDTO (Optional<StudentExamEntity> studentExamEntity ) {
        StudentExamEntity se = new StudentExamEntity();

        if (studentExamEntity.isPresent())
            se = studentExamEntity.get();
        
        return transformToDTO(se);
    }

    public List<StudentExam> transformToDTO (Iterable<StudentExamEntity> studentExamEntities ) {
        List<StudentExam> studentExamList = new ArrayList<>();

        for (StudentExamEntity courseAchievementEntity : studentExamEntities) {
            StudentExam courseAchievement =  transformToDTO(courseAchievementEntity);
            studentExamList.add(courseAchievement);
        }
        return studentExamList;
    }

    public StudentExamEntity transformToEntity(StudentExam studentExam) {
        return modelMapper.map(studentExam, StudentExamEntity.class);
    }
}
