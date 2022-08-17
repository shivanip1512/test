package com.cannontech.dr.ecobee.service;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.cannontech.clientutils.YukonLogManager;

/**
 * A very simple error handler for the Ecobee RestTemplate. Logs the status code, and status text when
 * an error occurs.
 */
public class EcobeeErrorHandler implements ResponseErrorHandler {
    private static final Logger log = YukonLogManager.getLogger(EcobeeErrorHandler.class);
    
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        //log the status for troubleshooting
        log.info(response.getStatusCode() + " - " + response.getStatusText());
        //It would be nice to debug log the response body here, but we can't consume the stream twice
        //(here and during message parsing).
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR
                || response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR;
    }

}
