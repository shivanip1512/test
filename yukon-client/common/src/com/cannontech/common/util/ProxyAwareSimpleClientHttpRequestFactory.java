package com.cannontech.common.util;

import java.net.Proxy;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import com.cannontech.system.dao.GlobalSettingDao;

/**
 * A SimpleClientHttpRequestFactory that sets its proxy based on the "System HTTP proxy" global setting. The proxy is 
 * only updated when the bean is initialized, so a restart is required to pick up changes.
 */
public class ProxyAwareSimpleClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
    @Autowired private GlobalSettingDao globalSettingDao;
    
    @PostConstruct
    public void init() {
        Optional<YukonHttpProxy> oProxy = YukonHttpProxy.fromGlobalSetting(globalSettingDao);

        if (oProxy.isPresent()) {
            Proxy proxy = oProxy.get().getJavaHttpProxy();
            setProxy(proxy);
        }

    }
}
