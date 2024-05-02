package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.model.dto.Address;
import ca.bc.gov.educ.api.course.model.dto.School;
import ca.bc.gov.educ.api.course.model.dto.SchoolDetail;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;

@Service
public class InstituteService {

    @Autowired
    private WebClient webClient;

    @SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(InstituteService.class);

    public List<School> getCommonSchools(String accessToken) {
        try {
            return webClient.get().uri("https://institute-api-75e61b-dev.apps.silver.devops.gov.bc.ca/api/v1/institute/school")
                    .headers(h -> {
                        h.setBearerAuth(accessToken);
                    })
                    .retrieve().bodyToMono(new ParameterizedTypeReference<List<School>>(){}).block();
        } catch (WebClientResponseException e) {
            logger.warn("Error getting Common School List");
        } catch (Exception e) {
            logger.error(String.format("Error while calling school-api: %s", e.getMessage()));
        }
        return null;
    }

    public SchoolDetail getCommonSchoolDetailById(String schoolId, String accessToken) {
        try {
            return webClient.get().uri(
                    String.format("https://institute-api-75e61b-dev.apps.silver.devops.gov.bc.ca/api/v1/institute/school/%s", schoolId))
                    .headers(h -> {
                        h.setBearerAuth(accessToken);
                    })
                    .retrieve().bodyToMono(SchoolDetail.class).block();
        } catch (WebClientResponseException e) {
            logger.warn("Error getting Common School Details");
        } catch (Exception e) {
            logger.error(String.format("Error while calling school-api: %s", e.getMessage()));
        }
        return null;
    }

    public List<SchoolDetail> getAllSchoolDetails() {

        String accessToken = getAccessToken();
        List<School> schools = getCommonSchools(accessToken);
        List<SchoolDetail> schoolDetails = new ArrayList<SchoolDetail>();
        Address address = new Address();
        int counter = 1;

        for (School s : schools) {
            SchoolDetail sd = new SchoolDetail();

            if (counter%100 == 0)
                accessToken = getAccessToken();
            sd = getCommonSchoolDetailById(s.getSchoolId(), accessToken);

            address = null;
            if (sd.getAddresses() == null || sd.getAddresses().isEmpty()) {
                logger.debug("," + sd.getMincode() + "," + "," + "," + "," + "," + "," + "," + ",");
            } else {
                address = sd.getAddresses().get(0);
                logger.debug("," + sd.getMincode() + ","
                        + sd.getAddresses().get(0).getAddressLine1() + ","
                        + sd.getAddresses().get(0).getAddressLine2() + ","
                        + sd.getAddresses().get(0).getCity() + ","
                        + sd.getAddresses().get(0).getProvinceCode() + ","
                        + sd.getAddresses().get(0).getCountryCode() + ","
                        + sd.getAddresses().get(0).getPostal() + ","
                        + sd.getAddresses().get(0).getAddressTypeCode() + ","
                );
            }
            schoolDetails.add(sd);
            counter++;
        }
        return schoolDetails;
    }

    public String getAccessToken() {
        String token = "";
        try {
            OAuthClient client = new OAuthClient(new URLConnectionClient());
            OAuthClientRequest request = OAuthClientRequest.tokenLocation("https://soam-dev.apps.silver.devops.gov.bc.ca/auth/realms/master/protocol/openid-connect/token")
                    .setGrantType(GrantType.CLIENT_CREDENTIALS)
                    .setClientId("client-id")
                    .setClientSecret("client-secret")
                    .buildBodyMessage();

            //logger.debug(request.getBody());
            token = client.accessToken(request, OAuth.HttpMethod.POST, OAuthJSONAccessTokenResponse.class).getAccessToken();
            //logger.debug(token);
        } catch (Exception exn) {
            exn.printStackTrace();
        }
        return token;
    }
}
