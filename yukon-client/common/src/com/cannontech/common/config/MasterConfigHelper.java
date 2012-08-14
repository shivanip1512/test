package com.cannontech.common.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.util.CtiUtilities;

public class MasterConfigHelper {
    static private File masterCfgLocation;
    private static String url = null;
    
    static {
        URL url =  MasterConfigHelper.class.getClassLoader().getResource("master.cfg");
        if (url != null) {
            try {
                masterCfgLocation = new File(url.toURI());
            } catch(URISyntaxException e) {
                masterCfgLocation = new File(url.getPath());
            }
            CTILogger.info("Local config found on classpath: " + url);
        } else {
            masterCfgLocation = new File(CtiUtilities.getYukonBase(), "Server/Config/master.cfg");
        }
    }
    
    static public boolean isLocalConfigAvailable() {
        URL url = MasterConfigHelper.class.getClassLoader().getResource("master.cfg");
        return (url != null) || masterCfgLocation.canRead();
    }
    
    static public InputStream getMasterConfig() {
        try {
            return masterCfgLocation.toURI().toURL().openStream();
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
        
        try {
            config.initialize();
        } catch (IOException e) {
            //log.warn("caught exception in getLocalConfiguration", e);
        }
        return config;
    }
    
    static public ConfigurationSource getRemoteConfiguration() {
        if (url == null) {
            throw new BadConfigurationException("can't get remote configuration when url isn't set");
        }
        HttpInvokerClientInterceptor interceptor = new HttpInvokerClientInterceptor();
        interceptor.setServiceUrl(url);
        interceptor.afterPropertiesSet();
        CTILogger.debug("remote configuration interceptor=" + interceptor);
        ConfigurationSource config = (ConfigurationSource) ProxyFactory.getProxy(ConfigurationSource.class, interceptor);
        return config;
    }
    
    static public ConfigurationSource getConfiguration() {
        if (url != null) {
            CTILogger.debug("Returning remote config");
            return getRemoteConfiguration();
        } else {
            CTILogger.debug("Returning local config");
            return getLocalConfiguration();
        }
    }
    
    static public void setRemoteHostAndPort(String host, String userName, String password) {
        try {
            url = host + "/remote/MasterConfig?" + "USERNAME=" + URLEncoder.encode(userName, "UTF-8") + "&PASSWORD=" + URLEncoder.encode(password, "UTF-8") + "&noLoginRedirect=true";
            CTILogger.debug("setting url to: " + url);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to set host and port", e);
        }
    }
    
    static public File getMasterCfgLocation() {
        return masterCfgLocation;
    }
    
}
