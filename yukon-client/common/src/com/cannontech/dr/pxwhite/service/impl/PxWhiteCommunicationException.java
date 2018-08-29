package com.cannontech.dr.pxwhite.service.impl;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;

/**
 * Exception thrown when there is a communication error with PX White, or the response has a bad http status.
 */
public class PxWhiteCommunicationException extends IOException {
    private final ClientHttpResponse response;
    
    public PxWhiteCommunicationException(ClientHttpResponse response) {
        this.response = response;
    }
    
    public ClientHttpResponse getResponse() {
        return response;
    }
}
