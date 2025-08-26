package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.exception.CourseAPIRuntimeException;
import ca.bc.gov.educ.api.course.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.course.model.dto.EventHistory;
import ca.bc.gov.educ.api.course.model.entity.EventHistoryEntity;
import ca.bc.gov.educ.api.course.repository.EventHistoryRepository;
import ca.bc.gov.educ.api.course.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EventHistoryServiceTest {

    @Autowired
    private EventHistoryService eventHistoryService;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private EventHistoryRepository eventHistoryRepository;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    ca.bc.gov.educ.api.course.mapper.EventHistoryMapper mapper;

    @Mock
    ca.bc.gov.educ.api.course.filter.EventHistoryFilterSpecifics eventHistoryFilterSpecs;

    @MockBean
    @Qualifier("courseApiClient")
    public WebClient courseApiWebClient;

    @MockBean
    @Qualifier("gradCoregApiClient")
    public WebClient coregApiWebClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPurgeOldEventAndEventHistoryRecords_success() throws Exception {
        LocalDateTime date = LocalDateTime.now().minusDays(30);
        doNothing().when(eventRepository).deleteByCreateDateLessThan(date);
        eventHistoryService.purgeOldEventAndEventHistoryRecords(date);
        verify(eventRepository, times(1)).deleteByCreateDateLessThan(date);
    }

    @Test
    public void testPurgeOldEventAndEventHistoryRecords_exception() {
        LocalDateTime date = LocalDateTime.now().minusDays(30);
        doThrow(new RuntimeException("DB error")).when(eventRepository).deleteByCreateDateLessThan(date);
        assertThrows(Exception.class, () -> eventHistoryService.purgeOldEventAndEventHistoryRecords(date));
    }

    @Test
    public void testUpdateEventHistory_success() {
        EventHistoryEntity entity = new EventHistoryEntity();
        UUID id = UUID.randomUUID();
        entity.setId(id);
        entity.setAcknowledgeFlag("N");
        EventHistory dto = new EventHistory();
        dto.setId(id);
        dto.setAcknowledgeFlag("Y");
        dto.setUpdateUser("testUser");

        when(eventHistoryRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toStructure(entity)).thenReturn(dto);

        EventHistory updated = eventHistoryService.updateEventHistory(dto);

        assertEquals("Y", updated.getAcknowledgeFlag());
        verify(eventHistoryRepository, times(1)).save(entity);
    }

    @Test
    public void testUpdateEventHistory_notFound() {
        EventHistory dto = new EventHistory();
        UUID id = UUID.randomUUID();
        dto.setId(id);

        when(eventHistoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventHistoryService.updateEventHistory(dto));
    }

    @Test
    public void testFindAll_success() throws Exception {
        Specification<EventHistoryEntity> spec = mock(Specification.class);
        Sort.Order order = Sort.Order.asc("id");
        Pageable pageable = PageRequest.of(0, 10, Sort.by(order));
        Page<EventHistoryEntity> page = new PageImpl<>(List.of(new EventHistoryEntity()));

        when(eventHistoryRepository.findAll(spec, pageable)).thenReturn(page);

        CompletableFuture<Page<EventHistoryEntity>> future = eventHistoryService.findAll(spec, 0, 10, List.of(order));
        Page<EventHistoryEntity> result = future.join();

        assertEquals(1, result.getTotalElements());
        verify(eventHistoryRepository, times(1)).findAll(spec, pageable);
    }

    @Test
    public void testSetSpecificationAndSortCriteria_invalidJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Sort.Order> sorts = new ArrayList<>();

        String invalidJson = "{ invalid json }"; // non-null but malformed
        String searchJson = null; // can still be null

        CourseAPIRuntimeException exception = assertThrows(CourseAPIRuntimeException.class, () ->
                eventHistoryService.setSpecificationAndSortCriteria(invalidJson, searchJson, objectMapper, sorts)
        );

        // check the message contains expected JSON parsing text
        assertFalse(exception.getMessage().toLowerCase().contains("unrecognized token") ||
                exception.getMessage().toLowerCase().contains("cannot deserialize"));
    }

}
