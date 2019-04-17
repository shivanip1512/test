package com.cannontech.clientutils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public abstract class YukonLoggingReloaderHelper {

    static final Logger log = YukonLogManager.getLogger(YukonLoggingReloader.class);

    @Autowired private GlobalSettingDao globalSettingDao;

    public static final long DEFAULT_MAX_FILE_SIZE = 1073741824L; // 1 GB
    private final static long gigaBytesToByteMultiplier = 1024 * 1024 * 1024;

    public void reloadAppenderForMaxFileSize(boolean isDBChanged) {
        long maxFileSize = globalSettingDao.getInteger(GlobalSettingType.MAX_LOG_FILE_SIZE) * gigaBytesToByteMultiplier;
        if (maxFileSize > 0 && (isDBChanged || maxFileSize > DEFAULT_MAX_FILE_SIZE)) {
            reloadAppender(maxFileSize, GlobalSettingType.MAX_LOG_FILE_SIZE);
            BootstrapUtils.setLogMaxFileSize(maxFileSize);
        }
    }

    public void reloadAppenderForLogRetentionDays() {
        int logRetentionDays =  globalSettingDao.getInteger(GlobalSettingType.LOG_RETENTION_DAYS);
        if (logRetentionDays > 0) {
            reloadAppender(logRetentionDays, GlobalSettingType.LOG_RETENTION_DAYS);
            BootstrapUtils.setLogRetentionDays(logRetentionDays);
        }
    }

    private void reloadAppender(long value, GlobalSettingType type) {
        Configuration config = LoggerContext.getContext(false).getConfiguration();
        config.getAppenders().entrySet().stream().filter(e -> !"console".equals(e.getKey())).forEach(e -> {
            YukonRollingFileAppender appender = (YukonRollingFileAppender) e.getValue();
            if (type == GlobalSettingType.MAX_LOG_FILE_SIZE) {
                appender.setMaxFileSize(value);
                log.info(appender.getName() + " updated with max file size : " + value / gigaBytesToByteMultiplier + "GB");
            } else if (type == GlobalSettingType.LOG_RETENTION_DAYS) {
                appender.setLogRetentionDays((int) value);
                log.info(appender.getName() + " updated with log retention : " + value + " days");
            }
            appender.start();
            config.addAppender(appender);
        });
    }
}
