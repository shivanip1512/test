package com.cannontech.dr.edgeDr;

import org.apache.logging.log4j.core.Logger;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.cannontech.clientutils.YukonLogManager;

public class EdgeDrErrorHandler extends DefaultResponseErrorHandler{
    private static final Logger log = YukonLogManager.getLogger(EdgeDrErrorHandler.class);
    
    @Override
    public void handleError(ClientHttpResponse response) {
        log.error("An error occured sending webhook request: {}", response);
    }
}
