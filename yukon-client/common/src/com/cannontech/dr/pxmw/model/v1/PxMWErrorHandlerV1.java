package com.cannontech.dr.pxmw.model.v1;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.logging.log4j.core.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.JsonUtils;
import com.google.gson.GsonBuilder;

/**
 * Error handler for PX White REST API calls. Treats any 4xx or 5xx response as an error, and throws a
 * PxMWCommunicationException.
 */
public class PxMWErrorHandlerV1 implements ResponseErrorHandler {
    private static final Logger log = YukonLogManager.getLogger(PxMWErrorHandlerV1.class);
    
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus status = response.getStatusCode();
        PxMWCommunicationExceptionV1 exception;
        String body = "";
        try {
            body = StreamUtils.copyToString(response.getBody(), Charset.defaultCharset());
            exception = new PxMWCommunicationExceptionV1(status, parseErrorMessage(body));
            log.error(
                    new GsonBuilder().setPrettyPrinting().create().toJson(exception.getErrorMessage()),
                    exception);
        } catch (Exception e) {
            exception = new PxMWCommunicationExceptionV1(status);
            log.error("Status: {} Error Response Body: {}", status, body, e);
        }
        throw exception;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus status = response.getStatusCode();
        return status.is4xxClientError() || status.is5xxServerError();
    }
    
    /**
     * Parses error from json
     */
    private static PxMWErrorsV1 parseErrorMessage(String errorMessageString) throws IOException {
        return JsonUtils.fromJson(errorMessageString, PxMWErrorsV1.class);
    }
    
}
