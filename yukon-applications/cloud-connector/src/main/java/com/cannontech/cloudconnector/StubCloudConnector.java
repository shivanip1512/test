package com.cannontech.cloudconnector;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class StubCloudConnector implements CloudConnector {

    private static Logger log = (Logger) LogManager.getLogger(StubCloudConnector.class);
    
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    
    @Override
    public void start() {

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("CloudConnector is currently running");
        }, 10, 10, TimeUnit.SECONDS);
        
    }

}
