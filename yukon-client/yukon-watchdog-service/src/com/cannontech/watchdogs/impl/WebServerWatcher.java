package com.cannontech.watchdogs.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;

@Service
public class WebServerWatcher extends ServiceStatusWatchdogImpl {
    private static final Logger log = YukonLogManager.getLogger(WebServerWatcher.class);

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;

    @Override
    public void start() {
        executor.scheduleAtFixedRate(() -> {
            watchAndNotify();
        }, 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus connectionStatus = getWebServerStatus();
        return generateWarning(WatchdogWarningType.WEB_SERVER_CONNECTION_STATUS, connectionStatus);
    }

    private ServiceStatus getWebServerStatus() {
        URL url;
        try {
            url = new URL("http://localhost:8080/yukon");
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.connect();
            httpConn.setConnectTimeout(10);
            log.debug("Yukon web server is : " + httpConn.getResponseCode());
            if (httpConn.getResponseCode() == 200) {
                return ServiceStatus.RUNNING;
            } else {
                return ServiceStatus.STOPPED;
            }
        } catch (IOException e) {
            log.debug("Yukon web server is down ");
            return ServiceStatus.STOPPED;
        }
    }
}
