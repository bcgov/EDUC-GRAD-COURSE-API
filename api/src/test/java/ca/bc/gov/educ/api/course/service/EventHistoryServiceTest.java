package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.exception.CourseAPIRuntimeException;
import ca.bc.gov.educ.api.course.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.course.exception.InvalidParameterException;
import ca.bc.gov.educ.api.course.filter.EventHistoryFilterSpecifics;
import ca.bc.gov.educ.api.course.mapper.EventHistoryMapper;
import ca.bc.gov.educ.api.course.model.dto.EventHistory;
import ca.bc.gov.educ.api.course.model.dto.search.*;
import ca.bc.gov.educ.api.course.model.entity.EventHistoryEntity;
import ca.bc.gov.educ.api.course.repository.EventHistoryRepository;
import ca.bc.gov.educ.api.course.repository.EventRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventHistoryServiceTest {

    @InjectMocks
    private EventHistoryService service;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventHistoryRepository eventHistoryRepository;

    @Mock
    private EventHistoryMapper mapper;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EventHistoryFilterSpecifics filterSpecs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void purgeOldEventAndEventHistoryRecords_success() throws Exception {
        LocalDateTime date = LocalDateTime.now().minusDays(30);
        doNothing().when(eventRepository).deleteByCreateDateLessThan(date);

        assertDoesNotThrow(() -> service.purgeOldEventAndEventHistoryRecords(date));
        verify(eventRepository, times(1)).deleteByCreateDateLessThan(date);
    }

    @Test
    void purgeOldEventAndEventHistoryRecords_exception() {
        LocalDateTime date = LocalDateTime.now().minusDays(30);
        doThrow(new RuntimeException("DB error")).when(eventRepository).deleteByCreateDateLessThan(date);

        Exception ex = assertThrows(Exception.class, () -> service.purgeOldEventAndEventHistoryRecords(date));
        assertTrue(ex.getMessage().contains("DB error"));
    }

    @Test
    void updateEventHistory_success() {
        UUID id = UUID.randomUUID();
        EventHistoryEntity entity = new EventHistoryEntity();
        entity.setId(id);
        entity.setAcknowledgeFlag("N");

        EventHistory dto = new EventHistory();
        dto.setId(id);
        dto.setAcknowledgeFlag("Y");
        dto.setUpdateUser("testUser");

        when(eventHistoryRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toStructure(entity)).thenReturn(dto);

        EventHistory updated = service.updateEventHistory(dto);

        assertEquals("Y", updated.getAcknowledgeFlag());
        verify(eventHistoryRepository, times(1)).save(entity);
    }

    @Test
    void updateEventHistory_notFound() {
        UUID id = UUID.randomUUID();
        EventHistory dto = new EventHistory();
        dto.setId(id);

        when(eventHistoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateEventHistory(dto));
    }

    @Test
    void findAll_success() throws Exception {
        Specification<EventHistoryEntity> spec = mock(Specification.class);
        Sort.Order order = Sort.Order.asc("id");
        Pageable pageable = PageRequest.of(0, 10, Sort.by(order));
        Page<EventHistoryEntity> page = new PageImpl<>(List.of(new EventHistoryEntity()));

        when(eventHistoryRepository.findAll(spec, pageable)).thenReturn(page);

        CompletableFuture<Page<EventHistoryEntity>> future = service.findAll(spec, 0, 10, List.of(order));
        Page<EventHistoryEntity> result = future.join();

        assertEquals(1, result.getTotalElements());
        verify(eventHistoryRepository, times(1)).findAll(spec, pageable);
    }

    @Test
    void setSpecificationAndSortCriteria_invalidJson() throws Exception {
        ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        List<Sort.Order> sorts = new ArrayList<>();
        String invalidJson = "{ invalid json }";

        // Force readValue to throw JsonProcessingException
        when(mockObjectMapper.readValue(eq(invalidJson), any(TypeReference.class)))
                .thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("Invalid JSON") {});

        CourseAPIRuntimeException exception = assertThrows(CourseAPIRuntimeException.class, () ->
                service.setSpecificationAndSortCriteria(invalidJson, null, mockObjectMapper, sorts)
        );

        assertTrue(exception.getMessage().contains("Invalid JSON"));
    }

    @Test
    void getDefaultSearchCriteria_returnsExpectedSearch() {
        // Reflection call since it's private
        Search defaultSearch = (Search) org.springframework.test.util.ReflectionTestUtils
                .invokeMethod(service, "getDefaultSearchCriteria");

        assertNotNull(defaultSearch);
        assertEquals(1, defaultSearch.getSearchCriteriaList().size());
        assertEquals("event.eventType", defaultSearch.getSearchCriteriaList().get(0).getKey());
    }

    @Test
    void getTypeSpecification_allTypes() throws Exception {
        // Mock all type methods
        when(filterSpecs.getStringTypeSpecification(any(), any(), any()))
                .thenReturn((root, query, cb) -> cb.conjunction());
        when(filterSpecs.getDateTimeTypeSpecification(any(), any(), any()))
                .thenReturn((root, query, cb) -> cb.conjunction());
        when(filterSpecs.getLongTypeSpecification(any(), any(), any()))
                .thenReturn((root, query, cb) -> cb.conjunction());
        when(filterSpecs.getIntegerTypeSpecification(any(), any(), any()))
                .thenReturn((root, query, cb) -> cb.conjunction());
        when(filterSpecs.getDateTypeSpecification(any(), any(), any()))
                .thenReturn((root, query, cb) -> cb.conjunction());

        when(filterSpecs.getUUIDTypeSpecification(any(), any(), any()))
                .thenReturn((root, query, cb) -> cb.conjunction());
        when(filterSpecs.getBooleanTypeSpecification(any(), any(), any()))
                .thenReturn((root, query, cb) -> cb.conjunction());

        for (ValueType type : ValueType.values()) {
            Specification<EventHistoryEntity> spec = (Specification<EventHistoryEntity>)
                    org.springframework.test.util.ReflectionTestUtils.invokeMethod(service, "getTypeSpecification", "key", FilterOperation.EQUAL, "value", type);
            assertNotNull(spec, "Specification should not be null for type: " + type);
        }
    }

    @Test
    void getEventHistorySpecification_invalidCriteria_throwsException() {
        SearchCriteria criteria = new SearchCriteria(); // null key, operation
        List<SearchCriteria> list = List.of(criteria);

        assertThrows(InvalidParameterException.class,
                () -> org.springframework.test.util.ReflectionTestUtils.invokeMethod(service,
                        "getEventHistorySpecification", list));
    }

    @Test
    void getSpecifications_combinesSpecificationsCorrectly() {
        SearchCriteria criteria1 = new SearchCriteria();
        criteria1.setKey("field1");
        criteria1.setOperation(FilterOperation.EQUAL);
        criteria1.setValue("value1");
        criteria1.setValueType(ValueType.STRING);
        criteria1.setCondition(Condition.AND);

        SearchCriteria criteria2 = new SearchCriteria();
        criteria2.setKey("field2");
        criteria2.setOperation(FilterOperation.EQUAL);
        criteria2.setValue("value2");
        criteria2.setValueType(ValueType.STRING);
        criteria2.setCondition(Condition.OR);

        Search search1 = Search.builder().searchCriteriaList(List.of(criteria1)).condition(Condition.AND).build();
        Search search2 = Search.builder().searchCriteriaList(List.of(criteria2)).condition(Condition.OR).build();

        // Mock the type specifications
        when(filterSpecs.getStringTypeSpecification(any(), any(), any()))
                .thenAnswer(inv -> (Specification<EventHistoryEntity>) (root, query, cb) -> cb.conjunction());

        Specification<EventHistoryEntity> spec = null;
        spec = (Specification<EventHistoryEntity>) org.springframework.test.util.ReflectionTestUtils.invokeMethod(
                service, "getSpecifications", spec, 0, search1);
        assertNotNull(spec);

        // Combine with another search using OR
        spec = (Specification<EventHistoryEntity>) org.springframework.test.util.ReflectionTestUtils.invokeMethod(
                service, "getSpecifications", spec, 1, search2);
        assertNotNull(spec);
    }

    @Test
    void getSpecificationPerGroup_andOrLogic() {
        SearchCriteria criteriaAnd = new SearchCriteria();
        criteriaAnd.setCondition(Condition.AND);
        Specification<EventHistoryEntity> spec1 = (root, query, cb) -> cb.conjunction();
        Specification<EventHistoryEntity> spec2 = (root, query, cb) -> cb.conjunction();

        // First spec, i = 0
        Specification<EventHistoryEntity> combined = (Specification<EventHistoryEntity>) org.springframework.test.util.ReflectionTestUtils.invokeMethod(
                service, "getSpecificationPerGroup", null, 0, criteriaAnd, spec1);
        assertNotNull(combined);

        // Second spec with AND
        combined = (Specification<EventHistoryEntity>) org.springframework.test.util.ReflectionTestUtils.invokeMethod(
                service, "getSpecificationPerGroup", combined, 1, criteriaAnd, spec2);
        assertNotNull(combined);

        // OR condition
        SearchCriteria criteriaOr = new SearchCriteria();
        criteriaOr.setCondition(Condition.OR);
        combined = (Specification<EventHistoryEntity>) org.springframework.test.util.ReflectionTestUtils.invokeMethod(
                service, "getSpecificationPerGroup", combined, 2, criteriaOr, spec2);
        assertNotNull(combined);
    }

    @Test
    void getEventHistorySpecification_withValidCriteria() {
        SearchCriteria criteria = new SearchCriteria();
        criteria.setKey("field");
        criteria.setOperation(FilterOperation.EQUAL);
        criteria.setValue("val");
        criteria.setValueType(ValueType.STRING);

        when(filterSpecs.getStringTypeSpecification(any(), any(), any()))
                .thenReturn((root, query, cb) -> cb.conjunction());

        List<SearchCriteria> list = List.of(criteria);
        Specification<EventHistoryEntity> spec = (Specification<EventHistoryEntity>) org.springframework.test.util.ReflectionTestUtils.invokeMethod(
                service, "getEventHistorySpecification", list);
        assertNotNull(spec);
    }
}