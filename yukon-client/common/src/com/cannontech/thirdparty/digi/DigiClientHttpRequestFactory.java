package com.cannontech.thirdparty.digi;

import java.io.IOException;
import java.net.URI;

import javax.annotation.PostConstruct;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;

import com.cannontech.common.config.ConfigurationSource;

public class DigiClientHttpRequestFactory implements ClientHttpRequestFactory {

    private String user;
    private String password;
    private ConfigurationSource configSource;
    private HttpClient httpClient;
    private HttpClientContext context;
    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);

    public DigiClientHttpRequestFactory() {
        // Create a client builder.
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();

        // Set user credentails
        if (user != null) {
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
            clientBuilder.setDefaultCredentialsProvider(credsProvider);
        }
        // Set Socket time out
        RequestConfig requestConfig =
            RequestConfig.custom().setSocketTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS).build();

        clientBuilder.setDefaultRequestConfig(requestConfig);
        httpClient = clientBuilder.build();
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    @PostConstruct
    public void initialize() {
        user = configSource.getString("DIGI_USERNAME", "default");
        password = configSource.getString("DIGI_PASSWORD", "default");
    }

    @Autowired
    public void setConfigSource(ConfigurationSource configSource) {
        this.configSource = configSource;
    }

    @Override
    public ClientHttpRequest createRequest(URI arg0, HttpMethod arg1) throws IOException {
        return null;
    }

    public HttpClientContext getContext(String hostname, int port, String protocol){
        HttpHost targetHost = new HttpHost(hostname, port, protocol);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(  new AuthScope(hostname,port), 
            new UsernamePasswordCredentials(user,password));
        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);
        // Add AuthCache to the execution context
        context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);
        return context;
    }

}
