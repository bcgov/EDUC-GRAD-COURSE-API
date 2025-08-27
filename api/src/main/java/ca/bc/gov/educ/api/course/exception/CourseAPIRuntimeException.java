package ca.bc.gov.educ.api.course.exception;

import java.io.Serial;

public class CourseAPIRuntimeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CourseAPIRuntimeException(String message) {
        super(message);
    }
}
