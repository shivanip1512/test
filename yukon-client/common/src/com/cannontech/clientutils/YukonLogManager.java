package com.cannontech.clientutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import com.cannontech.common.config.RemoteLoginSession;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.CtiUtilities;


/**
 * YukonLogManager manages the initialization of the log4j2 logging system.
 * This includes: finding and loading log4j2.xml configuration file and 
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

    private static final String REMOTE_LOGGING_XML = "remoteLogging.xml";

    private static final String REMOTE_LOGGING_CONFIG_FOLDER = CtiUtilities.getConfigDirPath() + "Yukon/Config/";

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

        // typically c:\Yukon, but installation dependent
        String yukonBase = CtiUtilities.getYukonBase();

        // The path to the log file
        File path = new File(yukonBase + "/Server/Config/" + YUKON_LOGGING_XML);

        File remoteLoggingFilePath = new File(REMOTE_LOGGING_CONFIG_FOLDER + REMOTE_LOGGING_XML);
        // See if the yukonLogging.xml config file is on the classpath
        // convert the xml file path to a url and configure log4j2 if url is successful
        URL url = null;
        url = CTILogger.class.getClassLoader().getResource(YUKON_LOGGING_XML);
        if (url != null) {
            try {
                getMyLogger().getContext().setConfigLocation(url.toURI());
                getMyLogger().info("The config file was found on classpath: " + url);
            } catch (URISyntaxException e) {
                defaultConsoleAppender(path);
            }
        } else if (path.canRead()) {
            getMyLogger().getContext().setConfigLocation(path.toURI());
            getMyLogger().info("The config file was found under " + path.toURI());
        } else if (BootstrapUtils.isWebStartClient() && remoteLoggingFilePath.canRead()) {
            getMyLogger().getContext().setConfigLocation(remoteLoggingFilePath.toURI());
            getMyLogger().info("The Remote Logging config file was found under " + remoteLoggingFilePath.toURI());
        } else {
            defaultConsoleAppender(path);
        }
    }

    /**
     * Try not to call this before Log4j is configured.
     * @return
     */
    private static Logger getMyLogger() {
        return (Logger) LogManager.getLogger(YukonLogManager.class);
    }
    
    /**
     * Used for initializing the logging for clients
     * @param hostname the IP address of the host
     * @param port the connection port number
     */
    public static synchronized void initialize(RemoteLoginSession remoteLoginSession) {
        File remoteLoggingFolder = new File(REMOTE_LOGGING_CONFIG_FOLDER);
        if (!remoteLoggingFolder.exists()) {
            remoteLoggingFolder.mkdirs();
        }
        File remoteLoggingFile = new File(remoteLoggingFolder, REMOTE_LOGGING_XML);
        // try to configure logging
        if (!remoteLoggingFile.exists()) {
           try (InputStream inputStream = remoteLoginSession.getInputStreamForUrl("/servlet/LoggingServlet", false);
               FileOutputStream outputStream = new FileOutputStream(remoteLoggingFile);) {
               IOUtils.copy(inputStream, outputStream);
               getMyLogger().getContext().setConfigLocation(remoteLoggingFile.toURI());
           } catch (FactoryConfigurationError | IOException e) {
            // If all else fails use BasicConfigurator, log to console
            getMyLogger().error("Unable to configure logging, using BasicConfigurator to log to console, bad url. ", e);
            return;
           }
        getMyLogger().info("The remote logging config file was found under: /servlet/LoggingServlet");
        }
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
        Logger tempLogger = (Logger) LogManager.getLogger(c);
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
     * Returns apiLogger, used for logging Rest Api calls.
     */
    public static Logger getApiLogger() {
        return getLogger("apiLogger");
    }

    /**
     * Default Appender which will append messages to the console if configuration file is not found in
     * Classpath as well as in Yukon Config Path.
     */
    private static void defaultConsoleAppender(File path) {
        Configurator.setRootLevel(Level.INFO);
        getMyLogger().error(
            "Unable to configure logging, using Basic Configuration to log to console (path=" + path + ")");
    }
}
