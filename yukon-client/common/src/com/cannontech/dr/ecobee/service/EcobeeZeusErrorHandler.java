package com.cannontech.dr.ecobee.service;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.message.ZeusErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EcobeeZeusErrorHandler implements ResponseErrorHandler {
    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ZeusErrorResponse errorResponse = mapper.readValue(response.getBody(), ZeusErrorResponse.class);
            log.error("Error occured while communicating with Ecobee API : Error " +
                    errorResponse.getError() + ", Description: " + errorResponse.getDescription());
        } catch (IOException e) {
            String errorResponseBody = StreamUtils.copyToString(response.getBody(), Charset.defaultCharset());
            log.error("Unable to parse error response from Ecobee. Response: " + errorResponseBody);
        }
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR
                || (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
                        && response.getStatusCode() != HttpStatus.CONFLICT);
    }
}
