package com.cannontech.clientutils;

import java.io.FileNotFoundException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

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
    
    
    /**
     * create a logger for this class
     */
    private final static Logger logger = Logger.getLogger(YukonLogManager.class.getClass());
    
    /**
     *  typically c:\Yukon, but installation dependent
     */
    private static String yukonBase = CtiUtilities.getYukonBase();
    
    /**
     *  The path to the log file
     */
    private final static String path = yukonBase + "/Client/Log/log4j.xml";
    
    /**
     *  boolean flag for logging configuration
     */
    private static boolean alreadyConfigured = false;
    
    //Constructor never gets used, YukonLogManager has only static methods
    private YukonLogManager() {
        super();
    }
    
    //initialize the logging at YukonLogManager creation
    static {
        initialize();
    }
    
    /**
     * find the log4j.xml configuration file depending on platform
     * and load that particular file. Ignore initialization if it has already taken place.
     */
    public static synchronized void initialize() {
        if (alreadyConfigured)
            return;
        
        //See if the log4j.xml config file is on the classpath
        //convert the xml file path to a url and configure log4j if url is successful
        URL url = null;
        url = CTILogger.class.getClassLoader().getResource("log4j.xml");
        if (url != null){
            DOMConfigurator.configure(url);
            logger.info("The log4j.xml config file was found on classpath");
            alreadyConfigured = true;
            return;
        /*} else if (!CtiUtilities.isRunningAsWebApplication()) {
            return; */
        } else if (yukonBase != null){
            //if !webserver get file under YUKON_BASE/Client/Log/log4j.xml
            DOMConfigurator.configure(path);
            logger.info("The log4j.xml config file was found under " + path);
            alreadyConfigured = true;
            return;
        }
    }
    
    /**
     * Creates a new logger for all new logging code (post 
     * we'll now instantiate a logger in each new class/file by:
     * @param c A class name in any of the following forms:
     * Final static Logger logger = YukonLogManager.getLogger(X.class);
     * For base classes use Logger logger = YukonLogManager.getLogger(this.getClass());
     * @return existing logger or new logger if it doesn't already exist
     */
    public static Logger getLogger(Class c) {
        Logger tempLogger = Logger.getLogger(c);
        return  tempLogger; 
    }
    
    /**
     * Creates a new logger for all new logging code
     * we'll now instantiate a logger in each new class/file by:
     * @param c A class name in any of the following forms:
     * Final static Logger logger = YukonLogManager.getLogger(“com.wombat.X”);
     * Final static Logger logger = YukonLogManager.getLogger(X.class.getName());
     * @return existing logger or new logger if it doesn't already exist
     */
    public static Logger getLogger(String s) {
        Logger tempLogger = Logger.getLogger(s);
        return  tempLogger;	
    }
    
}
