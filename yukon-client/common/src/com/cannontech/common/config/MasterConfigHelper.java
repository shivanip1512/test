package com.cannontech.common.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.spring.SimpleSessionHttpInvokerRequestExecutor;

public class MasterConfigHelper {
    static private File masterCfgLocation;
    private static String urlBase = null;
    //private static String sessionId;
    private static SimpleHttpInvokerRequestExecutor requestExecutor = new SimpleHttpInvokerRequestExecutor();
    
    static {
        masterCfgLocation = new File(CtiUtilities.getYukonBase(), "Server/Config/master.cfg");
    }
    
    static public boolean isLocalConfigAvailable() {
        URL url = MasterConfigHelper.class.getClassLoader().getResource("master.cfg");
        return (url != null) || masterCfgLocation.canRead();
    }
    
    static public InputStream getMasterConfig() {
        try {
            return masterCfgLocation.toURL().openStream();
        } catch (Exception e) {
            throw new BadConfigurationException("Unable to open master.cfg from " + masterCfgLocation, e);
        }
    }
    
    static public ConfigurationSource getLocalConfiguration() {
        MasterConfigMap config = new MasterConfigMap();
        // check if on classpath
        URL url = MasterConfigHelper.class.getClassLoader().getResource("master.cfg");
        if (url != null) {
            try {
                config.setConfigSource(url.openStream());
                CTILogger.info("Local config found on classpath: " + url);
            } catch (IOException e) {
                CTILogger.error("master.cfg appears on the classpath but is unreadable, configuring with full path ", e);
                config.setConfigSource(getMasterConfig());
                CTILogger.info("Local config found on filesystem: " + masterCfgLocation);
            }
        } else {
            config.setConfigSource(getMasterConfig());
            CTILogger.info("Local config found on filesystem: " + masterCfgLocation);
        }
        
        config.initialize();
        return config;
    }
    
    static public ConfigurationSource getRemoteConfiguration() {
        if (urlBase == null) {
            CTILogger.warn("urlBase=" + urlBase);
            
            throw new BadConfigurationException("remoteBaseUrl or sessionId not set");
        }
        HttpInvokerClientInterceptor interceptor = new HttpInvokerClientInterceptor();
        interceptor.setHttpInvokerRequestExecutor(requestExecutor);
        String url = urlBase + "/remote/MasterConfig";
        interceptor.setServiceUrl(url);
        CTILogger.debug("remote configuration interceptor=" + interceptor);
        ConfigurationSource config = (ConfigurationSource) ProxyFactory.getProxy(ConfigurationSource.class, interceptor);
        CTILogger.info("Remote config setup: " + url);
        return config;
    }
    
    static public ConfigurationSource getConfiguration() {
        if (urlBase != null) {
            CTILogger.debug("Returning remote config");
            return getRemoteConfiguration();
        } else {
            CTILogger.debug("Returning local config");
            return getLocalConfiguration();
        }
    }
    
    static public void setRemoteBaseUrl(String urlBase) {
        MasterConfigHelper.urlBase = urlBase;
    }
    
    static public void setRemoteHostAndPort(String host, int port) {
        setRemoteBaseUrl("http://" + host + ":" + port);
    }
    
    static public void setSessionId(String sessionId) {
        //MasterConfigHelper.sessionId = sessionId;
        if (sessionId == null) {
            requestExecutor = new SimpleHttpInvokerRequestExecutor();
        } else {
            SimpleSessionHttpInvokerRequestExecutor executor = new SimpleSessionHttpInvokerRequestExecutor();
            executor.setSessionId(sessionId);
            requestExecutor = executor;
        }
    }
    
}
