package com.cannontech.clientutils;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class YukonLoggingReloader {
    static final Logger log = YukonLogManager.getLogger(YukonLoggingReloader.class);
    public static final long DEFAULT_MAX_FILE_SIZE = 1073741824L; // 1 GB
    private final static long gigaBytesToByteMultiplier = 1024 * 1024 * 1024;
   
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    
    @PostConstruct
    public void initialize() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(event -> {
            Integer primaryKeyId = Integer.valueOf(event.getPrimaryKey());
            if ((event.getChangeCategory() == DbChangeCategory.GLOBAL_SETTING)
                && (primaryKeyId.equals(globalSettingDao.getSetting(GlobalSettingType.MAX_LOG_FILE_SIZE).getId()))) {
                reloadAppender(true);
            }
        });
        reloadAppender(false);
    }

    public void reloadAppender(boolean isDBChanged) {
        long maxFileSize = getMaxFileSize() * gigaBytesToByteMultiplier;
        if (maxFileSize > 0 && (isDBChanged || maxFileSize > DEFAULT_MAX_FILE_SIZE)) {
            Configuration config = LoggerContext.getContext(false).getConfiguration();
            config.getAppenders().entrySet()
                                 .stream()
                                 .filter(e -> !"console".equals(e.getKey()))
                                 .forEach(e -> setMaxFileSize(config, e.getValue(), maxFileSize));
        }
    }

    /**
     * Set max file size to the passed appender and add it to logging configuration.
     */
    private void setMaxFileSize(Configuration config, Appender appender, long maxFileSize) {
        ((YukonRollingFileAppender) appender).setMaxFileSize(maxFileSize);
        log.info(appender.getName() + " appender updated with max file size : " + maxFileSize/gigaBytesToByteMultiplier + "GB");
        appender.start();
        config.addAppender(appender);
    }

    private long getMaxFileSize() {
        //get maxFileSize in GB
        String maxFileSize = globalSettingDao.getString(GlobalSettingType.MAX_LOG_FILE_SIZE);
        try {
            return Long.parseLong(maxFileSize);
        } catch (Exception e) {
            return (long) GlobalSettingType.MAX_LOG_FILE_SIZE.getDefaultValue();
        }
    }
}
