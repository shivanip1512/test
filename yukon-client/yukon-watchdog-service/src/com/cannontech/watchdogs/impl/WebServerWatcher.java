package com.cannontech.watchdogs.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.WebserverUrlResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;

@Service
public class WebServerWatcher extends ServiceStatusWatchdogImpl {
    private static final Logger log = YukonLogManager.getLogger(WebServerWatcher.class);

    @Autowired private WebserverUrlResolver webserverUrlResolver;
    @Autowired GlobalSettingDao globalSettingDao;

    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus connectionStatus = getWebServerStatus();
        log.debug("Status of web server " + connectionStatus);
        return generateWarning(WatchdogWarningType.WEB_SERVER_SERVICE_STATUS, connectionStatus);
    }

    private ServiceStatus getWebServerStatus() {
        try {
            // Try to send a request with proxy, if it fails try without proxy.
            if (sendRequest(true) != 200) {
                if (sendRequest(false) != 200) {
                    return ServiceStatus.STOPPED;
                }
            }
            return ServiceStatus.RUNNING;

        } catch (SocketTimeoutException e) {
            log.debug("Yukon web server may be starting ");
            return ServiceStatus.UNKNOWN;
        } catch (IOException e) {
            log.debug("Yukon web server is down ");
            return ServiceStatus.STOPPED;
        }

    }
    
    private int sendRequest(boolean useProxy) throws SocketTimeoutException, IOException {
        String webServerUrl = globalSettingDao.getString(GlobalSettingType.YUKON_INTERNAL_URL);
        if (StringUtils.isBlank(webServerUrl)) {
            webServerUrl = webserverUrlResolver.getUrlBase();
        }
        URL url = new URL(webServerUrl);
        log.debug("Web server url " + webServerUrl);
        HttpURLConnection httpConn;
        if(useProxy) {
            httpConn = (HttpURLConnection) url.openConnection();
        } else {
            httpConn = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
        }
        httpConn.setReadTimeout(5000); // 5 sec for timeout
        httpConn.connect();
        log.debug("Response code from web server " + httpConn.getResponseCode());
        return httpConn.getResponseCode();
    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.WEBSERVER;
    }
}
