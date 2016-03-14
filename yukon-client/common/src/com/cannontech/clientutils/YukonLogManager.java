package com.cannontech.clientutils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.DOMConfigurator;

import com.cannontech.common.config.RemoteLoginSession;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.CtiUtilities;


/**
 * YukonLogManager manages the initialization of the log4j logging system.
 * This includes: finding and loading log4j.xml configuration file and 
 * creating new loggers. This class replaces the previous CTILogManager class
 * while allowing existing logging to work as-is, but also acts as a wrapper for
 * new logging code. See comments on getLogger() methods below for how
 * to properly use this class to create new loggers inside a class.
 * @see com.cannontech.clientutils.CTILogger
 * @see com.cannontech.clientutils.YukonLogFileAppender
 * 10/24/2006
 * @author dharrington
 *
 */
public class YukonLogManager {
    
    
    private static final String YUKON_LOGGING_XML = "yukonLogging.xml";


    private static Logger rfnCommsLogger = setupRfnCommsLogger();
    private static final RfnLogger rfnLogger = new RfnLogger();
    
    //Constructor never gets used, YukonLogManager has only static methods
    private YukonLogManager() {
        super();
    }
    
    //initialize the logging at YukonLogManager creation  
    static {
        initialize();
    }  
 
    /**
     * Find the yukonLogging.xml configuration file depending on platform
     * and load that particular file. Called once at the creation of YukonLogManager
     */
    private static synchronized void initialize() {
        
        //typically c:\Yukon, but installation dependent
        String yukonBase = CtiUtilities.getYukonBase();
        
        //The path to the log file
        File path = new File(yukonBase + "/Server/Config/" + YUKON_LOGGING_XML);

        //See if the yukonLogging.xml config file is on the classpath
        //convert the xml file path to a url and configure log4j if url is successful
        URL url = null;
        url = CTILogger.class.getClassLoader().getResource(YUKON_LOGGING_XML);
        if (url != null){
            DOMConfigurator.configure(url);
            getMyLogger().info("The config file was found on classpath: " + url);
            dumpDebugInfo();
            return;
        } else if (path.canRead()){
            //update the file if it changes every 3000 milliseconds
            DOMConfigurator.configureAndWatch(path.getAbsolutePath(), 3000);
            getMyLogger().info("The config file was found under " + path);
            dumpDebugInfo();
            return;
        } else {
            //If all else fails use BasicConfigurator which will append messages to the 
            //console
            BasicConfigurator.configure();
            Logger.getRootLogger().setLevel(Level.INFO);
            getMyLogger().error("Unable to configure logging, using BasicConfigurator to log to console (path=" + path + ")");
            dumpDebugInfo();
            return;
        }   
    }
    
    private static void dumpDebugInfo() {
        Logger logger = getLogger("com.cannontech.sysinfo");
        if (logger.isInfoEnabled()) {
            logger.info("System info:\n" + CtiUtilities.getSystemInfoString());
        }
    }

    /**
     * Try not to call this before Log4j is configured.
     * @return
     */
    private static Logger getMyLogger() {
        return Logger.getLogger(YukonLogManager.class);
    }
    
    /**
     * Used for initializing the logging for clients
     * @param hostname the IP address of the host
     * @param port the connection port number
     */
    public static synchronized void initialize(RemoteLoginSession remoteLoginSession) {
        // try to configure logging
        try {
            InputStream inputStream = remoteLoginSession.getInputStreamForUrl("/servlet/LoggingServlet", false);
            new DOMConfigurator().doConfigure(inputStream, LogManager.getLoggerRepository());
        } catch (FactoryConfigurationError | IOException e) {
            // If all else fails use BasicConfigurator, log to console
            getMyLogger().error("Unable to configure logging, using BasicConfigurator to log to console, bad url. ", e);
            return;
        }

        // if that worked, setup YukonRemoteAppender 
        YukonRemoteAppender.configureLogger(remoteLoginSession);
        getMyLogger().info("The remote logging config file was found under: /servlet/LoggingServlet");
    }
    
    
    /**
     * Get an existing logger by class name. Creates a new logger
     * if it does not already exist. (Loggers are typically named
     * using the fully qualified class name.
     * @param c A class name in any of the following forms:
     * In most cases: ClassName.class
     * In some cases for base classes: this.getClass()
     * @return existing logger or new logger if it doesn't already exist
     */
    public static Logger getLogger(Class<?> c) {
        Logger tempLogger = Logger.getLogger(c);
        return  tempLogger; 
    }
    
    public static LogHelper getLogHelper(Class<?> c) {
        Logger logger = getLogger(c);
        return LogHelper.getInstance(logger);
    }
    
    /**
     * Get an existing logger by class name. Creates a new logger
     * if it does not already exist. (Loggers are typically named
     * using the fully qualified class name.
     * @param s A string that is the name of the logger in the following forms:
     * "packageName.className" or
     * className.class.getName()
     * @return existing logger or new logger if it doesn't already exist
     */
    public static Logger getLogger(String s) {
        Logger tempLogger = Logger.getLogger(s);
        return  tempLogger;	
    }
    
    /** 
     * This class serves as a wrapper for the rfnCommsLogger Logger.
     * Any classes that want to make use of RFN comms logging should obtain a reference
     * to this class by calling getRfnLogger(). 
     * 
     * This class guarantees that setupRfnCommsLogger() is called before each logging statement, 
     * which needs to be done in order to ensure that the proper appenders are attached to the logger. 
     */
    public static final class RfnLogger {
        public void warn(Object message) {
            setupRfnCommsLogger().warn(message);
        }
        
        public void warn(Object message, Throwable t) {
            setupRfnCommsLogger().warn(message, t);
        }
        
        public void error(Object message) {
            setupRfnCommsLogger().error(message);
        }
        
        public void error(Object message, Throwable t) {
            setupRfnCommsLogger().error(message, t);
        }
        
        public void info(Object message) {
            setupRfnCommsLogger().info(message);
        }
        
        public void info(Object message, Throwable t) {
            setupRfnCommsLogger().info(message, t);
        }
        
        public void debug(Object message) {
            setupRfnCommsLogger().debug(message);
        }
        
        public void debug(Object message, Throwable t) {
            setupRfnCommsLogger().debug(message, t);
        }
        
        public void trace(Object message) {
            setupRfnCommsLogger().trace(message);
        }
        
        public void trace(Object message, Throwable t) {
            setupRfnCommsLogger().trace(message, t);
        }
        
        public boolean isInfoEnabled() {
            return setupRfnCommsLogger().isInfoEnabled();
        }
        
        public boolean isDebugEnabled() {
            return setupRfnCommsLogger().isDebugEnabled();
        }
        
        public Level getLevel() {
            return setupRfnCommsLogger().getLevel();
        }
    }
    
    public static RfnLogger getRfnLogger() {
        return rfnLogger;
    }
    
    
    /**
     * Sets up the Logger and appender that are used for logging communications to and from Network Manager.
     * <br><br>
     * This method needs to be called each time a logging statement is made because when yukonLogging.xml is edited, 
     * all open appenders are closed and everything is reloaded from the xml configuration, but this one is not.
     * This appender is added programmatically outside of the xml configuration and so it needs to be 
     * re-created and attached to the logger.
     * <br><br>
     * (There are ways to avoid this in later versions of log4j, like log4j2.  We currently use log4j 1.2)
     * @return
     */
    private static Logger setupRfnCommsLogger() {
        if (rfnCommsLogger == null) {
            rfnCommsLogger = getLogger("rfnCommsLogger");
            if (rfnCommsLogger.getLevel() == null) {
                rfnCommsLogger.setLevel(Level.INFO);  // set to INFO by default if not defined in yukonLogging.xml
            }
        }
        
        // re-associate the appender with the rfnCommsLogger if it was removed during reload of yukonLogging.xml
        if (rfnCommsLogger.getAppender("rfnCommsFileAppender") == null) {
            // set up rfnFileAppender
            DatedFileAppender rfnAppender;
            String directory = BootstrapUtils.getServerLogDir() + "\\Comm";
            String logName = "RfnCommsLog";
            
            //create a DatedFileAppender to take over the actual appending, rollover, and timing issues
            rfnAppender = new DatedFileAppender(directory, logName + "_", ".log");
            rfnAppender.setName("rfnCommsFileAppender");
            rfnAppender.setFile(directory + logName + ".log");
            rfnAppender.setSystemInfoString(CtiUtilities.getSystemInfoString());
            rfnAppender.setMaxFileSize(YukonFileAppender.getMaxFileSize());    // same max as other logs, as 
                                                                               // defined in yukonLogging.xml
            rfnAppender.setMaxFileOpenRetries(DatedFileAppender.MAX_FILE_OPEN_RETRIES);
            rfnAppender.setRetryDelayInMillis(DatedFileAppender.RETRY_DELAY_IN_MS);
            rfnAppender.setLogRetentionDays(DatedFileAppender.LOG_RETENTION_DAYS);
            
            //The layout for the log file:
            Layout layout = new PatternLayout(YukonFileAppender.getConversionPattern());
            rfnAppender.setLayout(layout);
            
            //Inherited from AppenderSkeleton. Calls once options are set
            rfnAppender.activateOptions();
            rfnCommsLogger.addAppender(rfnAppender);
        }
        return rfnCommsLogger;
    }
}
