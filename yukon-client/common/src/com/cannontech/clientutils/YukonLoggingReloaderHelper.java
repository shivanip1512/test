package com.cannontech.clientutils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.logger.dao.YukonLoggerDao;
import com.cannontech.clientutils.logger.service.YukonLoggerService.SortBy;
import com.cannontech.common.log.model.CustomizedSystemLogger;
import com.cannontech.common.log.model.LoggerLevel;
import com.cannontech.common.log.model.SystemLogger;
import com.cannontech.common.log.model.YukonLogger;
import com.cannontech.common.model.Direction;
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
        int logRetentionDays = globalSettingDao.getInteger(GlobalSettingType.LOG_RETENTION_DAYS);
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

    public void reloadYukonLoggers(DbChangeType dbChangeType, int loggerId) {
        // Do not load the Configuration on startup as YukonLogManager loads it for every application. When there user
        // add/edit/delete a logger, reload the Configuration.
        if (dbChangeType != DbChangeType.NONE) {
            log.info("Yukon loggers are reloaded");
            // Get the current context and configuration
            LoggerContext ctx = YukonLogManager.getMyLogger().getContext();
            Configuration config = ctx.getConfiguration();

            if (dbChangeType != DbChangeType.DELETE) {
                YukonLogger logger = yukonLoggerDao.getLogger(loggerId);
                // All the loggers should log the details in the log file and console. So retrieve these 2 appenders from
                // configuration.
                String loggerName = logger.getLoggerName();
                String appenderRef = CustomizedSystemLogger.isCustomizedAppenderLogger(loggerName) ? YukonLogManager
                        .getCustomizedAppenderRef(loggerName) : YukonLogManager.getAppenderRef(loggerName);
                Appender appender = config.getAppender(appenderRef);
                Appender consoleAppender = config.getAppender("console");

                // Create the AppenderRef[] which will be used to create LoggerConfig
                AppenderRef ref = AppenderRef.createAppenderRef(appender.getName(), null, config.getFilter());
                AppenderRef consoleRef = AppenderRef.createAppenderRef(consoleAppender.getName(), null, config.getFilter());
                AppenderRef[] refs = new AppenderRef[] { ref, consoleRef };

                Level level = YukonLogManager.getApacheLevel(logger.getLevel());
                updateLogger(loggerName, level, refs, config, appender, consoleAppender);
                // Update dependent package level loggers for customizable loggers
                if (SystemLogger.isCustomAppenderLogger(loggerName)) {
                    CustomizedSystemLogger customLogger = SystemLogger.getForLoggerName(loggerName)
                                                                      .getCustomizedSystemLogger();
                    for (String packageName : customLogger.getPackageNames()) {
                        updateLogger(packageName, level, refs, config, appender, consoleAppender);
                    }
                }
            } else {
                // Retrieve the all the loggers from DB table.
                List<YukonLogger> currentDbloggers = yukonLoggerDao.getLoggers(StringUtils.EMPTY, SortBy.NAME, Direction.asc,
                        Collections.<LoggerLevel>emptyList());
                // Get current loggerNames
                Set<String> currentDbLoggerNames = currentDbloggers.stream()
                                                                   .map(YukonLogger::getLoggerName)
                                                                   .collect(Collectors.toSet());
                // Get existing loggers from configuration.
                Set<String> existingLoggers = config.getLoggers().keySet();
                // remove the loggers if its there in database as we only need to delete the logger which is not available in
                // database.
                List<String> deletedLoggers = existingLoggers.stream()
                                                             .filter(name -> !currentDbLoggerNames.contains(name))
                                                             .collect(Collectors.toList());
                deletedLoggers.stream()
                        .forEach(name -> {
                            if (StringUtils.isNotBlank(name)) {
                                config.removeLogger(name);
                            }
                        });
            }
            ctx.updateLoggers(config);
        }
    }

    /**
     * Update/add the logger specified by the loggerName.
     */
    private void updateLogger(String packageName, Level level, AppenderRef[] refs, Configuration config,
            Appender appender, Appender consoleAppender) {
        LoggerConfig loggerConfig = LoggerConfig.createLogger(false, level, packageName, "true", refs, null,
                config, null);
        loggerConfig.addAppender(appender, null, config.getFilter());
        loggerConfig.addAppender(consoleAppender, null, config.getFilter());
        // addLogger() does not add the logger if its already there.So first remove the logger and then add back to
        // config
        config.removeLogger(packageName);
        config.addLogger(packageName, loggerConfig);
    }
}