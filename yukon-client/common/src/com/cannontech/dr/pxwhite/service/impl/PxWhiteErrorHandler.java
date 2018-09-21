package com.cannontech.dr.pxwhite.service.impl;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.logging.log4j.core.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;

import com.cannontech.clientutils.YukonLogManager;

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
        log.error("Body: " + StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
        throw new PxWhiteCommunicationException(response);
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus status = response.getStatusCode();
        return status.is4xxClientError() || status.is5xxServerError();
    }
    
}
