package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.exception.ServiceException;
import ca.bc.gov.educ.api.course.model.entity.EventEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class CourseEventsServiceTest {

    private CourseEventsService service;
    private EventEntity eventEntity;

    @MockBean
    @Qualifier("courseApiClient")
    public WebClient courseApiWebClient;

    @MockBean
    @Qualifier("gradCoregApiClient")
    public WebClient coregApiWebClient;

    @BeforeEach
    void setUp() {
        // Concrete test subclass
        service = Mockito.spy(new CourseEventsService() {
            @Override
            protected void updateEvent(EventEntity entity, boolean isProcessed) throws ServiceException {
                // no-op stub
            }

            @Override
            public String getEventType() {
                return "TEST_EVENT"; // required by EventService
            }
        });

        eventEntity = new EventEntity();
        eventEntity.setEventType("COURSE_CREATED");
    }

    @Test
    void processEvent_shouldCallUpdateEvent_whenNoException() throws ServiceException {
        service.processEvent(eventEntity);

        verify(service, times(1)).updateEvent(eventEntity, true);
    }

    @Test
    void processEvent_shouldCatchServiceException() throws ServiceException {
        doThrow(new ServiceException("Update failed"))
                .when(service).updateEvent(eventEntity, true);

        service.processEvent(eventEntity);

        verify(service, times(1)).updateEvent(eventEntity, true);
        // no exception propagated because processEvent catches it
    }
}

