package ca.bc.gov.educ.api.course.service;

import static org.assertj.core.api.Assertions.assertThat;

import ca.bc.gov.educ.api.course.model.dto.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@Slf4j
class EventHandlerServiceIntegrationTest {

  @Autowired
  EventHandlerService eventHandlerServiceUnderTest;

  @MockBean
  @Qualifier("gradCoregApiClient")
  private WebClient coregApiWebClient;

  @MockBean
  @Qualifier("courseApiClient")
  private WebClient courseApiWebClient;

  @Nested
  @DisplayName("GET_COURSE_REQUIREMENTS Event Tests")
  class GetCourseRequirementsEventTests {

    @Test
    @DisplayName("Should return course requirements when course codes provided")
    void testHandleEvent_givenEventTypeGET_COURSE_REQUIREMENTS_whenCourseCodesProvided_shouldReturnRequirements() throws Exception {
      // Given
      String eventPayload = "[\"CLE\",\"CLC\"]";

      Event event = new Event();
      event.setEventType("GET_COURSE_REQUIREMENTS");
      event.setEventId(UUID.randomUUID());
      event.setEventPayload(eventPayload);

      // When
      byte[] responseBytes = eventHandlerServiceUnderTest.handleGetCourseRequirementsByCourseIDEvent(event);

      // Then
      assertThat(responseBytes).isNotEmpty();
    }

    @Test
    @DisplayName("Should handle empty course codes list")
    void testHandleEvent_givenEventTypeGET_COURSE_REQUIREMENTS_whenEmptyCourseCodes_shouldReturnEmptyList() throws Exception {
      // Given
      List<String> courseCodes = List.of();
      String eventPayload = new ObjectMapper().writeValueAsString(courseCodes);

      Event event = new Event();
      event.setEventType("GET_COURSE_REQUIREMENTS");
      event.setEventId(UUID.randomUUID());
      event.setEventPayload(eventPayload);

      // When
      byte[] responseBytes = eventHandlerServiceUnderTest.handleGetCourseRequirementsByCourseIDEvent(event);

      // Then
      assertThat(responseBytes).isNotEmpty();
    }
  }

  @Nested
  @DisplayName("GET_COURSE_RESTRICTIONS Event Tests")
  class GetCourseRestrictionsEventTests {

    @Test
    @DisplayName("Should return course restrictions when course codes provided")
    void testHandleEvent_givenEventTypeGET_COURSE_RESTRICTIONS_whenCourseCodesProvided_shouldReturnRestrictions() throws Exception {
      // Given
      String eventPayload = "[\"CLE\",\"CLC\"]";

      Event event = new Event();
      event.setEventType("GET_COURSE_RESTRICTIONS");
      event.setEventId(UUID.randomUUID());
      event.setEventPayload(eventPayload);

      // When
      byte[] responseBytes = eventHandlerServiceUnderTest.handleGetCourseRestrictionsByCourseIDEvent(event);

      // Then
      assertThat(responseBytes).isNotEmpty();
    }

    @Test
    @DisplayName("Should handle empty course codes list")
    void testHandleEvent_givenEventTypeGET_COURSE_RESTRICTIONS_whenEmptyCourseCodes_shouldReturnEmptyList() throws Exception {
      // Given
      List<String> courseCodes = List.of();
      String eventPayload = new ObjectMapper().writeValueAsString(courseCodes);

      Event event = new Event();
      event.setEventType("GET_COURSE_RESTRICTIONS");
      event.setEventId(UUID.randomUUID());
      event.setEventPayload(eventPayload);

      // When
      byte[] responseBytes = eventHandlerServiceUnderTest.handleGetCourseRestrictionsByCourseIDEvent(event);

      // Then
      assertThat(responseBytes).isNotEmpty();
    }
  }
}