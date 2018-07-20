package com.cannontech.watchdogs.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

@Service
public class WebServerWatcher extends ServiceStatusWatchdogImpl {
    private static final Logger log = YukonLogManager.getLogger(WebServerWatcher.class);

    @Autowired private WebserverUrlResolver webserverUrlResolver;
    @Autowired private ConfigurationSource configurationSource;
    private volatile boolean isHttpsSettingInitialized = false;
    
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
        String webServerUrl = webserverUrlResolver.getYukonInternalUrl();
        if (StringUtils.isBlank(webServerUrl)) {
            webServerUrl = webserverUrlResolver.getUrlBase();
        }
        try {
            int webServerReponse = getWebServerResponse(webServerUrl, useProxy);
            if (webServerReponse != 200) {
                log.debug("Response from Web server is not running. Trying once again with External URL");
                webServerUrl = webserverUrlResolver.getUrlBase();
                webServerReponse = getWebServerResponse(webServerUrl, useProxy);
            }
            log.debug("Response code from Web server " + webServerReponse);
            return webServerReponse;
        } catch (IOException e) {
            log.debug("Response from Web server is not running. Trying once again with External URL");
            webServerUrl = webserverUrlResolver.getUrlBase();
            int webServerReponse = getWebServerResponse(webServerUrl, useProxy);
            log.debug("Response code from Web Server " + webServerReponse);
            return webServerReponse;
        }
    }

    //Returns the response of Web Server.
    private int getWebServerResponse(String webServerUrl, boolean useProxy) throws SocketTimeoutException, IOException{
        boolean isHttps = StringUtils.containsIgnoreCase(webServerUrl, "https");
        URL url = new URL(webServerUrl);
        log.debug("Web server url " + webServerUrl);
        HttpURLConnection conn;
        
        if (isHttps && !isHttpsSettingInitialized) {
            initializeHttpsSetting();
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
    
    // Initialize settings for a https connection.
    private void initializeHttpsSetting() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> true);

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            isHttpsSettingInitialized = true;
        } catch (Exception e) {
            log.debug("Could not initialze HTTPS settings " + e);
        }
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
