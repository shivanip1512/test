package com.cannontech.web.api.client.errorHandler;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.core.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.api.error.model.ApiErrorModel;
import com.cannontech.web.api.errorHandler.model.ApiError;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

/**
 * This custom error handler is to handle Exception from API calls.
 * Any exception thrown from API will have exception message added to RestClientException.
 * 
 * UNPROCESSABLE_ENTITY HttpStatus is special case (for binding errors),
 * so have to preserve the response body.
 */
public class CustomDefaultResponseErrorHandler implements ResponseErrorHandler {
    private static final Logger log = YukonLogManager.getLogger(CustomDefaultResponseErrorHandler.class);
    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();
    private ObjectReader reader;

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        reader = mapper.reader();
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus httpStatus = response.getStatusCode();
        if (httpStatus == HttpStatus.UNPROCESSABLE_ENTITY) {
            return false;
        } else {
            return errorHandler.hasError(response);
        }
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus status = response.getStatusCode();
        log.error("Received error response for request. " + status.value() + " " + status.name());
        String body = StreamUtils.copyToString(response.getBody(), Charset.defaultCharset());
        try {
            // Try to retrieve error massage from JSON in the response body.
            String errorMessage = parseErrorMessage(body);
            throw new RestClientException(errorMessage);
        } catch (IOException e) {
            log.debug("No error message found in response body, or message was in an unrecognized format.", e);
            log.error("Error Response Body: " + body);
        }
    }

    /**
     * Parse the JSON String with FAIL_ON_UNKNOWN_PROPERTIES. First try to convert the JSON String to ApiError object, and if it
     * fails convert it to ApiErrorModel.
     */
    private String parseErrorMessage(String errorMessageString) throws IOException {
        try {
            ApiError error = reader.withType(ApiError.class).readValue(errorMessageString, ApiError.class);
            return error.getMessage();
        } catch (UnrecognizedPropertyException e) {
            // Only catch UnrecognizedPropertyException.
            ApiErrorModel error = reader.withType(ApiErrorModel.class).readValue(errorMessageString, ApiErrorModel.class);
            return error.getDetail();
        }
    }
}
