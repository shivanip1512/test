package com.cannontech.web.dr.errorHandler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * This custom error handler is to handle UNPROCESSABLE_ENTITY.
 * For DR API call, we need to process UNPROCESSABLE_ENTITY HttpStatus code
 * instead of default behavior of rest template of throwing exception.
 *
 */
public class CustomDefaultResponseErrorHandler implements ResponseErrorHandler {

    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

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
        errorHandler.handleError(response);
    }

}
