package com.cannontech.clientutils;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.logger.dao.YukonLoggerDao;
import com.cannontech.common.log.model.YukonLogger;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public abstract class YukonLoggingReloaderHelper {

    static final Logger log = YukonLogManager.getLogger(YukonLoggingReloader.class);

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private YukonLoggerDao yukonLoggerDao;

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

    public void reloadYukonLoggers(DbChangeType dbChangeType) {
        {
            // Get the current context and configuration
            LoggerContext ctx = YukonLogManager.getMyLogger().getContext();
            Configuration config = ctx.getConfiguration();

            // Retrieve the all the loggers from DB table.
            List<YukonLogger> loggers = yukonLoggerDao.getLoggers();

            // The new class level loggers should log the details in the file and console. So retrieve these 2 appenders from
            // configuration.
            Appender appender = config.getAppender("yukonRollingFileAppender");
            Appender consoleAppender = config.getAppender("console");

            // Create the AppenderRef[] which will be used to create LoggerConfig
            AppenderRef ref = AppenderRef.createAppenderRef(appender.getName(), null, config.getFilter());
            AppenderRef consoleRef = AppenderRef.createAppenderRef(consoleAppender.getName(), null, config.getFilter());
            AppenderRef[] refs = new AppenderRef[] { ref, consoleRef };

            // Get existing loggers from configuration.
            Map<String, LoggerConfig> existingLoggers = config.getLoggers();

            // Iterate over the loggers and add them to configuration.Compare with existing loggers before creating LoggerConfig
            for (YukonLogger logger : loggers) {
                LoggerConfig oldConfig = existingLoggers.get(logger.getLoggerName());
                if (oldConfig == null || oldConfig.getLevel() != YukonLogManager.getApacheLevel(logger.getLevel())) {
                    LoggerConfig loggerConfig = LoggerConfig.createLogger(false,
                            YukonLogManager.getApacheLevel(logger.getLevel()), logger.getLoggerName(),
                            "true", refs, null, config, null);
                    loggerConfig.addAppender(appender, null, config.getFilter());
                    loggerConfig.addAppender(consoleAppender, null, config.getFilter());
                    config.addLogger(logger.getLoggerName(), loggerConfig);
                }
                if (dbChangeType == DbChangeType.DELETE) {
                    config.removeLogger(logger.getLoggerName());
                }
            }
            ctx.updateLoggers(config);
        }
    }
}