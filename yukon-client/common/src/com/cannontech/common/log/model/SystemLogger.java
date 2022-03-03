package com.cannontech.common.log.model;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public enum SystemLogger {
    // Custom appender loggers
    API_LOGGER("apiLogger", LoggerLevel.INFO, CustomizedSystemLogger.CUSTOM_API_LOGGER, "yukonApiRollingFile"),
    COMMS_LOGGER("commsLogger", LoggerLevel.INFO, CustomizedSystemLogger.CUSTOM_COMMS_LOGGER, "commsRollingFile"),
    RFN_COMMS_LOGGER("rfnCommsLogger", LoggerLevel.INFO, CustomizedSystemLogger.CUSTOM_RFN_COMMS_LOGGER, "yukonRfnRollingFile"),
    SMART_NOTIFICATION_LOGGER("smartNotifLogger", LoggerLevel.INFO, CustomizedSystemLogger.CUSTOM_SMART_NOTIFICATION_LOGGER,
            "smartNotifRollingFile"),

    // Package level loggers
    YUKON_CORE_LOGGER("com.cannontech", LoggerLevel.INFO),
    RFN_METER_EVENT_LOGGER("com.cannontech.amr.rfn.service.RfnMeterEventService", LoggerLevel.TRACE),
    SPRING_FRAMEWORK_LOGGERS("org.springframework", LoggerLevel.WARN),
    SPRING_DATASOURCE_LOGGER("org.springframework.jdbc.datasource.DataSourceTransactionManager", LoggerLevel.INFO),
    SPRING_CLIENT_MESSAGETRACING_LOGGER("org.springframework.ws.client.MessageTracing", LoggerLevel.TRACE),
    SPRING_SERVER_MESSAGETRACING_LOGGER("org.springframework.ws.server.MessageTracing", LoggerLevel.TRACE);

    private final static ImmutableMap<String, SystemLogger> lookupByLoggerName;
    private final static ImmutableSet<SystemLogger> customAppenderLoggers;
    private final static ImmutableMap<String, String> appenderRefByLoggerName;
    private final static ImmutableMap<CustomizedSystemLogger, String> lookupByCustomizedSystemLogger;

    static {
        ImmutableMap.Builder<String, SystemLogger> nameBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<String, String> appenderRefByLoggerNameBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<CustomizedSystemLogger, String> customizedSystemLoggerBuilder = ImmutableMap.builder();

        for (SystemLogger logger : values()) {
            nameBuilder.put(logger.loggerName, logger);
            if (StringUtils.isNotBlank(logger.appenderRef)) {
                appenderRefByLoggerNameBuilder.put(logger.loggerName, logger.appenderRef);
            }
            if (logger.customizedSystemLogger != null) {
                customizedSystemLoggerBuilder.put(logger.customizedSystemLogger, logger.loggerName);
            }
        }
        lookupByLoggerName = nameBuilder.build();
        customAppenderLoggers = ImmutableSet.of(API_LOGGER, COMMS_LOGGER, RFN_COMMS_LOGGER, SMART_NOTIFICATION_LOGGER);
        appenderRefByLoggerName = appenderRefByLoggerNameBuilder.build();
        lookupByCustomizedSystemLogger = customizedSystemLoggerBuilder.build();
    }

    private LoggerLevel level;
    private String loggerName;
    private CustomizedSystemLogger customizedSystemLogger;
    private String appenderRef;

    SystemLogger(String loggerName, LoggerLevel level) {
        this.loggerName = loggerName;
        this.level = level;
    }

    SystemLogger(String loggerName, LoggerLevel level, CustomizedSystemLogger customizedSystemLogger, String appenderRef) {
        this.loggerName = loggerName;
        this.level = level;
        this.customizedSystemLogger = customizedSystemLogger;
        this.appenderRef = appenderRef;
    }

    public LoggerLevel getLevel() {
        return level;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public CustomizedSystemLogger getCustomizedSystemLogger() {
        return customizedSystemLogger;
    }

    public String getAppenderRef() {
        return appenderRef;
    }

    public static SystemLogger getForLoggerName(String loggerName) {
        SystemLogger logger = lookupByLoggerName.get(loggerName);
        checkArgument(logger != null, loggerName);
        return logger;
    }

    public static boolean isSystemLogger(String loggerName) {
        checkArgument(loggerName != null);
        return lookupByLoggerName.get(loggerName) != null;
    }

    public static boolean isCustomAppenderLogger(String loggerName) {
        checkArgument(loggerName != null);
        return lookupByLoggerName.get(loggerName) != null ? customAppenderLoggers
                .contains(lookupByLoggerName.get(loggerName)) : false;
    }

    public static String getAppenderRef(String loggerName) {
        return appenderRefByLoggerName.get(loggerName);
    }

    public static String getLoggerNameForCustomizedLogger(CustomizedSystemLogger customizedSystemLogger) {
        return lookupByCustomizedSystemLogger.get(customizedSystemLogger);
    }
}