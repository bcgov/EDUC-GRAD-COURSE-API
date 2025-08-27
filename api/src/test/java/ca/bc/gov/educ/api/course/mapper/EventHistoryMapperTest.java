package ca.bc.gov.educ.api.course.mapper;

import ca.bc.gov.educ.api.course.model.dto.EventHistory;
import ca.bc.gov.educ.api.course.model.entity.EventEntity;
import ca.bc.gov.educ.api.course.model.entity.EventHistoryEntity;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.charset.StandardCharsets;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class EventHistoryMapperTest {

    @Autowired
    private EventHistoryMapper mapper; // Spring injects the mapper

    @Autowired
    private EducCourseApiConstants constants;

    @MockBean
    @Qualifier("courseApiClient")
    public WebClient courseApiWebClient;

    @MockBean
    @Qualifier("gradCoregApiClient")
    public WebClient coregApiWebClient;

    @BeforeEach
    void setup() {
        constants.setCoregSearchUrl("http://coreg.example.com/course/%d");
    }

    @Test
    void testToStructure() {
        // create EventEntity with payload bytes
        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventPayloadBytes("{\"affectedId\":123}".getBytes(StandardCharsets.UTF_8));

        // create EventHistoryEntity and set the EventEntity
        EventHistoryEntity historyEntity = new EventHistoryEntity();
        historyEntity.setEvent(eventEntity);

        // call mapper
        EventHistory history = mapper.toStructure(historyEntity);

        assertNotNull(history);
        assertEquals("123", history.getCourseId().toString());
    }

    @Test
    void testToEntity() {
        // create the nested EventEntity
        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventPayloadBytes("payload".getBytes(StandardCharsets.UTF_8));

        // create EventHistoryEntity and set the EventEntity
        EventHistoryEntity historyEntity = new EventHistoryEntity();
        historyEntity.setEvent(eventEntity);

        // map to DTO
        EventHistory history = mapper.toStructure(historyEntity);

        assertNotNull(history);
        assertEquals("payload", history.getEvent().getEventPayload());
    }
}
