package com.cannontech.web;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

/**
 * This class allow setting up SSL connection, each context should initialize this once.
 * Currently it is used by Watchdog (WebServerWatcher) and API connection to DR.
 */
public class SSLSettingsInitializer {
    private static final Logger log = YukonLogManager.getLogger(SSLSettingsInitializer.class);
    
    public static volatile boolean isHttpsSettingInitialized = false; 

    /**
     * Initialize setting for SSL connection
     */
    public static void initializeHttpsSetting() {
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
            log.debug("Could not initialize HTTPS settings " + e);
        }
    }

}
