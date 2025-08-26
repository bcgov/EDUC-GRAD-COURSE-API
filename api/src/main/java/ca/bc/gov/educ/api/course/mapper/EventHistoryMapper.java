package ca.bc.gov.educ.api.course.mapper;

import ca.bc.gov.educ.api.course.exception.ServiceException;
import ca.bc.gov.educ.api.course.model.dto.CourseDataSourceDTO;
import ca.bc.gov.educ.api.course.model.dto.CourseRegistryEventDTO;
import ca.bc.gov.educ.api.course.model.dto.EventHistory;
import ca.bc.gov.educ.api.course.model.dto.mapper.UUIDMapper;
import ca.bc.gov.educ.api.course.model.entity.EventEntity;
import ca.bc.gov.educ.api.course.model.entity.EventHistoryEntity;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import ca.bc.gov.educ.api.course.util.JsonUtilWithJavaTime;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.Builder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
@Mapper(
        componentModel = "spring",
        uses = {EventMapper.class, UUIDMapper.class},
        builder = @Builder(disableBuilder = true)
)
public abstract class EventHistoryMapper {

    protected EducCourseApiConstants constants;

    @Autowired
    public void setConstants(EducCourseApiConstants constants){
        this.constants = constants;
    }

    @Mapping(source = "event", target = "eventHistoryUrl", qualifiedByName = "getUrlFromEventEntity")
    @Mapping(source = "event", target = "courseId", qualifiedByName = "getCourseIdFromEventEntity")
    public abstract EventHistory toStructure(EventHistoryEntity eventHistoryEntity);

    @Mapping(target = "event.eventPayloadBytes", ignore = true)
    public abstract EventHistoryEntity toEntity(EventHistory eventHistory);

    @Named("getUrlFromEventEntity")
    protected String getUrlFromEventEntity(EventEntity eventEntity) {
        Long courseId = this.getCourseIdFromEventEntity(eventEntity);
        try {
            if (courseId != null) {
                return String.format(constants.getCoregSearchUrl(), courseId);
            }
        } catch (final Exception exception) {
            log.error("Error building eventHistoryUrl: {}", exception.getMessage());
        }
        return null;
    }

    @Named("getCourseIdFromEventEntity")
    protected static Long getCourseIdFromEventEntity(EventEntity eventEntity) {
        Long courseId = 0L;
        if (eventEntity != null) {
            try {
                CourseRegistryEventDTO courseRegistryEvent = JsonUtilWithJavaTime.getJsonObjectFromString(CourseRegistryEventDTO.class, eventEntity.getEventPayload());
                courseId = courseRegistryEvent.getAffectedId();
            } catch (ServiceException | JsonProcessingException e) {
                log.error(e.getMessage());
            }
        }
        return courseId;
    }
}
