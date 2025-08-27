package ca.bc.gov.educ.api.course.model.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event extends BaseModel {
    private UUID eventId;
    private String eventPayload;
    private String payloadVersion;
    private String eventStatus;
    private String eventType;
    private String replyTo;
    private String eventOutcome;
    private String activityCode;
}
