package ca.bc.gov.educ.api.course.service;

import ca.bc.gov.educ.api.course.exception.ServiceException;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import ca.bc.gov.educ.api.course.util.ThreadLocalStateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class RESTService {
    private final WebClient webClient;
    private static final String ERROR_5xx = "5xx error.";
    private static final String SERVICE_FAILED_ERROR = "Service failed to process after max retries.";

    @Autowired
    public RESTService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Uses this method with a service client
     * @param url
     * @param clazz
     * @return
     * @param <T>
     */
    public <T> T get(String url, Class<T> clazz) {
        T obj;
        try {
            obj = webClient
                    .get()
                    .uri(url)
                    .headers(h -> { h.set(EducCourseApiConstants.CORRELATION_ID, ThreadLocalStateUtil.getCorrelationID()); })
                    .retrieve()
                    // if 5xx errors, throw Service error
                    .onStatus(HttpStatusCode::is5xxServerError,
                            clientResponse -> Mono.error(new ServiceException(getErrorMessage(url, ERROR_5xx), clientResponse.statusCode().value())))
                    .bodyToMono(clazz)
                    // only does retry if initial error was 5xx as service may be temporarily down
                    // 4xx errors will always happen if 404, 401, 403 etc, so does not retry
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                            .filter(ServiceException.class::isInstance)
                            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                                throw new ServiceException(getErrorMessage(url, SERVICE_FAILED_ERROR), HttpStatus.SERVICE_UNAVAILABLE.value());
                            }))
                    .block();
        } catch (Exception e) {
            // catches IOExceptions and the like
            throw new ServiceException(getErrorMessage(
                    url,
                    e.getLocalizedMessage()),
                    (e instanceof WebClientResponseException) ? ((WebClientResponseException) e).getStatusCode().value() : HttpStatus.SERVICE_UNAVAILABLE.value(),
                    e);
        }
        return obj;
    }

    public <T> T post(String url, Object body, Class<T> clazz) {
        T obj;
        try {
            obj = webClient.post()
                    .uri(url)
                    .headers(h -> { h.set(EducCourseApiConstants.CORRELATION_ID, ThreadLocalStateUtil.getCorrelationID()); })
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .onStatus(HttpStatusCode::is5xxServerError,
                            clientResponse -> Mono.error(new ServiceException(getErrorMessage(url, ERROR_5xx), clientResponse.statusCode().value())))
                    .bodyToMono(clazz)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                            .filter(ServiceException.class::isInstance)
                            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                                throw new ServiceException(getErrorMessage(url, SERVICE_FAILED_ERROR), HttpStatus.SERVICE_UNAVAILABLE.value());
                            }))
                    .block();
        } catch (Exception e) {
            throw new ServiceException(getErrorMessage(
                    url,
                    e.getLocalizedMessage()),
                    (e instanceof WebClientResponseException) ? ((WebClientResponseException) e).getStatusCode().value() : HttpStatus.SERVICE_UNAVAILABLE.value(),
                    e);
        }
        return obj;
    }

    private String getErrorMessage(String url, String errorMessage) {
        return "Service failed to process at url: " + url + " due to: " + errorMessage;
    }

}
