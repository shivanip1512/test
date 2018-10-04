package com.cannontech.dr.pxwhite.service.impl;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.logging.log4j.core.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.dr.pxwhite.model.PxWhiteErrorMessage;

/**
 * Error handler for PX White REST API calls. Treats any 4xx or 5xx response as an error, and throws a
 * PxWhiteCommunicationException.
 */
public class PxWhiteErrorHandler implements ResponseErrorHandler {
    private static final Logger log = YukonLogManager.getLogger(PxWhiteErrorHandler.class);
    
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus status = response.getStatusCode();
        String statusText = response.getStatusText();
        log.error("Received error response for request. " + status.value() + " " + status.name() + " - " + statusText);
        String body = StreamUtils.copyToString(response.getBody(), Charset.defaultCharset());
        
        try {
            // Try to parse an error object from JSON in the response body.
            PxWhiteErrorMessage error = parseErrorMessage(body);
            throw new PxWhiteCommunicationException(response, error);
        } catch (IOException e) {
            // Couldn't parse the error object, log the response content so we can at least see it
            log.debug("No error message found in response body, or message was in an unrecognized format.", e);
            log.error("Error Response Body: " + body);
        }
        throw new PxWhiteCommunicationException(response);
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus status = response.getStatusCode();
        return status.is4xxClientError() || status.is5xxServerError();
    }
    
    private static PxWhiteErrorMessage parseErrorMessage(String errorMessageString) throws IOException {
        return JsonUtils.fromJson(errorMessageString, PxWhiteErrorMessage.class);
    }
    
}
