package ca.bc.gov.educ.api.course.controller;

import ca.bc.gov.educ.api.course.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.course.mapper.EventHistoryMapper;
import ca.bc.gov.educ.api.course.model.dto.EventHistory;
import ca.bc.gov.educ.api.course.model.entity.EventHistoryEntity;
import ca.bc.gov.educ.api.course.service.EventHistoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class EventHistoryControllerTest {

    @Mock
    private EventHistoryService eventHistoryService;

    @Mock
    private EventHistoryMapper mapper;

    @InjectMocks
    private EventHistoryController controller;

    private EventHistoryEntity eventHistoryEntity;
    private EventHistory eventHistoryDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        eventHistoryEntity = new EventHistoryEntity();
        eventHistoryDto = new EventHistory();
    }

    @Test
    void testFindAll_ReturnsPaginatedEventHistory() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
        Page<EventHistoryEntity> entityPage =
                new PageImpl<>(List.of(eventHistoryEntity), pageable, 1);

        when(eventHistoryService.setSpecificationAndSortCriteria(anyString(), anyString(), any(), anyList()))
                .thenReturn(null);
        when(eventHistoryService.findAll(any(), anyInt(), anyInt(), anyList()))
                .thenReturn(CompletableFuture.completedFuture(entityPage));
        when(mapper.toStructure(any(EventHistoryEntity.class)))
                .thenReturn(eventHistoryDto);

        CompletableFuture<Page<EventHistory>> future =
                controller.findAll(0, 10, "", null);

        Page<EventHistory> result = future.get();

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(eventHistoryService, times(1))
                .findAll(any(), eq(0), eq(10), anyList());
    }

    @Test
    void testUpdateEventHistory_Success() {
        when(eventHistoryService.updateEventHistory(any(EventHistory.class)))
                .thenReturn(eventHistoryDto);

        EventHistory result = controller.updateEventHistory(eventHistoryDto);

        assertThat(result).isNotNull();
        verify(eventHistoryService, times(1)).updateEventHistory(eventHistoryDto);
    }

    @Test
    void testUpdateEventHistory_NotFound() {
        when(eventHistoryService.updateEventHistory(any(EventHistory.class)))
                .thenThrow(new EntityNotFoundException("Event history not found"));

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            controller.updateEventHistory(eventHistoryDto);
        });

        assertThat(exception.getMessage()).contains("not found");
        verify(eventHistoryService, times(1)).updateEventHistory(any(EventHistory.class));
    }

    @Test
    void testUpdateEventHistory_BadRequest() {
        when(eventHistoryService.updateEventHistory(any(EventHistory.class)))
                .thenThrow(new IllegalArgumentException("Invalid request"));

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            controller.updateEventHistory(eventHistoryDto);
        });

        assertThat(exception.getMessage()).contains("Invalid request");
        verify(eventHistoryService, times(1)).updateEventHistory(any(EventHistory.class));
    }
}
