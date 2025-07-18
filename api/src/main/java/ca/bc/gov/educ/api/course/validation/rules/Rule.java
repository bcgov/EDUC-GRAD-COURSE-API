package ca.bc.gov.educ.api.course.validation.rules;

import java.util.List;

public interface Rule<U, T> {
    boolean shouldExecute(U u, List<T> list);

    List<T> executeValidation(U u);
}

