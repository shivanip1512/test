package com.cannontech.common.config;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.dao.GlobalSettingUpdateDao;

public class MasterConfigDeprecatedKeyMigrationHelper {
    static private final Logger log = YukonLogManager.getLogger(MasterConfigDeprecatedKeyMigrationHelper.class);
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private GlobalSettingUpdateDao globalSettingUpdateDao;
    
    @PostConstruct
    public void updateDeprecatedConfigKeys() {
        if (!(configurationSource instanceof MasterConfigMap)) {
            log.error(configurationSource + " is not an instance of MasterConfigMap"); 
            return;
        }
        var mcm = (MasterConfigMap) configurationSource;
                
        try {
            mcm.updateDeprecatedKeys(globalSettingDao, globalSettingUpdateDao);
        } catch (IOException e) {
            log.error("Error while attempting to update deprecated keys", e);
        }
    }
}
