package com.cannontech.watchdogs.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.WebserverUrlResolver;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;

@Service
public class WebServerWatcher extends ServiceStatusWatchdogImpl {
    private static final Logger log = YukonLogManager.getLogger(WebServerWatcher.class);

    @Autowired private WebserverUrlResolver webserverUrlResolver;

    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus connectionStatus = getWebServerStatus();
        log.debug("Status of web server " + connectionStatus);
        return generateWarning(WatchdogWarningType.WEB_SERVER_SERVICE_STATUS, connectionStatus);
    }

    private ServiceStatus getWebServerStatus() {
        try {
            String webServerUrl = webserverUrlResolver.getUrlBase();
            URL url = new URL(webServerUrl);
            log.debug("Web server url " + webServerUrl);

            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setReadTimeout(5000); // 5 sec for timeout
            httpConn.connect();
            log.debug("Response code from web server " + httpConn.getResponseCode());

            if (httpConn.getResponseCode() == 200) {
                return ServiceStatus.RUNNING;
            } else {
                return ServiceStatus.STOPPED;
            }
        } catch (SocketTimeoutException e) {
            log.debug("Yukon web server may be starting ");
            return ServiceStatus.UNKNOWN;
        } catch (IOException e) {
            log.debug("Yukon web server is down ");
            return ServiceStatus.STOPPED;
        }
    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.WEBSERVER;
    }
}
