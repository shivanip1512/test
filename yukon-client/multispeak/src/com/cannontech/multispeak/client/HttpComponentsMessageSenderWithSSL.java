package com.cannontech.multispeak.client;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.Logger;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.cannontech.clientutils.YukonLogManager;

/**
 * Singleton class to create Http message sender with SSL settings.
 */
public class HttpComponentsMessageSenderWithSSL extends HttpComponentsMessageSender {
    private final static Logger log = YukonLogManager.getLogger(HttpComponentsMessageSenderWithSSL.class);
    private static HttpComponentsMessageSender httpMessageSender = null;
    private static final int DEFAULT_TIMEOUT_MILLISECONDS = 120000;
    
    private HttpComponentsMessageSenderWithSSL() {
        setHttpClient(httpClient());
    }
    
    public static HttpComponentsMessageSender getInstance() {
        if (httpMessageSender == null) {
            httpMessageSender = new HttpComponentsMessageSenderWithSSL();
        }
        return httpMessageSender;
    }
    
    private HttpClient httpClient() {
        try {
            
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(DEFAULT_TIMEOUT_MILLISECONDS)
                    .setConnectionRequestTimeout(DEFAULT_TIMEOUT_MILLISECONDS)
                    .setSocketTimeout(DEFAULT_TIMEOUT_MILLISECONDS)
                    .build();
            
            HttpClient httpClient =
                HttpClientBuilder.create().setSSLSocketFactory(sslConnectionSocketFactory())
                .setDefaultRequestConfig(config)
                .addInterceptorFirst(new RemoveSoapHeadersInterceptor()).build();

     
            return httpClient;
        } catch (Exception e) {
            log.info("Could not set SSL settings on HTTP client" + e);
        }
        return (HttpClient) HttpClientBuilder.create();
    }
    
    private SSLConnectionSocketFactory sslConnectionSocketFactory() throws Exception {
        return new SSLConnectionSocketFactory(sslContext(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    }    
    
    private SSLContext sslContext() throws Exception {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, getAllTrustCertificate(), new java.security.SecureRandom());
        return sc;
    }
    
    
    private TrustManager[] getAllTrustCertificate() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };
        return trustAllCerts;
    }
}
