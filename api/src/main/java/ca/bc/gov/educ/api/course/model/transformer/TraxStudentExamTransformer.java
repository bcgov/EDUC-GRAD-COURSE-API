package ca.bc.gov.educ.api.course.model.transformer;

import ca.bc.gov.educ.api.course.model.dto.TraxStudentExam;
import ca.bc.gov.educ.api.course.model.entity.TraxStudentExamEntity;
import ca.bc.gov.educ.api.course.util.EducCourseApiUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TraxStudentExamTransformer {

    @Autowired
    ModelMapper modelMapper;

    public TraxStudentExam transformToDTO (TraxStudentExamEntity traxStudentExamEntity) {
       return modelMapper.map(traxStudentExamEntity, TraxStudentExam.class);
    }

    public TraxStudentExam transformToDTO (Optional<TraxStudentExamEntity> courseAchievementEntity ) {
        TraxStudentExamEntity cae = new TraxStudentExamEntity();

        if (courseAchievementEntity.isPresent())
            cae = courseAchievementEntity.get();
        
        return modelMapper.map(cae, TraxStudentExam.class);
    }

    public List<TraxStudentExam> transformToDTO (Iterable<TraxStudentExamEntity> courseAchievementEntities ) {

        List<TraxStudentExam> courseAchievementList = new ArrayList<>();

        for (TraxStudentExamEntity courseAchievementEntity : courseAchievementEntities) {
            TraxStudentExam courseAchievement =  modelMapper.map(courseAchievementEntity, TraxStudentExam.class);
           
            courseAchievement.setPen(courseAchievementEntity.getCourseKey().getPen());
            courseAchievement.setCourseCode(courseAchievementEntity.getCourseKey().getCourseCode());
            courseAchievement.setCourseLevel(courseAchievementEntity.getCourseKey().getCourseLevel());
            courseAchievement.setSessionDate(EducCourseApiUtils.parseTraxDate(courseAchievementEntity.getCourseKey().getSessionDate()));
            courseAchievementList.add(courseAchievement);
        }

        return courseAchievementList;
    }

    public TraxStudentExamEntity transformToEntity(TraxStudentExam traxStudentExam) {
        return modelMapper.map(traxStudentExam, TraxStudentExamEntity.class);
    }
}
