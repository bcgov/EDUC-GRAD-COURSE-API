package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.ChoreographedEvent;
import ca.bc.gov.educ.api.course.model.StatusEvent;
import ca.bc.gov.educ.api.course.repository.StatusEventRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.UUID;

import static ca.bc.gov.educ.api.course.constants.EventOutcome.COURSE_FOUND;
import static ca.bc.gov.educ.api.course.constants.EventStatus.DB_COMMITTED;
import static ca.bc.gov.educ.api.course.constants.EventStatus.MESSAGE_PUBLISHED;
import static ca.bc.gov.educ.api.course.constants.EventType.GET_STUDENT_COURSE;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class JetStreamEventHandlerServiceTest {
    @Autowired
    JetStreamEventHandlerService jetStreamEventHandlerService;

    @Autowired
    StatusEventRepository statusEventRepository;

    @MockBean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    @MockBean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @MockBean
    public ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    @Qualifier("default")
    public WebClient webClient;

    static final String PEN = "123456789";

    @Test
    public void testUpdateEventStatus_givenDataInDB_shouldUpdateStatus() {
        var studentEvent = statusEventRepository.save(createStudentEvent());
        ChoreographedEvent choreographedEvent = new ChoreographedEvent();
        choreographedEvent.setEventID(studentEvent.getEventId().toString());
        choreographedEvent.setEventOutcome(COURSE_FOUND);
        choreographedEvent.setEventType(GET_STUDENT_COURSE);
        choreographedEvent.setEventPayload(PEN);
        jetStreamEventHandlerService.updateEventStatus(choreographedEvent);
        var results = statusEventRepository.findByEventStatus(MESSAGE_PUBLISHED.toString());
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isNotNull();
    }

    private StatusEvent createStudentEvent() {
        return StatusEvent.builder()
                .eventId(UUID.randomUUID())
                .createDate(LocalDateTime.now())
                .createUser("TEST")
                .eventOutcome(COURSE_FOUND.toString())
                .eventStatus(DB_COMMITTED.toString())
                .eventType(GET_STUDENT_COURSE.toString())
                .eventPayload(PEN)
                .updateDate(LocalDateTime.now())
                .updateUser("TEST")
                .build();
    }
}
