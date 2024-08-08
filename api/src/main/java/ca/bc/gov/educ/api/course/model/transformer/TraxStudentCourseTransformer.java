package ca.bc.gov.educ.api.course.model.transformer;

import ca.bc.gov.educ.api.course.model.dto.TraxStudentCourse;
import ca.bc.gov.educ.api.course.model.entity.TraxStudentCourseEntity;
import ca.bc.gov.educ.api.course.util.EducCourseApiUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TraxStudentCourseTransformer {

    @Autowired
    ModelMapper modelMapper;

    public TraxStudentCourse transformToDTO (TraxStudentCourseEntity traxStudentCourseEntity) {
        return modelMapper.map(traxStudentCourseEntity, TraxStudentCourse.class);
    }

    public TraxStudentCourse transformToDTO (Optional<TraxStudentCourseEntity> courseAchievementEntity ) {
        TraxStudentCourseEntity cae = new TraxStudentCourseEntity();

        if (courseAchievementEntity.isPresent())
            cae = courseAchievementEntity.get();

        return modelMapper.map(cae, TraxStudentCourse.class);
    }

    public List<TraxStudentCourse> transformToDTO (Iterable<TraxStudentCourseEntity> courseAchievementEntities ) {

        List<TraxStudentCourse> courseAchievementList = new ArrayList<>();

        for (TraxStudentCourseEntity courseAchievementEntity : courseAchievementEntities) {
            TraxStudentCourse courseAchievement = modelMapper.map(courseAchievementEntity, TraxStudentCourse.class);
           
            courseAchievement.setPen(courseAchievementEntity.getCourseKey().getPen());
            courseAchievement.setCourseCode(courseAchievementEntity.getCourseKey().getCourseCode());
            courseAchievement.setCourseLevel(courseAchievementEntity.getCourseKey().getCourseLevel());
            courseAchievement.setSessionDate(EducCourseApiUtils.parseTraxDate(courseAchievementEntity.getCourseKey().getSessionDate()));
            courseAchievementList.add(courseAchievement);
        }

        return courseAchievementList;
    }

    public TraxStudentCourseEntity transformToEntity(TraxStudentCourse traxStudentCourse) {
        return modelMapper.map(traxStudentCourse, TraxStudentCourseEntity.class);
    }
}
