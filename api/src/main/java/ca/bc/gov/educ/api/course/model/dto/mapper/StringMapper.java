package ca.bc.gov.educ.api.course.model.dto.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class StringMapper {

    /**
     * Map string.
     *
     * @param value the value
     * @return the string
     */
    public String map(String value) {
        if (StringUtils.isNotEmpty(value)) {
            return value.trim();
        }
        return value;
    }
}

