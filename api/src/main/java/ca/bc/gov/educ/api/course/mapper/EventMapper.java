package ca.bc.gov.educ.api.course.mapper;

import ca.bc.gov.educ.api.course.model.dto.Event;
import ca.bc.gov.educ.api.course.model.dto.mapper.UUIDMapper;
import ca.bc.gov.educ.api.course.model.entity.EventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Builder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import java.nio.charset.StandardCharsets;

@Mapper(
        componentModel = "spring",
        uses = {UUIDMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface EventMapper {

    // ENTITY → DTO
    @Mapping(target = "eventPayload", source = "eventPayloadBytes") // let default map(byte[]) handle it
    Event toStructure(EventEntity eventEntity);

    // DTO → ENTITY
    @Mapping(target = "eventPayloadBytes", source = "eventPayload") // let default map(String) handle it
    EventEntity toEntity(Event event);

    // ---- Payload Converters ----
    default String map(byte[] value) {
        return value != null ? new String(value, java.nio.charset.StandardCharsets.UTF_8) : null;
    }

    default byte[] map(String value) {
        return value != null ? value.getBytes(java.nio.charset.StandardCharsets.UTF_8) : null;
    }

    // ---- Date converters ----
    default LocalDateTime map(Date date) {
        return date == null ? null : LocalDateTime.ofInstant(
                Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault()
        );
    }

    default Date map(LocalDateTime dateTime) {
        return dateTime == null ? null : new Date(
                dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        );
    }
}

