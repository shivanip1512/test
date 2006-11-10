package com.cannontech.clientutils;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.cannontech.common.util.CtiUtilities;

import org.springframework.util.FileCopyUtils;


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

    /**
     * create a logger for this class
     */
    private final static Logger logger = Logger.getLogger("com.cannontech.clientutils.YukonLogManager");
    
    /**
     *  typically c:\Yukon, but installation dependent
     */
    private static String yukonBase = CtiUtilities.getYukonBase();
    
    /**
     *  The path to the log file
     */
    private final static String path = yukonBase + "/Server/Config/" + YUKON_LOGGING_XML;
    
    //Constructor never gets used, YukonLogManager has only static methods
    private YukonLogManager() {
        super();
    }
    
    //initialize the logging at YukonLogManager creation
    static {
        initialize();
    }  
    
    /**
     * find the yukonLogging.xml configuration file depending on platform
     * and load that particular file. Ignore initialization if it has already taken place.
     */
    private static synchronized void initialize() {
        
        //See if the yukonLogging.xml config file is on the classpath
        //convert the xml file path to a url and configure log4j if url is successful
        URL url = null;
        url = CTILogger.class.getClassLoader().getResource(YUKON_LOGGING_XML);
        if (url != null){
            DOMConfigurator.configure(url);
            logger.info("The config file was found on classpath: " + url);
            return;
        } else if (yukonBase != null){
            //if !webserver get file under YUKON_BASE/Server/Log/yukonLogging.xml
            //update the file if it changes every 3000 milliseconds
            DOMConfigurator.configureAndWatch(path, 3000);
            logger.info("The config file was found under " + path);
            return;
        } else {
            //If all else fails use BasicConfigurator which will append messages to the 
            //console
            BasicConfigurator.configure();
            logger.error("Unbable to configure logging, using BasicConfigurator to log to console");
            return;
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
    public static Logger getLogger(Class c) {
        Logger tempLogger = Logger.getLogger(c);
        return  tempLogger; 
    }
    
    /**
     * Get an existing logger by class name. Creates a new logger
     * if it does not already exist. (Loggers are typically named
     * using the fully qualified class name.
     * @param s A string that is the name of the logger in the following forms:
     * “packageName.className” or
     * className.class.getName()
     * @return existing logger or new logger if it doesn't already exist
     */
    public static Logger getLogger(String s) {
        Logger tempLogger = Logger.getLogger(s);
        return  tempLogger;	
    }
    
}
