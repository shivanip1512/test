package com.cannontech.clientutils;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class YukonLoggingReloader extends YukonLoggingReloaderHelper{

    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    
    @PostConstruct
    public void initialize() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(event -> {
            Integer primaryKeyId = Integer.valueOf(event.getPrimaryKey());
            if ((event.getChangeCategory() == DbChangeCategory.GLOBAL_SETTING)) {
                if (primaryKeyId.equals(globalSettingDao.getSetting(GlobalSettingType.MAX_LOG_FILE_SIZE).getId())) {
                    reloadAppenderForMaxFileSize(true);
                } else if (primaryKeyId.equals(globalSettingDao.getSetting(GlobalSettingType.LOG_RETENTION_DAYS).getId())) {
                    reloadAppenderForLogRetentionDays();
                }
            }
        });
        reloadAppenderForMaxFileSize(false);
        reloadAppenderForLogRetentionDays();
    }
}
