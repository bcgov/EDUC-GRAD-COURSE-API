package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.entity.EventEntity;

public interface EventService<T> {

  void processEvent(EventEntity eventEntity);

  String getEventType();
}