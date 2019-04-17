package com.cannontech.common.config;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.dao.GlobalSettingUpdateDao;

public class MasterConfigDeprecatedKeyMigrationHelper {
    private static final Logger log = YukonLogManager.getLogger(MasterConfigDeprecatedKeyMigrationHelper.class);
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private GlobalSettingUpdateDao globalSettingUpdateDao;
    
    @PostConstruct
    public void updateDeprecatedConfigKeys() {
        MasterConfigMap configMap = MasterConfigHelper.getLocalConfiguration();
        if (configMap == null) {
            log.error("No local config available"); 
            return;
        }
        try {
            configMap.updateDeprecatedKeys(globalSettingDao, globalSettingUpdateDao);
        } catch (IOException e) {
            log.error("Error while attempting to update deprecated keys", e);
        }
    }
}
