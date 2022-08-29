package com.cannontech.clientutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import com.cannontech.common.config.RemoteLoginSession;
import com.cannontech.common.log.model.CustomizedSystemLogger;
import com.cannontech.common.log.model.LoggerLevel;
import com.cannontech.common.log.model.SystemLogger;
import com.cannontech.common.util.ApplicationIdUnknownException;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;

/**
 * YukonLogManager manages the initialization of the log4j2 logging system. This includes: reading data from YukonLogging table
 * and creating new loggers. This class replaces the previous CTILogManager class while allowing existing logging to work as-is,
 * but also acts as a wrapper for new logging code. See comments on getLogger() methods below for how to properly use this class
 * to create new loggers inside a class.
 * 
 * @see com.cannontech.clientutils.CTILogger
 * @see com.cannontech.clientutils.YukonLogFileAppender
 *      10/24/2006
 * @author dharrington
 *
 */
public class YukonLogManager {

    // Constructor never gets used, YukonLogManager has only static methods
    private YukonLogManager() {
        super();
    }

    // initialize the logging at YukonLogManager creation
    static {
        try {
            // Initialize loggers for valid application Ids only. For Test cases, Eclipse builders(like encryption.xml), ignore
            // loading the logger. Test cases and builders will print the message default messages on eclipse console.
            BootstrapUtils.getApplicationId();
            initialize();
        } catch (ApplicationIdUnknownException e) {
            // Do nothing.
        }
    }

    /**
     * Load the loggers from YukonLogging table. Called once at the creation of YukonLogManager
     */
    public static synchronized void initialize() {
        List<SystemLogger> dbSystemLoggers = getDbSystemLoggers();
        // Add the system loggers with default value if there are no entries in YukonLogging table.
        if (shouldPopulateSystemLoggers(dbSystemLoggers)) {
            populateSystemLoggers(dbSystemLoggers);
        }
        //Prevent loading of expired loggers on start up.
        deleteExpiredLoggers();

        // Create a ConfigurationBuilder Object.
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(Level.INFO);
        builder.setPackages("com.cannontech.clientutils");

        // Create a default PatternLayout for ConsoleAppender and YukonRollingFileAppender.
        LayoutComponentBuilder patternLayout = builder.newLayout("PatternLayout")
                                                      .addAttribute("pattern", "%d %d{zzz} [%t] %-5p %c - %m%n%throwable");

        // Create PatternLayout for custom Appenders like yukonRfnRollingFile, commsRollingFile and yukonApiRollingFile
        // With default patternLayout, class name not getting logged for custom appenders. Difference : %c vs %C
        //Do not use this unless its required as generating the caller class information is slow.
        LayoutComponentBuilder customPatternLayout = builder.newLayout("PatternLayout")
                                                            .addAttribute("pattern", "%d %d{zzz} [%t] %-5p %C - %m%n%throwable");

        // Create triggeringPolicy
        ComponentBuilder<?> triggeringPolicy = builder.newComponent("Policies")
                                                      .addComponent(builder.newComponent("TimeBasedTriggeringPolicy")
                                                                           .addAttribute("interval", "1"));

        // Create DirectWriteRolloverStrategy
        ComponentBuilder<?> strategyBuilder = builder.newComponent("DirectWriteRolloverStrategy")
                                                     .addAttribute("maxFiles", 1);

        // Create console appender and add it to the builder.
        builder.add(builder.newAppender("console", "Console")
                           .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
                           .add(patternLayout));

        // Create yukonRollingFileAppender and add it to the builder.
        builder.add(builder.newAppender("yukonRollingFileAppender", "YukonRollingFile")
                           .add(patternLayout)
                           .addComponent(triggeringPolicy)
                           .addComponent(strategyBuilder));

        // Create commsRollingFile and add it to the builder.
        builder.add(builder.newAppender("commsRollingFile", "CommsRollingFile")
                           .add(customPatternLayout)
                           .addComponent(triggeringPolicy)
                           .addComponent(strategyBuilder));
        
        // Create smartNotifRollingFile and add it to the builder.
        builder.add(builder.newAppender("smartNotifRollingFile", "SmartNotifRollingFile")
                           .add(customPatternLayout)
                           .addComponent(triggeringPolicy)
                           .addComponent(strategyBuilder));

        // Create yukonRfnRollingFile and add it to the builder.
        builder.add(builder.newAppender("yukonRfnRollingFile", "YukonRfnRollingFile")
                           .add(customPatternLayout)
                           .addComponent(triggeringPolicy)
                           .addComponent(strategyBuilder));

        // Create yukonApiRollingFile and add it to the builder.
        builder.add(builder.newAppender("yukonApiRollingFile", "YukonApiRollingFile")
                           .add(customPatternLayout)
                           .addComponent(triggeringPolicy)
                           .addComponent(strategyBuilder));
        
        // Create yukonRfnRollingFile and add it to the builder.
        builder.add(builder.newAppender("eatonCloudCommsRollingFile", "EatonCloudCommsRollingFile")
                           .add(customPatternLayout)
                           .addComponent(triggeringPolicy)
                           .addComponent(strategyBuilder));

        // Load the other loggers which are there in DB table.User can add any class and corresponding logging level to the DB.
        getLoggers().forEach((loggerName, level) -> {
            if (CustomizedSystemLogger.isCustomizedAppenderLogger(loggerName)) {
                builder.add(builder.newLogger(loggerName, level)
                        .add(builder.newAppenderRef(getCustomizedAppenderRef(loggerName)))
                        .addAttribute("additivity", false));
            } else if (SystemLogger.isCustomAppenderLogger(loggerName)) {
                builder.add(builder.newLogger(loggerName, level)
                        .add(builder.newAppenderRef(getAppenderRef(loggerName)))
                        .addAttribute("additivity", false));
            } else {
                builder.add(builder.newLogger(loggerName, level));
            }
        });

        //Add package level loggers for customizable loggers.
        Arrays.asList(CustomizedSystemLogger.values()).forEach(customLogger -> {
            String packageNames[] = customLogger.getPackageNames();
            for (String packageName : packageNames) {
                builder.add(builder.newLogger(packageName, getLevel(getLoggers(), packageName))
                        .add(builder.newAppenderRef(getCustomizedAppenderRef(packageName)))
                        .addAttribute("additivity", false));
            }
        });

        //Disable Pentaho report generation logging
        builder.add(builder.newLogger("org.pentaho", Level.OFF));

        // Create root logger and add to the builder.
        builder.add(builder.newRootLogger(Level.INFO)
                           .add(builder.newAppenderRef("console"))
                           .add(builder.newAppenderRef("yukonRollingFileAppender")));

        // Build the configuration and initialize it by passing true.
        BuiltConfiguration configuration = builder.build(true);

        // Set the configuration in current LoggerContext
        getMyLogger().getContext().setConfiguration(configuration);
    }

    /**
     * Method to delete the old loggers on startup of services. This will prevent loading of expired loggers.
     */
    private static void deleteExpiredLoggers() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy");
            String sql = "DELETE FROM YukonLogging WHERE ExpirationDate < '" + format.format(new java.util.Date()) + "'";
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            ps = conn.prepareStatement(sql);
            ps.execute();
        } catch (SQLException e) {
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    /**
     * Return custom appender name for the specified loggerName.
     */
    public static String getAppenderRef(String loggerName) {
        String appenderRef = SystemLogger.getAppenderRef(loggerName);
        return StringUtils.isNotBlank(appenderRef) ? appenderRef : "yukonRollingFileAppender";
    }

    /**
     * Return the appender for the customized loggerName
     */
    public static String getCustomizedAppenderRef(String loggerName) {
        CustomizedSystemLogger customizedSystemLogger = CustomizedSystemLogger.getForLoggerName(loggerName);
        String appenderRef = SystemLogger.getAppenderRef(SystemLogger.getLoggerNameForCustomizedLogger(customizedSystemLogger));
        return StringUtils.isNotBlank(appenderRef) ? appenderRef : "yukonRollingFileAppender";
    }

    /**
     * Return logging level for customized logger package name
     */
    private static Level getLevel(Map<String, Level> loggersMap, String packageName) {
        CustomizedSystemLogger customizedSystemLogger = CustomizedSystemLogger.getForLoggerName(packageName);
        Level level = loggersMap.get(SystemLogger.getLoggerNameForCustomizedLogger(customizedSystemLogger));
        return level != null ? level : Level.INFO;
    }

    /**
     * This method will verify YukonLogging table and return true if it is empty.
     */
    private static boolean shouldPopulateSystemLoggers(List<SystemLogger> dbSystemLoggers) {
        return Arrays.stream(SystemLogger.values()).filter(systemlogger -> !dbSystemLoggers.contains(systemlogger)).findFirst()
                .isPresent();
    }

    private static List<SystemLogger> getDbSystemLoggers() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<SystemLogger> loggers = new ArrayList<SystemLogger>();
        try {
            String sql = "SELECT LoggerName FROM YukonLogging";
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                String loggerName = rs.getString("LoggerName");
                if (SystemLogger.isSystemLogger(loggerName)) {
                    loggers.add(SystemLogger.getForLoggerName(loggerName));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
        return loggers;
    }

    /**
     * This method will insert System Loggers(Default loggers) in YukonLogging table. Only execute once i.e for the 1st time with
     * Log4j2 migration.
     */
    private static void populateSystemLoggers(List<SystemLogger> dbLoggers) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO YukonLogging(LoggerId, LoggerName, LoggerLevel, ExpirationDate, Notes) VALUES (?,?,?,?,?)";
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            ps = conn.prepareStatement(sql);
            List<SystemLogger> newSystemLoggers = getNewSystemLoggers(dbLoggers);
            for (SystemLogger systemLogger : newSystemLoggers) {
                ps.setInt(1, getNextLoggerId());
                ps.setString(2, systemLogger.getLoggerName());
                ps.setString(3, systemLogger.getLevel().name());
                ps.setDate(4, null);
                ps.setString(5, null);
                ps.execute();
            }
        } catch (SQLException e) {
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }

        }
    }

    /**
     * Return next logger ID.
     */
    private static int getNextLoggerId() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT MAX(LoggerId) FROM YukonLogging";
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }

        }
        return 0;
    }

    private static List<SystemLogger> getNewSystemLoggers(List<SystemLogger> dbLoggers) {
        return Arrays.stream(SystemLogger.values()).filter(systemLogger -> !dbLoggers.contains(systemLogger))
                .collect(Collectors.toList());
    }

    /**
     * Method to retrieve all the loggers with level for loading the BuiltConfiguration on startup.
     */
    private static Map<String, Level> getLoggers() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Level> loggers = new HashMap<String, Level>();
        try {
            String sql = "SELECT LoggerName, LoggerLevel FROM YukonLogging";
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                loggers.put(rs.getString("LoggerName"), getApacheLevel(LoggerLevel.valueOf(rs.getString("LoggerLevel"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }

        }
        return loggers;
    }

    /**
     * Method to convert from LoggerLevel to Apache Level.
     */
    public static Level getApacheLevel(LoggerLevel level) {
        switch (level) {
        case DEBUG:
            return Level.DEBUG;
        case ERROR:
            return Level.ERROR;
        case FATAL:
            return Level.FATAL;
        case INFO:
            return Level.INFO;
        case OFF:
            return Level.OFF;
        case TRACE:
            return Level.TRACE;
        case WARN:
            return Level.WARN;
        }
        return Level.INFO;
    }

    /**
     * Try not to call this before Log4j is configured.
     * 
     * @return
     */
    public static Logger getMyLogger() {
        return (Logger) LogManager.getLogger(YukonLogManager.class);
    }

    /**
     * Used for initializing the logging for clients
     * 
     */
    public static synchronized void initialize(RemoteLoginSession remoteLoginSession) {
        initialize();
    }

    /**
     * Get an existing logger by class name. Creates a new logger
     * if it does not already exist. (Loggers are typically named
     * using the fully qualified class name.
     * 
     * @param c A class name in any of the following forms:
     *          In most cases: ClassName.class
     *          In some cases for base classes: this.getClass()
     * @return existing logger or new logger if it doesn't already exist
     */
    public static Logger getLogger(Class<?> c) {
        Logger tempLogger = (Logger) LogManager.getLogger(c);
        return tempLogger;
    }

    public static LogHelper getLogHelper(Class<?> c) {
        Logger logger = getLogger(c);
        return LogHelper.getInstance(logger);
    }

    /**
     * Get an existing logger by class name. Creates a new logger
     * if it does not already exist. (Loggers are typically named
     * using the fully qualified class name.
     * 
     * @param s A string that is the name of the logger in the following forms:
     *          "packageName.className" or
     *          className.class.getName()
     * @return existing logger or new logger if it doesn't already exist
     */
    public static Logger getLogger(String s) {
        Logger tempLogger = (Logger) LogManager.getLogger(s);
        return tempLogger;
    }

    /**
     * Returns rfnCommsLogger, used for logging communications to and from Network Manager.
     */
    public static Logger getRfnLogger() {
        return getLogger("rfnCommsLogger");
    }

    /**
     * Returns rfnCommsLogger for the specified class name, used for logging communications to and from Network Manager.
     */
    public static Logger getRfnLogger(Class<?> c) {

        Map<String, LoggerConfig> loggers = getMyLogger().getContext().getConfiguration().getLoggers();
        if (loggers.containsKey(c.getCanonicalName())) {
            return (Logger) LogManager.getLogger(c);
        } else {
            // For specifying a package structure for a custom logger
            for (String s : CustomizedSystemLogger.CUSTOM_RFN_COMMS_LOGGER.getPackageNames()) {
                if (loggers.containsKey(s)) {
                    return (Logger) LogManager.getLogger(s);
                }
            }
            // If class is not following package structure then return default logger.
            return getRfnLogger();
        }
    }

    /**
     * Returns eatonCloudCommsLogger, used for logging communications to and from Eaton Cloud.
     */
    public static Logger getEatonCloudCommsLogger() {
        return getLogger("eatonCloudCommsLogger");
    }

    /**
     * Returns eatonCloudCommsLogger, used for logging communications to and from Eaton Cloud.
     */
    public static Logger getEatonCloudCommsLogger(Class<?> c) {
        Map<String, LoggerConfig> loggers = getMyLogger().getContext().getConfiguration().getLoggers();
        if (loggers.containsKey(c.getCanonicalName())) {
            return (Logger) LogManager.getLogger(c);
        } else {
            // For specifying a package structure for a custom logger
            for (String s : CustomizedSystemLogger.CUSTOM_EATON_CLOUD_COMMS_LOGGER.getPackageNames()) {
                if (loggers.containsKey(s)) {
                    return (Logger) LogManager.getLogger(s);
                }
            }
            // If class is not following package structure then return default logger.
            return getEatonCloudCommsLogger();
        }
    }

    /**
     * Returns commsLogger, used for logging communications to and from Yukon Services.
     */
    public static Logger getCommsLogger() {
        return getLogger("commsLogger");
    }

    /**
     * Returns commsLogger for the specified class name, used for logging communications to and from Yukon Services.
     */
    public static Logger getCommsLogger(Class<?> c) {
        Map<String, LoggerConfig> loggers = getMyLogger().getContext().getConfiguration().getLoggers();
        if (loggers.containsKey(c.getCanonicalName())) {
            return (Logger) LogManager.getLogger(c);
        } else {
            // For specifying a package structure for a custom logger
            for (String s : CustomizedSystemLogger.CUSTOM_COMMS_LOGGER.getPackageNames()) {
                if (loggers.containsKey(s)) {
                    return (Logger) LogManager.getLogger(s);
                }
            }
            // If class is not following package structure then return default logger.
            return getCommsLogger();
        }
    }

    /**
     * Returns apiLogger, used for logging Rest Api calls.
     */
    public static Logger getApiLogger() {
        return getLogger("apiLogger");
    }

    /**
     * Returns apiLogger for the specified class name, used for logging Rest Api calls.
     */
    public static Logger getApiLogger(Class<?> c) {
        Map<String, LoggerConfig> loggers = getMyLogger().getContext().getConfiguration().getLoggers();
        if (loggers.containsKey(c.getCanonicalName())) {
            return (Logger) LogManager.getLogger(c);
        } else {
            // For specifying a package structure for a custom logger
            for (String s : CustomizedSystemLogger.CUSTOM_API_LOGGER.getPackageNames()) {
                if (loggers.containsKey(s)) {
                    return (Logger) LogManager.getLogger(s);
                }
            }
            // If class is not following package structure then return default logger.
            return getApiLogger();
        }
    }

    /**
     * Return smartNotifLogger
     */
    public static Logger getSmartNotificationsLogger() {
        return getLogger("smartNotifLogger");
    }

    /**
     * Return smartNotifLogger for the specified class name
     */
    public static Logger getSmartNotificationsLogger(Class<?> c) {
        Map<String, LoggerConfig> loggers = getMyLogger().getContext().getConfiguration().getLoggers();
        if (loggers.containsKey(c.getCanonicalName())) {
            return (Logger) LogManager.getLogger(c);
        } else {
            // For specifying a package structure for a custom logger
            for (String s : CustomizedSystemLogger.CUSTOM_SMART_NOTIFICATION_LOGGER.getPackageNames()) {
                if (loggers.containsKey(s)) {
                    return (Logger) LogManager.getLogger(s);
                }
            }
            // If class is not following package structure then return default logger.
            return getSmartNotificationsLogger();
        }
    }
}