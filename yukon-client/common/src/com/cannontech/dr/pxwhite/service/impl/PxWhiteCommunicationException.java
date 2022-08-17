package com.cannontech.dr.pxwhite.service.impl;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;

import com.cannontech.dr.pxwhite.model.PxWhiteErrorMessage;

/**
 * Exception thrown when there is a communication error with PX White, or the response has a bad http status.
 */
public class PxWhiteCommunicationException extends IOException {
    private final ClientHttpResponse response;
    private final PxWhiteErrorMessage errorMessage;
    
    public PxWhiteCommunicationException(ClientHttpResponse response) {
        this(response, null);
    }
    
    public PxWhiteCommunicationException(ClientHttpResponse response, PxWhiteErrorMessage errorMessage) {
        this.response = response;
        this.errorMessage = errorMessage;
    }
    
    public ClientHttpResponse getResponse() {
        return response;
    }
    
    public PxWhiteErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
