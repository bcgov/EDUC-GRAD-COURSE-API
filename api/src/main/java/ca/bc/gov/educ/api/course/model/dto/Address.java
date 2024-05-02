package ca.bc.gov.educ.api.course.model.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Address {
    private String schoolId;
    private String schoolAddressId;
    private String countryCode;
    private String provinceCode;
    private String addressTypeCode;
    private String postal;
    private String city;
    private String addressLine1;
    private String addressLine2;
    private String createUser;
    private String updateUser;
    private String createDate;
    private String updateDate;
}