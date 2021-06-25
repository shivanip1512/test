package com.cannontech.clientutils;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.broker.LoggingDbChangePublisher;
import com.cannontech.broker.message.request.EventType;
import com.cannontech.broker.message.request.LoggingDbChangeRequest;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class YukonLoggingReloader extends YukonLoggingReloaderHelper{

    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private LoggingDbChangePublisher dbChangePublisher;
    
    @PostConstruct
    public void initialize() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(event -> {
            LoggingDbChangeRequest request = null;
            if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.MAX_LOG_FILE_SIZE)) {
                reloadAppenderForMaxFileSize(true);
                request = new LoggingDbChangeRequest(EventType.MAX_LOG_FILE_SIZE);
            } else if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.LOG_RETENTION_DAYS)) {
                reloadAppenderForLogRetentionDays();
                request = new LoggingDbChangeRequest(EventType.LOG_RETENTION_DAYS);
            }
            dbChangePublisher.publish(request);
        });
        reloadAppenderForMaxFileSize(false);
        reloadAppenderForLogRetentionDays();
    }
}
