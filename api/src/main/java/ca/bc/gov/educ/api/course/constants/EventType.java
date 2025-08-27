package ca.bc.gov.educ.api.course.constants;

import lombok.Getter;

/**
 * The enum Event type.
 */
@Getter
public enum EventType {
    GET_STUDENT_COURSE(40),
    COURSE_CREATED(41),
    COURSE_UPDATED(42),
    COURSE_DELETED(43);

    private final long code;

    EventType(long code) {
        this.code = code;
    }

    // Optional: get enum by code
    public static EventType fromCode(long code) {
        for (EventType type : EventType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
