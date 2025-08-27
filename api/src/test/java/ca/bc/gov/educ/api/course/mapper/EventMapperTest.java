package ca.bc.gov.educ.api.course.mapper;

import ca.bc.gov.educ.api.course.model.dto.Event;
import ca.bc.gov.educ.api.course.model.entity.EventEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class EventMapperTest {

    private final EventMapper mapper = new EventMapperImpl(); // MapStruct generates this

    @MockBean
    @Qualifier("courseApiClient")
    public WebClient courseApiWebClient;

    @MockBean
    @Qualifier("gradCoregApiClient")
    public WebClient coregApiWebClient;

    @Test
    void testEntityToDtoAndBack() {
        EventEntity entity = new EventEntity();
        entity.setEventPayloadBytes("payload".getBytes());
        entity.setCreateDate(LocalDateTime.now());

        // Map to DTO
        Event dto = mapper.toStructure(entity);
        assertNotNull(dto);
        assertEquals("payload", dto.getEventPayload());

        // Map back to Entity
        EventEntity mappedEntity = mapper.toEntity(dto);
        assertNotNull(mappedEntity);
        assertArrayEquals("payload".getBytes(), mappedEntity.getEventPayloadBytes());
    }

    @Test
    void testDateMapping() {
        LocalDateTime now = LocalDateTime.now();
        Date date = mapper.map(now);
        assertNotNull(date);

        LocalDateTime localDateTime = mapper.map(date);
        assertEquals(now.getYear(), localDateTime.getYear());
        assertEquals(now.getMonth(), localDateTime.getMonth());
    }

    @Test
    void testPayloadMapping() {
        String payload = "test payload";
        byte[] bytes = mapper.map(payload);
        assertNotNull(bytes);

        String mapped = mapper.map(bytes);
        assertEquals(payload, mapped);
    }
}
