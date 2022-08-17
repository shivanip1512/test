package com.cannontech.common.config;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;

public class MasterConfigServerFactory implements FactoryBean<ConfigurationSource> {
    
    private String remoteFileName = "remoteMaster.cfg";
    private ConfigurationSource localConfigurationSource;
    private Logger log = YukonLogManager.getLogger(MasterConfigServerFactory.class);

    @Override
    public ConfigurationSource getObject() throws Exception {
        try {
            File remoteFile = new File(CtiUtilities.getYukonBase(), "Server/Config/" + remoteFileName);
            boolean isRemoteVersionAvailable = remoteFile.canRead();
            if (isRemoteVersionAvailable) {
                MasterConfigMap config = new MasterConfigMap(remoteFile);
                log.info("Using remote configuration: " + remoteFileName);
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
    
    @Autowired
    public void setLocalConfigurationSource(ConfigurationSource localConfigurationSource) {
        this.localConfigurationSource = localConfigurationSource;
    }

}