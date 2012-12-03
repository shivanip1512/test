package com.cannontech.clientutils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * CTILogger acts as a wrapper for log4j, and all older log messages
 * use its static methods to post to the log file--
 * allowing existing log code to continue working as-is.
 * CTILogger has been modified to more efficiently extract the fully
 * qualified class name from the current thread.
 * 10/24/2006
 * @author dharrington
 *
 */
public class CTILogger  {
    
     /**
      * Constructor for CTILogger.
      * @param arg0
      */
    private CTILogger() {
        super();
    }
    
    /**
     * Returns the fully qualified class name for the class that called the logging method by using the current
     * thread's getStackTrace method.
     */
    public static String getFullyQualifiedClassName() {
        StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();
        String className = stackElements[3].getClassName();
        return className;
    }

    /**
     * @return the level of the logger that called this method
     */
    public static Level getLevel() {
        String className = getFullyQualifiedClassName();
        Logger logger = YukonLogManager.getLogger(className);
        return logger.getLevel();
    }
     
    /**
     * @param msg a debug level message
     */
    public static void debug(Object msg) {
        String className = getFullyQualifiedClassName();
        Logger logger = YukonLogManager.getLogger(className);
        logger.debug(msg);
    }
    
    /**
     * @param msg a debug level message
     * @param t the exception thrown
     */
    public static void debug(Object msg, Throwable t) {
        String className = getFullyQualifiedClassName();
        Logger logger = YukonLogManager.getLogger(className);
        logger.debug(msg, t);
    }
    
    /**
     * @param msg an info level message
     */
    public static void info(Object msg) {
        String className = getFullyQualifiedClassName();
        Logger logger = YukonLogManager.getLogger(className);
        logger.info(msg);
    }
    
    /**
     * @param msg an info level message
     * @param t the exception thrown
     */
    public static void info(Object msg, Throwable t) {
        String className = getFullyQualifiedClassName();
        Logger logger = YukonLogManager.getLogger(className);
        logger.info(msg, t);
    }
    
    /**
     * @param msg an error level message
     */
    public static void error(Object msg) {
        String className = getFullyQualifiedClassName();
        Logger logger = YukonLogManager.getLogger(className);
        logger.error(msg);
    }
    
    /**
     * @param msg an error level message
     * @param t the exception thrown
     */
    public static void error(Object msg, Throwable t) {
        String className = getFullyQualifiedClassName();
        Logger logger = YukonLogManager.getLogger(className);
        logger.error(msg, t);
    }
    
    /**
     * @param msg a fatal level message
     */
    public static void fatal(Object msg) {
        String className = getFullyQualifiedClassName();
        Logger logger = YukonLogManager.getLogger(className);
        logger.fatal(msg);
    }
    
    /**
     * @param msg a fatal level message
     * @param t the exception thrown
     */
    public static void fatal(Object msg, Throwable t) {
        String className = getFullyQualifiedClassName();
        Logger logger = YukonLogManager.getLogger(className);
        logger.fatal(msg, t);
    }
    
    /**
     * @param msg a warn level message
     */
    public static void warn(Object msg) {
        String className = getFullyQualifiedClassName();
        Logger logger = YukonLogManager.getLogger(className);
        logger.warn(msg);
    }
    
    /**
     * @param msg a warn level message
     * @param t the exception thrown
     */
    public static void warn(Object msg, Throwable t) {
        String className = getFullyQualifiedClassName();
        Logger logger = YukonLogManager.getLogger(className);
        logger.warn(msg, t);
    }

}
