package com.cannontech.web.api.client.errorHandler;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.logging.log4j.core.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.web.api.errorHandler.model.ApiError;

/**
 * This custom error handler is to handle Exception from API calls.
 * Any exception thrown from API will be converted to ApiException here, to maintain body.
 * 
 * UNPROCESSABLE_ENTITY HttpStatus is special case (for binding errors),
 * so have to preserve the response body.
 */
public class CustomDefaultResponseErrorHandler implements ResponseErrorHandler {
    private static final Logger log = YukonLogManager.getLogger(CustomDefaultResponseErrorHandler.class);
    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus httpStatus = response.getStatusCode();
        if (httpStatus == HttpStatus.UNPROCESSABLE_ENTITY) {
            return false;
        } else if (httpStatus.is4xxClientError() || httpStatus.is5xxServerError()) {
            return errorHandler.hasError(response);
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
            // Try to parse an error object from JSON in the response body.
            ApiError error = parseErrorMessage(body);
            throw new RestClientException(error.getMessage());
        } catch (IOException e) {
            log.debug("No error message found in response body, or message was in an unrecognized format.", e);
            log.error("Error Response Body: " + body);
        }
    }

    private static ApiError parseErrorMessage(String errorMessageString) throws IOException {
        return JsonUtils.fromJson(errorMessageString, ApiError.class);
    }
}
