package com.cannontech.web.common.captcha.service.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.system.dao.GlobalSettingDao;

public class CaptchaRestProxyFactory {

    @Autowired private GlobalSettingDao settingDao;

    private final RestTemplate proxiedTemplate;


    public CaptchaRestProxyFactory(RestTemplate proxiedTemplate) {
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
                    Object responseObj = method.invoke(proxiedTemplate, args);
                    return responseObj;
                } catch (RestClientException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                    throw new RestClientException("Error while setting the Http proxy setting for CAPTCHA", e);
                }
            }
        };

        Object obj =
            Proxy.newProxyInstance(RestOperations.class.getClassLoader(), new Class[] { RestOperations.class },
                invocationHandler);

        return RestOperations.class.cast(obj);
    }

}
