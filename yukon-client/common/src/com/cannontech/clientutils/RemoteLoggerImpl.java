package com.cannontech.clientutils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import com.cannontech.common.util.CtiUtilities;

/**
 * Implements the logging for remote clients.
 * The creation of specific appenders for each
 * client's computer and application occurs in
 * getAppender--see method comments below
 * @author tmack and dharrington
 *
 */
public class RemoteLoggerImpl implements RemoteLogger {
    
    // Use a DatedFileAppender to do the actual appending of messages
    private DatedFileAppender dailyRollingFileAppender;
    
    //Maximum log file size before logging stops
    private static long maxFileSize = 1073741824;
    
    //A map to hold client applicationName+ids (key) and file appenders (value)
    private Map<String, DatedFileAppender> appenderIPAddresses = new HashMap<String, DatedFileAppender>();
    
    /**
     * The conversion pattern sets the layout for the log4j logging
     * See the log4j docs for specifics on formatting characters. The 
     * default string uses: [date and time, thread that generated log event, 
     * the log level left justified, the logger name which is usually the fq-class name, 
     * log message and newline] 
     */
    private String conversionPattern = "%d [%t] %-5p %c - %m%n";

    /**
     * Default constructor
     */
    public RemoteLoggerImpl() {
        super();
    }

    /**
     * Responsible for setting up logging--calls getAppender which does the 
     * actual work of setting up logging and logging the message.
     * @param applicationName The name of the client program doing the logging
     * @param clientId the IP address of the client's computer
     * @param event The actual logging message event
     * @see com.cannontech.clientutils.RemoteLogger#doLog(java.lang.String, java.lang.String, org.apache.log4j.spi.LoggingEvent)
     */
    public void doLog(String applicationName, String clientId, LoggingEvent event) {
        getAppender(applicationName, clientId).doAppend(event);
    }
    
    /**
     * Gets a logger for the client if one already exists, otherwise
     * instantiates a DatedFileAppender, sets its attributes, and 
     * returns the appender. Appenders are stored in a HashMap and
     * their key values are the app name appended to the clientId 
     * key: CLIENT_ID = applicationName+clientId
     * @param applicationName The name of the client application
     * @param clientId The IP address of the client's computer
     * @return returns a DatedFileAppender which is a subclass of AppenderSkeleton
     */
    protected AppenderSkeleton getAppender(String applicationName, String clientId) {
        String fileName = applicationName + "[" + clientId.replace(':', '_') + "]";

        //see if the appender for this clientId already exists
        if (appenderIPAddresses.containsKey(fileName)) {
            dailyRollingFileAppender = appenderIPAddresses.get(fileName);
        } else {
            //get the file path based on yukonbase for this system
            String directory = YukonFileAppender.getLogDirectory();
            
            // one doesn't exist so create an appender and put it in the map
            //Use DatedFileAppender to take over the actual appending, rollover, and timing issues
            dailyRollingFileAppender = new DatedFileAppender(directory, fileName, ".log");
            dailyRollingFileAppender.setName(fileName + "-appender");
            dailyRollingFileAppender.setSystemInfoString(CtiUtilities.getSystemInfoString());
            dailyRollingFileAppender.setMaxFileSize(maxFileSize);
            
            //The layout for the log file:
            Layout layout = new PatternLayout(conversionPattern);
            dailyRollingFileAppender.setLayout(layout);
            
            //Inherited from AppenderSkeleton. Calls once options are set
            dailyRollingFileAppender.activateOptions();
            
            //add this appender to the map
            appenderIPAddresses.put(fileName, dailyRollingFileAppender);
        }
        
        return dailyRollingFileAppender;
    }

    /**
     * See log4j documents for conversion 
     * character specifications
     * @return the conversion pattern string
     * specifying the layout of log messages
     */
    public String getConversionPattern() {
        return conversionPattern;
    }

    /**
     * See log4j documents for conversion 
     * character specifications.
     * Sets the conversion pattern string
     * specifying the layout of log messages
     * @param conversionPattern a string
     * representing a log4j message layout
     */
    public void setConversionPattern(String conversionPattern) {
        this.conversionPattern = conversionPattern;
    }
}
