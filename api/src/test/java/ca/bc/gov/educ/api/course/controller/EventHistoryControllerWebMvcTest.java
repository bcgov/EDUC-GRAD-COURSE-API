package ca.bc.gov.educ.api.course.controller;

import ca.bc.gov.educ.api.course.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.course.model.dto.EventHistory;
import ca.bc.gov.educ.api.course.model.entity.EventHistoryEntity;
import ca.bc.gov.educ.api.course.service.EventHistoryService;
import ca.bc.gov.educ.api.course.mapper.EventHistoryMapper;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(EventHistoryController.class)
class EventHistoryControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventHistoryService eventHistoryService;

    @MockBean
    private EventHistoryMapper mapper;

    @MockBean
    private ca.bc.gov.educ.api.course.util.GradValidation gradValidation;

    @MockBean
    private ca.bc.gov.educ.api.course.config.GradCommonConfig gradCommonConfig;

    @MockBean
    private EducCourseApiConstants educCourseApiConstants;

    @WithMockUser
    @Test
    void testFindAllEndpoint() throws Exception {
        EventHistoryEntity entity = new EventHistoryEntity();
        EventHistory dto = new EventHistory();

        Page<EventHistoryEntity> entityPage = new PageImpl<>(List.of(entity));

        when(eventHistoryService.setSpecificationAndSortCriteria(any(), any(), any(), anyList()))
                .thenReturn(null);

        // return Page<EventHistoryEntity> instead of Page<EventHistory>
        when(eventHistoryService.findAll(any(), anyInt(), anyInt(), anyList()))
                .thenReturn(CompletableFuture.completedFuture(entityPage));

        // mapper converts entity → dto
        when(mapper.toStructure(any(EventHistoryEntity.class))).thenReturn(dto);

        mockMvc.perform(get("/api/v1/course/event/history/paginated?pageNumber=0&pageSize=10"))
                .andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    void testUpdateEventHistoryEndpoint() throws Exception {
        EventHistory dto = new EventHistory();
        when(eventHistoryService.updateEventHistory(any(EventHistory.class)))
                .thenReturn(dto);

        mockMvc.perform(put("/api/v1/course/event/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + UUID.randomUUID().toString() + "\"},\"acknowledgeFlag\": \"Y\"")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateEventHistoryNotFound() throws Exception {
        // Mock service to throw exception or return null
        EventHistory eventHistory = new EventHistory();
        eventHistory.setId(UUID.randomUUID());

        // If your service throws an exception for not found:
        when(eventHistoryService.updateEventHistory(any(EventHistory.class)))
                .thenThrow(new EntityNotFoundException("Event history not found"));

        mockMvc.perform(put("/api/v1/course/event/history")
                        .with(csrf())
                        .with(user("test").authorities(new SimpleGrantedAuthority("SCOPE_WRITE_EVENT_HISTORY")))
                        .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"" + UUID.randomUUID().toString() + "\"},\"acknowledgeFlag\": \"Y\""))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    void testUpdateEventHistoryEndpoint_BadRequest_InvalidJson() throws Exception {
        mockMvc.perform(put("/api/v1/course/event/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json}") // malformed JSON
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    void testUpdateEventHistoryEndpoint_BadRequest_MissingRequiredField() throws Exception {
        // Assuming eventId is required
        mockMvc.perform(put("/api/v1/course/event/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
