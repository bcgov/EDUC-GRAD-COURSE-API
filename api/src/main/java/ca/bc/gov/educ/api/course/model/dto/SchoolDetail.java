package ca.bc.gov.educ.api.course.model.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class SchoolDetail {
    private String schoolId;
    private String districtId;
    private String mincode;
    private List<Address> addresses;

    //private String independentAuthorityId;
    //private String schoolNumber;
    //private String faxNumber;
    //private String phoneNumber;
    //private String email;
    //private String website;
    //private String displayName;
    //private String displayNameNoSpecialChars;
    //private String schoolReportingRequirementCode;
    //private String schoolOrganizationCode;
    //private String schoolCategoryCode;
    //private String facilityTypeCode;
    //private String openedDate;
    //private String closedDate;
    //private String createUser;
    //private String updateUser;
    //private String createDate;
    //private String updateDate;
}