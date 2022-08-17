package com.cannontech.watchdogs.impl;

import static com.cannontech.web.SSLSettingsInitializer.isHttpsSettingInitialized;

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
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.util.WebserverUrlResolver;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.web.SSLSettingsInitializer;

@Service
public class WebServerWatcher extends ServiceStatusWatchdogImpl {
    public WebServerWatcher(ConfigurationSource configSource) {
        super(configSource);
    }

    private static final Logger log = YukonLogManager.getLogger(WebServerWatcher.class);

    @Autowired private WebserverUrlResolver webserverUrlResolver;
    @Autowired private ConfigurationSource configurationSource;
    
    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus connectionStatus = getWebServerStatus();
        log.info("Status of web server " + connectionStatus);
        return generateWarning(WatchdogWarningType.YUKON_WEB_APPLICATION_SERVICE, connectionStatus);
    }

    private ServiceStatus getWebServerStatus() {
        if (shouldCheckWithInternalUrl()) {
            ServiceStatus status = checkWithInternalUrl();
            if (shouldCheckWithExternalUrl(status)) {
                return checkWithExternalUrl();
            } else {
                return status;
            }
        } else {
            return checkWithExternalUrl();
        }
    }

    private boolean shouldCheckWithInternalUrl() {
        String webServerUrl = webserverUrlResolver.getYukonInternalUrl();
        return StringUtils.isNotBlank(webServerUrl);
    }

    private boolean shouldCheckWithExternalUrl(ServiceStatus status) {
        return status == ServiceStatus.STOPPED;
    }

    private ServiceStatus checkWithExternalUrl() {
        String webServerUrl = webserverUrlResolver.getUrlBase();
        log.debug("Checking status with external url " + webServerUrl);
        return checkStatus(webServerUrl);
    }

    private ServiceStatus checkWithInternalUrl() {
        String webServerUrl = webserverUrlResolver.getYukonInternalUrl();
        log.debug("Checking status with internal url " + webServerUrl);
        return checkStatus(webServerUrl);
    }

    private ServiceStatus checkStatus(String url) {
        boolean proxySetting = true;
        try {
            // Check url with and without proxy
            int responseCode = getWebServerResponse(url, proxySetting);
            log.debug("Response code with proxy as " + proxySetting + " is " + responseCode);
            if (responseCode != 200) {
                proxySetting = false;
                responseCode = getWebServerResponse(url, proxySetting);
                log.debug("Response code with proxy as " + proxySetting + " is " + responseCode);
                if (responseCode != 200) {
                    return ServiceStatus.STOPPED;
                }
            }
            return ServiceStatus.RUNNING;
        } catch (SocketTimeoutException e) {
            log.debug("Yukon web server may be starting. Checked with url: " + url + " with proxy as " + proxySetting
                + " Error " + e);
            return ServiceStatus.UNKNOWN;
        } catch (IOException e) {
            log.debug("Yukon web server is down. Checked with url: " + url + " with proxy as " + proxySetting
                + " Error " + e);
            return ServiceStatus.STOPPED;
        }
    }

    //Returns the response of Web Server.
    private int getWebServerResponse(String webServerUrl, boolean useProxy) throws SocketTimeoutException, IOException{
        boolean isHttps = StringUtils.containsIgnoreCase(webServerUrl, "https");
        URL url = new URL(webServerUrl);

        if (url.getPort() == -1) {
            int port = isHttps ? 443 : 80;
            url = new URL(url.getProtocol(), url.getHost(), port, url.getFile());
            log.debug("Web server url " + url);
        }

        HttpURLConnection conn;
        
        if (isHttps && !isHttpsSettingInitialized) {
            SSLSettingsInitializer.initializeHttpsSetting();
        }

        if (useProxy) {
            conn = (HttpURLConnection) url.openConnection();
        } else {
            conn = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
        }

        conn.setReadTimeout(5000); // 5 sec for timeout
        conn.connect();
        return conn.getResponseCode();
    }
    

    @Override
    public YukonServices getServiceName() {
        return YukonServices.WEBSERVER;
    }

    @Override
    public boolean shouldRun() {
        return configurationSource.getBoolean(MasterConfigBoolean.WATCHDOG_WEB_SERVER_HTTP_CHECK_ENABLED, true);
    }
}
