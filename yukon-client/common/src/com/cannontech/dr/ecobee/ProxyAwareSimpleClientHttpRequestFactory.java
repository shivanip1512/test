package com.cannontech.dr.ecobee;

import java.net.InetSocketAddress;
import java.net.Proxy;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

/**
 * A SimpleClientHttpRequestFactory that sets its proxy based on the "System HTTP proxy" global setting. The proxy is 
 * only updated when the bean is initialized, so a restart is required to pick up changes.
 */
public class ProxyAwareSimpleClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
    @Autowired GlobalSettingDao globalSettingDao;
    private static final Logger log = YukonLogManager.getLogger(ProxyAwareSimpleClientHttpRequestFactory.class);
    
    @PostConstruct
    public void init() {
        String httpProxy = globalSettingDao.getString(GlobalSettingType.HTTP_PROXY);

        if (!httpProxy.equals("none")) {
            String [] hostAndPort = httpProxy.split(":");
            if(hostAndPort.length != 2) {
                log.error("GlobalSettingType = HTTP_PROXY has an invalid value: " + httpProxy
                         + ". Unable to setup proxy settings for ProxyAwareSimpleClientHttpRequestFactory.");
            } else {
                String host = httpProxy.split(":")[0];
                try {
                    int port = Integer.parseInt(httpProxy.split(":")[1]);
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
                    this.setProxy(proxy);
                } catch(NumberFormatException e) {
                    log.error("GlobalSettingType = HTTP_PROXY has an invalid value: "+ hostAndPort
                            + ". Unable to setup proxy settings for ProxyAwareSimpleClientHttpRequestFactory", e);
                }
            }
        }
    }
}
