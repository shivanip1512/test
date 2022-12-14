package com.cannontech.dr.honeywell.service.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.dr.honeywell.HoneywellCommunicationException;
import com.cannontech.dr.honeywell.message.TokenResponse;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class HoneywellRestProxyFactory {
    private static final Logger log = YukonLogManager.getLogger(HoneywellRestProxyFactory.class);

    private static final String authUrlPart = "Auth/Oauth/token";
    private static final int tokenExpirationBuffer = 60;

    @Autowired private GlobalSettingDao settingDao;

    private final RestTemplate proxiedTemplate;

    private final String authTokenKey = "authTokenKey";
    private String authTokenValue = null;
    private Cache<String,String> tokenCache;

    public HoneywellRestProxyFactory(RestTemplate proxiedTemplate) {
        this.proxiedTemplate = proxiedTemplate;
    }

    public RestOperations createInstance() {
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                try {
                    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                    YukonHttpProxy.fromGlobalSetting(settingDao).ifPresent(httpProxy -> {
                        factory.setProxy(httpProxy.getJavaHttpProxy());
                    });
                    proxiedTemplate.setRequestFactory(factory);
                    addAuthorizationToken(args);
                    Object responseObj = method.invoke(proxiedTemplate, args);

                    return responseObj;
                } catch (RestClientException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                    throw new HoneywellCommunicationException("Error communicating with Honeywell API", e);
                }
            }
        };

        Object obj =
            Proxy.newProxyInstance(RestOperations.class.getClassLoader(), new Class[] { RestOperations.class },
                invocationHandler);

        return RestOperations.class.cast(obj);
    }

    /**
     * Searches arguments for an HttpHeader. If one is found an honeywell authorization header will be added.
     */
    private void addAuthorizationToken(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof HttpEntity) {
                Object httpBody = ((HttpEntity<?>) arg).getBody();
                HttpHeaders httpheaders = ((HttpEntity<?>) arg).getHeaders();

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + getAuthenticationToken());
                headers.add("Content-Hash", httpheaders.get("Content-Hash").get(0));
                headers.add("Date", httpheaders.get("Date").get(0));
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                headers.setContentType(MediaType.APPLICATION_JSON);
                if (httpheaders.getFirst("UserId") != null) {
                    headers.add("UserId", httpheaders.getFirst("UserId"));
                }
               args[i] = new HttpEntity<>(httpBody, headers);
            }
        }
    }

    private synchronized String getAuthenticationToken() {
        if (tokenCache == null || tokenCache.getIfPresent(authTokenKey) == null) {
            authTokenValue = generateAuthenticationToken();
        } else {
            log.debug("Token Retrieved from Cache: {}", tokenCache.getIfPresent(authTokenKey));
        }
        return authTokenValue;
    }
    
    private String generateAuthenticationToken() {
        log.debug("Generating new Authentication Token");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        // TODO: Code cleanup required here will be done when we can get fully connected
        body.add("grant_type", "client_credentials");
        String urlBase = settingDao.getString(GlobalSettingType.HONEYWELL_SERVER_URL);
        String plainClientId = settingDao.getString(GlobalSettingType.HONEYWELL_CLIENTID);
        String plainSecret = settingDao.getString(GlobalSettingType.HONEYWELL_SECRET);

        byte[] bytes = (plainClientId + ":" + plainSecret).getBytes();
        byte[] base64Bytes = Base64.encodeBase64(bytes);
        String base64 = new String(base64Bytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64);
        HttpEntity<?> requestEntity = new HttpEntity<Object>(body, headers);

        String url = urlBase + authUrlPart;
        log.debug("Attempting login with userName " + plainClientId + " URL: " + url);
        TokenResponse response;
        try {
            response = proxiedTemplate.postForObject(url, requestEntity, TokenResponse.class);
            log.debug("Honeywell message response {}", response);
        } catch (RestClientException e) {
            throw new HoneywellCommunicationException("Unable to communicate with Honeywell API while generating authentication token.", e);
        }

        authTokenValue = response.getAccessToken();
        Long expiresIn = response.getExpiresIn() - tokenExpirationBuffer;
        tokenCache = CacheBuilder.newBuilder().expireAfterWrite(expiresIn, TimeUnit.SECONDS).build();
        tokenCache.put(authTokenKey, authTokenValue);

        return authTokenValue;
    }
}
