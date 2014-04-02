package com.cannontech.common.util;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

/**
 * LoggerEventListener is used by the DatabaseEditor class
 * to listen for database editor events and log them to the 
 * appropriate log4j file. This class will replace the 
 * FileMessageLog class that previously logged these events 
 * to its own file outside of the log4j system.
 * @see DatabaseEditor.java
 * @author dharrington
 *
 */
public class LoggerEventListener implements MessageEventListener {
   
    /**
     * A logger for the LoggerEventListener
     */
    private Logger logger;

    /**
     * default constructor not used
     */
    private LoggerEventListener() {
        super();
    }
    
    /** 
     * Constructor takes a logger name as a string
     * @param loggerName the logger name which 
     * is usually the fully qualified class name
     */
    public LoggerEventListener(String loggerName) {
        super();
        Validate.notEmpty(loggerName, "loggerName must not be empty");
        logger = YukonLogManager.getLogger(loggerName);
    }
    
    /**
     * Constructor takes a logger name as a string
     * @param c A class name in any of the following forms:
     * In most cases: ClassName.class
     * In some cases for base classes: this.getClass()
     */
    public LoggerEventListener(Class c) {
        super();
        Validate.notNull(c, "class name must not be null.");
        logger = YukonLogManager.getLogger(c);
    }
    
    /**
     * Logs the event when a messageEvent occurs.
     * The log now goes to the same direcotry as other logging-- 
     * it was previously logging to it's own file/location
     * @see com.cannontech.common.util.MessageEventListener
     * #messageEvent(com.cannontech.common.util.MessageEvent)
     */
    public void messageEvent(MessageEvent event) {
        logger.info(event.toString());    
    }

}
