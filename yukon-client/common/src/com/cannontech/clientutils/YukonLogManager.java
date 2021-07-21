package com.cannontech.clientutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import com.cannontech.common.config.RemoteLoginSession;
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
        // Add the system loggers with default value if there are no entries in YukonLogging table.
        if (shouldPopulateSystemLoggers()) {
            populateSystemLoggers();
        }
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
                           .add(patternLayout)
                           .addComponent(strategyBuilder));

        // Create yukonRollingFileAppender and add it to the builder.
        builder.add(builder.newAppender("yukonRollingFileAppender", "YukonRollingFile")
                           .add(patternLayout)
                           .addComponent(triggeringPolicy)
                           .addComponent(strategyBuilder));

        // Create yukonRfnRollingFile and add it to the builder.
        builder.add(builder.newAppender("yukonRfnRollingFile", "YukonRfnRollingFile")
                           .add(customPatternLayout)
                           .addComponent(triggeringPolicy)
                           .addComponent(strategyBuilder));

        // Create commsRollingFile and add it to the builder.
        builder.add(builder.newAppender("commsRollingFile", "CommsRollingFile")
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

        // Load the other loggers which are there in DB table.User can add any class and corresponding logging level to the DB.
        // As of now I have created a dummy database and tested this.I am not committing the DB changes as it will fail in VM.
        getLoggers().forEach((loggerName, level) -> {
            if (SystemLogger.isCustomAppenderLogger(loggerName)) {
                builder.add(builder.newLogger(loggerName, level)
                                   .add(builder.newAppenderRef(getAppenderRef(loggerName)))
                                   .addAttribute("additivity", false));
            } else {
                builder.add(builder.newLogger(loggerName, level));
            }
        });

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
     * Return custom appender name for the specified loggerName.
     */
    public static String getAppenderRef(String loggerName) {
        switch (loggerName) {
        case "apiLogger":
            return "yukonApiRollingFile";
        case "commsLogger":
            return "commsRollingFile";
        case "rfnCommsLogger":
            return "yukonRfnRollingFile";
        }
        return StringUtils.EMPTY;
    }

    /**
     * This method will verify YukonLogging table and return true if it is empty. 
     */
    private static boolean shouldPopulateSystemLoggers() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT COUNT(*) FROM YukonLogging";
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getInt(1) == 0;
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
        return false;
    }

    /**
     * This method will insert System Loggers(Default loggers) in YukonLogging table. Only execute once i.e for the 1st time with
     * Log4j2 migration.
     */
    private static void populateSystemLoggers() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO YukonLogging(LoggerId, LoggerName, LoggerLevel, ExpirationDate, Notes) VALUES (?,?,?,?,?)";
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            ps = conn.prepareStatement(sql);
            SystemLogger[] loggers = SystemLogger.values();
            for (int i = 0; i < loggers.length; i++) {
                ps.setInt(1, i);
                ps.setString(2, loggers[i].getLoggerName());
                ps.setString(3, loggers[i].getLevel().name());
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
     * Returns commsLogger, used for logging communications to and from Yukon Services.
     */
    public static Logger getCommsLogger() {
        return getLogger("commsLogger");
    }

    /**
     * Returns apiLogger, used for logging Rest Api calls.
     */
    public static Logger getApiLogger() {
        return getLogger("apiLogger");
    }
}