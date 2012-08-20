package com.cannontech.common.config;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.encryption.CryptoException;

public class MasterConfigHelper {
    private static File masterCfgLocation;
    private static String url = null;
    private static final Logger log = YukonLogManager.getLogger(MasterConfigHelper.class);
    private static MasterConfigMap localConfig = null;

    static {
        URL url =  MasterConfigHelper.class.getClassLoader().getResource("master.cfg");
        if (url != null) {
            try {
                masterCfgLocation = new File(url.toURI());
            } catch(URISyntaxException e) {
                masterCfgLocation = new File(url.getPath());
            }
            log.info("Local config found on classpath: " + url);
        } else {
            masterCfgLocation = new File(CtiUtilities.getYukonBase(), "Server/Config/master.cfg");
        }
    }

    static public boolean isLocalConfigAvailable() {
        URL url = MasterConfigHelper.class.getClassLoader().getResource("master.cfg");
        return (url != null) || masterCfgLocation.canRead();
    }

    static public File getMasterConfig() {
        return masterCfgLocation;
    }

    static public synchronized ConfigurationSource getLocalConfiguration(){
        if (localConfig == null) {
            localConfig = new MasterConfigMap();
            // check if on classpath
            URL url = MasterConfigHelper.class.getClassLoader().getResource("master.cfg");
            if (url != null) {
                try {
                    localConfig.setConfigSource(new File(url.toURI()));
                    log.info("Local config found on classpath: " + url);
                }  catch (URISyntaxException e) {
                    log.error("master.cfg appears on the classpath but is unreadable, configuring with full path ", e);
                    localConfig.setConfigSource(getMasterConfig());
                    log.info("Local config found on filesystem: " + masterCfgLocation);
                } 
            } else {
                localConfig.setConfigSource(getMasterConfig());
                log.info("Local config found on filesystem: " + masterCfgLocation);
            }

            try {
                localConfig.initialize();
            } catch (CryptoException cae) {
                log.error("Severe Error while initializing configuration. The MasterConfigPasskey.dat file might be invalid. May need to reset encrypted values in master.cfg to plaintext",cae);
            } catch (IOException e) {
                log.error("Severe Error while initializing configuration.",e);
            }
        }
        return localConfig;
    }

    public static ConfigurationSource getRemoteConfiguration() {
        if (url == null) {
            throw new BadConfigurationException("can't get remote configuration when url isn't set");
        }
        HttpInvokerClientInterceptor interceptor = new HttpInvokerClientInterceptor();
        interceptor.setServiceUrl(url);
        interceptor.afterPropertiesSet();
        log.debug("remote configuration interceptor=" + interceptor);
        ConfigurationSource config = ProxyFactory.getProxy(ConfigurationSource.class, interceptor);
        return config;
    }

    public static ConfigurationSource getConfiguration() {
        if (url != null) {
            log.debug("Returning remote config");
            return getRemoteConfiguration();
        } else {
            log.debug("Returning local config");
            return getLocalConfiguration();
        }
    }

    public static void setRemoteHostAndPort(String host, String userName, String password) {
        try {
            url = host + "/remote/MasterConfig?" + "USERNAME=" + URLEncoder.encode(userName, "UTF-8") + "&PASSWORD=" + URLEncoder.encode(password, "UTF-8") + "&noLoginRedirect=true";
            log.debug("setting url to: " + url);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to set host and port", e);
        }
    }
}
