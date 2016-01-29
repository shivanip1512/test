package com.cannontech.multispeak.util;

import org.apache.log4j.Logger;
import org.springframework.util.ErrorHandler;

import com.cannontech.clientutils.YukonLogManager;

public class YukonJmsErrorHandler implements ErrorHandler {
    private static final Logger log = YukonLogManager.getLogger(YukonJmsErrorHandler.class);

    @Override
    public void handleError(Throwable t) {
        log.error("Message Listener encountered an error: " + t.getMessage());
        t.printStackTrace();
    }

}
