package ca.bc.gov.educ.api.course.model.transformer;

import ca.bc.gov.educ.api.course.model.dto.ExaminableCourse;
import ca.bc.gov.educ.api.course.model.dto.FineArtsAppliedSkillsCode;
import ca.bc.gov.educ.api.course.model.entity.ExaminableCourseEntity;
import ca.bc.gov.educ.api.course.model.entity.FineArtsAppliedSkillsCodeEntity;
import ca.bc.gov.educ.api.course.util.EducCourseApiUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class ExaminableCourseTransformer {

    @Autowired
    ModelMapper modelMapper;

    public List<ExaminableCourse> transformToDTO (Iterable<ExaminableCourseEntity> examinableCoursesEntities ) {

        List<ExaminableCourse> examinableCourseList = new ArrayList<>();

        for (ExaminableCourseEntity ec : examinableCoursesEntities) {
            ExaminableCourse examinableCourse = modelMapper.map(ec, ExaminableCourse.class);
            examinableCourse.setExaminableStart(
                    EducCourseApiUtils.parseDateFromString(
                            ec.getExaminableStart() != null ? ec.getExaminableStart().toString() : null));
            examinableCourse.setExaminableEnd(
                    EducCourseApiUtils.parseDateFromString(
                            ec.getExaminableEnd() != null ? ec.getExaminableEnd().toString() : null));
            examinableCourse.setOptionalStart(
                    EducCourseApiUtils.parseDateFromString(
                            ec.getOptionalStart() != null ? ec.getOptionalStart().toString() : null));
            examinableCourse.setOptionalEnd(
                    EducCourseApiUtils.parseDateFromString(
                            ec.getOptionalEnd() != null ? ec.getOptionalEnd().toString() : null));
            examinableCourseList.add(examinableCourse);
        }

        return examinableCourseList;
    }


}
