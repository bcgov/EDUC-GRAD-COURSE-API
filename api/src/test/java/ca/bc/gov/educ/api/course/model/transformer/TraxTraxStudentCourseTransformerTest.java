package ca.bc.gov.educ.api.course.model.transformer;

import ca.bc.gov.educ.api.course.model.dto.TraxStudentCourse;
import ca.bc.gov.educ.api.course.model.entity.TraxStudentCourseEntity;
import ca.bc.gov.educ.api.course.model.entity.TraxStudentCourseId;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class TraxTraxStudentCourseTransformerTest {
    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    TraxStudentCourseTransformer traxStudentCourseTransformer;

    @Test
    public void testTransformToDTO() {
        TraxStudentCourse traxStudentCourse = new TraxStudentCourse();
        traxStudentCourse.setPen("123456789");
        traxStudentCourse.setCourseCode("main");
        traxStudentCourse.setCourseName("main course");
        traxStudentCourse.setCourseLevel("12");
        traxStudentCourse.setLanguage("en");

       TraxStudentCourseId traxStudentCourseId = new TraxStudentCourseId();
       traxStudentCourseId.setPen(traxStudentCourse.getPen());
       traxStudentCourseId.setCourseCode(traxStudentCourse.getCourseCode());
       traxStudentCourseId.setCourseLevel(traxStudentCourse.getCourseLevel());

        TraxStudentCourseEntity traxStudentCourseEntity = new TraxStudentCourseEntity();
        traxStudentCourseEntity.setCourseKey(traxStudentCourseId);

        Mockito.when(modelMapper.map(traxStudentCourseEntity, TraxStudentCourse.class)).thenReturn(traxStudentCourse);
        var result = traxStudentCourseTransformer.transformToDTO(traxStudentCourseEntity);
        assertThat(result).isNotNull();
        assertThat(result.getPen()).isEqualTo(traxStudentCourseEntity.getCourseKey().getPen());
        assertThat(result.getCourseCode()).isEqualTo(traxStudentCourseEntity.getCourseKey().getCourseCode());
        assertThat(result.getCourseLevel()).isEqualTo(traxStudentCourseEntity.getCourseKey().getCourseLevel());
    }

    @Test
    public void testTransformOptionalToDTO() {
        TraxStudentCourse traxStudentCourse = new TraxStudentCourse();
        traxStudentCourse.setPen("123456789");
        traxStudentCourse.setCourseCode("main");
        traxStudentCourse.setCourseName("main course");
        traxStudentCourse.setCourseLevel("12");
        traxStudentCourse.setLanguage("en");

        TraxStudentCourseId traxStudentCourseId = new TraxStudentCourseId();
        traxStudentCourseId.setPen(traxStudentCourse.getPen());
        traxStudentCourseId.setCourseCode(traxStudentCourse.getCourseCode());
        traxStudentCourseId.setCourseLevel(traxStudentCourse.getCourseLevel());

        TraxStudentCourseEntity traxStudentCourseEntity = new TraxStudentCourseEntity();
        traxStudentCourseEntity.setCourseKey(traxStudentCourseId);

        Mockito.when(modelMapper.map(traxStudentCourseEntity, TraxStudentCourse.class)).thenReturn(traxStudentCourse);
        var result = traxStudentCourseTransformer.transformToDTO(Optional.of(traxStudentCourseEntity));
        assertThat(result).isNotNull();
        assertThat(result.getPen()).isEqualTo(traxStudentCourseEntity.getCourseKey().getPen());
        assertThat(result.getCourseCode()).isEqualTo(traxStudentCourseEntity.getCourseKey().getCourseCode());
        assertThat(result.getCourseLevel()).isEqualTo(traxStudentCourseEntity.getCourseKey().getCourseLevel());
    }

    @Test
    public void testTransformToEntity() {
        TraxStudentCourse traxStudentCourse = new TraxStudentCourse();
        traxStudentCourse.setPen("123456789");
        traxStudentCourse.setCourseCode("main");
        traxStudentCourse.setCourseName("main course");
        traxStudentCourse.setCourseLevel("12");
        traxStudentCourse.setLanguage("en");

        TraxStudentCourseId traxStudentCourseId = new TraxStudentCourseId();
        traxStudentCourseId.setPen(traxStudentCourse.getPen());
        traxStudentCourseId.setCourseCode(traxStudentCourse.getCourseCode());
        traxStudentCourseId.setCourseLevel(traxStudentCourse.getCourseLevel());

        TraxStudentCourseEntity traxStudentCourseEntity = new TraxStudentCourseEntity();
        traxStudentCourseEntity.setCourseKey(traxStudentCourseId);

        Mockito.when(modelMapper.map(traxStudentCourse, TraxStudentCourseEntity.class)).thenReturn(traxStudentCourseEntity);
        var result = traxStudentCourseTransformer.transformToEntity(traxStudentCourse);
        assertThat(result).isNotNull();
        assertThat(result.getCourseKey().getPen()).isEqualTo(traxStudentCourse.getPen());
        assertThat(result.getCourseKey().getCourseCode()).isEqualTo(traxStudentCourse.getCourseCode());
        assertThat(result.getCourseKey().getCourseLevel()).isEqualTo(traxStudentCourse.getCourseLevel());
    }
}
