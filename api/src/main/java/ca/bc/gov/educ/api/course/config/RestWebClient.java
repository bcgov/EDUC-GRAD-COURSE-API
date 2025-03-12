package ca.bc.gov.educ.api.course.config;

import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import ca.bc.gov.educ.api.course.util.LogHelper;
import ca.bc.gov.educ.api.course.util.ThreadLocalStateUtil;
import io.netty.handler.logging.LogLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class RestWebClient {

    LogHelper logHelper;
    @Autowired
    EducCourseApiConstants constants;

    private final HttpClient httpClient;

    public RestWebClient() {
        this.httpClient = HttpClient.create(ConnectionProvider.create("course-api")).compress(true)
                .resolver(spec -> spec.queryTimeout(Duration.ofMillis(200)).trace("DNS", LogLevel.TRACE));
        this.httpClient.warmup().block();
    }

    @Bean
    public WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction filter = new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        filter.setDefaultClientRegistrationId("course-api-client");
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        return WebClient.builder()
                .uriBuilderFactory(defaultUriBuilderFactory)
                .filter(setRequestHeaders())
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(codecs -> codecs
                                .defaultCodecs()
                                .maxInMemorySize(50 * 1024 * 1024))
                        .build())
                .apply(filter.oauth2Configuration())
                .filter(this.log())
                .build();
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService clientService) {
        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();
        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, clientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    private ExchangeFilterFunction setRequestHeaders() {
        return (clientRequest, next) -> {
            ClientRequest modifiedRequest = ClientRequest.from(clientRequest)
                    .header(EducCourseApiConstants.CORRELATION_ID, ThreadLocalStateUtil.getCorrelationID())
                    .header(EducCourseApiConstants.USER_NAME, ThreadLocalStateUtil.getCurrentUser())
                    .header(EducCourseApiConstants.REQUEST_SOURCE, EducCourseApiConstants.API_NAME)
                    .build();
            return next.exchange(modifiedRequest);
        };
    }

    private ExchangeFilterFunction log() {
        return (clientRequest, next) -> next
                .exchange(clientRequest)
                .doOnNext((clientResponse -> logHelper.logClientHttpReqResponseDetails(
                        clientRequest.method(),
                        clientRequest.url().toString(),
                        clientResponse.statusCode().value(),
                        clientRequest.headers().get(EducCourseApiConstants.CORRELATION_ID),
                        clientRequest.headers().get(EducCourseApiConstants.REQUEST_SOURCE),
                        constants.isSplunkLogHelperEnabled())
                ));
    }

}
