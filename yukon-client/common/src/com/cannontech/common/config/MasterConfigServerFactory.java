package com.cannontech.common.config;

import java.io.File;
import java.io.InputStream;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;

public class MasterConfigServerFactory implements FactoryBean {
    private String remoteFileName = "remoteMaster.cfg";
    private ConfigurationSource localConfigurationSource;
    private Logger log = YukonLogManager.getLogger(MasterConfigServerFactory.class);

    @Override
    public Object getObject() throws Exception {
        try {
            File remoteFile = new File(CtiUtilities.getYukonBase(), "Server/Config/" + remoteFileName);
            boolean isRemoteVersionAvailable = remoteFile.canRead();
            if (isRemoteVersionAvailable) {
                InputStream stream = remoteFile.toURI().toURL().openStream();
                MasterConfigMap config = new MasterConfigMap();
                config.setConfigSource(stream);
                config.initialize();
                log.info("Using remote configuation: " + remoteFileName);
                return config;
            }
        } catch (Exception e) {
            log.warn("Unable to read " + remoteFileName, e);
        }
        return localConfigurationSource;
    }

    @Override
    public Class<ConfigurationSource> getObjectType() {
        return ConfigurationSource.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
    
    @Resource(name="localMasterConfig")
    public void setLocalConfigurationSource(
            ConfigurationSource localConfigurationSource) {
        this.localConfigurationSource = localConfigurationSource;
    }


}
