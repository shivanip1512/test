package com.cannontech.dr.eatonCloud.model.v1;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.logging.log4j.core.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.JsonUtils;

/**
 * Error handler for PX White REST API calls. Treats any 4xx or 5xx response as an error, and throws a
 * EatonCloudCommunicationException.
 */
public class EatonCloudErrorHandlerV1 implements ResponseErrorHandler {
    private static final Logger log = YukonLogManager.getLogger(EatonCloudErrorHandlerV1.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus status = response.getStatusCode();
        EatonCloudCommunicationExceptionV1 exception;
        String body = "";
        try {
            body = StreamUtils.copyToString(response.getBody(), Charset.defaultCharset());
            EatonCloudErrorV1 error = parseErrorMessage(body);
            exception = new EatonCloudCommunicationExceptionV1(error);
        } catch (Exception e) {
            exception = new EatonCloudCommunicationExceptionV1();
            log.error("Unable to parse error for status: {} response body: {}", status, body, e);
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
    private static EatonCloudErrorV1 parseErrorMessage(String errorMessageString) throws IOException {
        return JsonUtils.fromJson(errorMessageString, EatonCloudErrorV1.class);
    }
    
}
