package com.cannontech.clientutils;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.logger.dao.YukonLoggerDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class YukonLoggingReloader extends YukonLoggingReloaderHelper{

    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private YukonLoggerDao yukonLoggerDao;

    @PostConstruct
    public void initialize() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(event -> {
            if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.MAX_LOG_FILE_SIZE)) {
                reloadAppenderForMaxFileSize(true);
            } else if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.LOG_RETENTION_DAYS)) {
                reloadAppenderForLogRetentionDays();
            } else if(yukonLoggerDao.isDbChangeForLogger(event)) {
                reloadYukonLoggers();
            }
        });
        reloadAppenderForMaxFileSize(false);
        reloadAppenderForLogRetentionDays();
        reloadYukonLoggers();
    }
}
