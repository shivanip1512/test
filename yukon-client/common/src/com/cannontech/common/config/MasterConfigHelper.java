package com.cannontech.common.config;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.encryption.CryptoException;

public class MasterConfigHelper {
    private static File masterCfgLocation;
    private static final Logger log = YukonLogManager.getLogger(MasterConfigHelper.class);
    private static MasterConfigMap localConfig = null;
    
    private MasterConfigHelper() {
        //  hide implicit constructor
    }

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
            masterCfgLocation = new File(BootstrapUtils.getYukonBase(log.isDebugEnabled()), "Server/Config/master.cfg");
        }
    }

    static public boolean isLocalConfigAvailable() {
        URL url = MasterConfigHelper.class.getClassLoader().getResource("master.cfg");
        return (url != null) || masterCfgLocation.canRead();
    }

    static public File getMasterConfig() {
        return masterCfgLocation;
    }

    static public synchronized MasterConfigMap getLocalConfiguration(){
        if (localConfig == null) {
            File masterCfg;
            // check if on classpath
            URL url = MasterConfigHelper.class.getClassLoader().getResource("master.cfg");
            if (url != null) {
                try {
                    masterCfg = new File(url.toURI());
                    log.info("Local config found on classpath: " + url);
                }  catch (URISyntaxException e) {
                    log.error("master.cfg appears on the classpath but is unreadable, configuring with full path ", e);
                    masterCfg = getMasterConfig();
                    log.info("Local config found on filesystem: " + masterCfgLocation);
                } 
            } else {
                masterCfg = getMasterConfig();
                log.info("Local config found on filesystem: " + masterCfgLocation);
            }

            try {
                localConfig = new MasterConfigMap(masterCfg);
            } catch (CryptoException cae) {
                log.error("Severe Error while initializing configuration. The MasterConfigPasskey.dat file might be "
                    + "invalid or you may need to reset encrypted values in master.cfg to plaintext", cae);
            } catch (IOException e) {
                log.error("Severe Error while initializing configuration.",e);
            }
        }
        return localConfig;
    }

    public static ConfigurationSource getRemoteConfiguration() {
        ConfigurationSource config =
                ClientSession.getRemoteLoginSession().getReconnectingInteceptorProxy(ConfigurationSource.class,
                    "/remote/MasterConfig");
        log.debug("MasterConfigMap loaded");
        return config;
    }
   
    public static ConfigurationSource getConfiguration() {
        if (ClientSession.isRemoteSession()) {
            log.debug("Returning remote config");
            return getRemoteConfiguration();
        }

        log.debug("Returning local config");
        return getLocalConfiguration();
    }    
}
