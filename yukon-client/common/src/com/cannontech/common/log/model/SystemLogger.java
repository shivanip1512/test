package com.cannontech.common.log.model;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public enum SystemLogger {
    // Custom appender loggers
    API_LOGGER("apiLogger", LoggerLevel.INFO),
    COMMS_LOGGER("commsLogger", LoggerLevel.INFO),
    RFN_COMMS_LOGGER("rfnCommsLogger", LoggerLevel.INFO),
    SMART_NOTIFICATION_LOGGER("smartNotifLogger", LoggerLevel.INFO),

    // Package level loggers
    YUKON_CORE_LOGGER("com.cannontech", LoggerLevel.INFO),
    RFN_METER_EVENT_LOGGER("com.cannontech.amr.rfn.service.RfnMeterEventService", LoggerLevel.TRACE),
    SPRING_FRAMEWORK_LOGGERS("org.springframework", LoggerLevel.WARN),
    SPRING_DATASOURCE_LOGGER("org.springframework.jdbc.datasource.DataSourceTransactionManager", LoggerLevel.INFO),
    SPRING_CLIENT_MESSAGETRACING_LOGGER("org.springframework.ws.client.MessageTracing", LoggerLevel.TRACE),
    SPRING_SERVER_MESSAGETRACING_LOGGER("org.springframework.ws.server.MessageTracing", LoggerLevel.TRACE);

    private final static ImmutableMap<String, SystemLogger> lookupByLoggerName;
    private final static ImmutableSet<SystemLogger> customAppenderLoggers ;
    static {
        ImmutableMap.Builder<String, SystemLogger> nameBuilder = ImmutableMap.builder();
        for (SystemLogger logger : values()) {
            nameBuilder.put(logger.loggerName, logger);
        }
        lookupByLoggerName = nameBuilder.build();
        customAppenderLoggers = ImmutableSet.of(API_LOGGER, COMMS_LOGGER, RFN_COMMS_LOGGER, SMART_NOTIFICATION_LOGGER);
    }

    private LoggerLevel level;
    private String loggerName;

    SystemLogger(String loggerName, LoggerLevel level) {
        this.loggerName = loggerName;
        this.level = level;
    }

    public LoggerLevel getLevel() {
        return level;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public SystemLogger getForLoggerName(String loggerName) {
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
}