package ru.practicum.statistic.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statistic.client.exception.ClientException;
import java.util.Map;

public abstract class BaseClient {

    protected final RestTemplate rest;

    private final ParameterizedTypeReference<Object> OBJECT_PARAMETERIZED_TR =
            new ParameterizedTypeReference<>() {};

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, null);
    }

    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, OBJECT_PARAMETERIZED_TR, null);
    }

    protected <K> ResponseEntity<K> get(
            String path,
            ParameterizedTypeReference<K> parameterizedTypeReference,
            @Nullable Map<String, Object> parameters
    ) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, parameterizedTypeReference,null);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, parameters, OBJECT_PARAMETERIZED_TR, body);
    }

    protected <T> ResponseEntity<Object> put(String path, T body) {
        return put(path, null, body);
    }

    protected <T> ResponseEntity<Object> put(String path, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PUT, path, parameters, OBJECT_PARAMETERIZED_TR, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return patch(path, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path) {
        return patch(path, null, null);
    }

    protected <T> ResponseEntity<Object> patch(String path, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, parameters, OBJECT_PARAMETERIZED_TR, body);
    }

    protected ResponseEntity<Object> delete(String path) {
        return delete(path, null);
    }

    protected ResponseEntity<Object> delete(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, parameters, OBJECT_PARAMETERIZED_TR, null);
    }

    private <T, K> ResponseEntity<K> makeAndSendRequest(
            HttpMethod method,
            String path,
            @Nullable Map<String, Object> parameters,
            ParameterizedTypeReference<K> parameterizedTR,
            @Nullable T body
    ) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body);

        ResponseEntity<K> response;
        try {
            if (parameters != null) {
                response = rest.exchange(path, method, requestEntity, parameterizedTR, parameters);
            } else {
                response = rest.exchange(path, method, requestEntity, parameterizedTR);
            }
        } catch (HttpStatusCodeException e) {
            throw new ClientException(e.getStatusCode(), e.getMessage(), e.getResponseBodyAsString());
        }
        return prepareResponse(response);
    }

    private static <K> ResponseEntity<K> prepareResponse(ResponseEntity<K> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

}
