package com.cannontech.clientutils;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import com.cannontech.common.util.CtiUtilities;

/**
 * YukonFileAppender is a custom log4j appender that locates
 * where the current system should log messages. Most of the actual
 * appending is delegated to a DatedFileAppender called 
 * dailyRollingFileAppender that handles actual appending,
 * roll over, and timing issues. YukonFileAppender does set
 * the layout for the log file and determines the application
 * that is running this appender.
 * @see com.cannontech.ctiutilits.DatedFileAppender 
 *  10/24/2006
 * @author dharrington
 *
 */

public class YukonFileAppender extends AppenderSkeleton {
    
    /**
     * maximum file size set at 1 gigabyte
     */ 
    private static long maxFileSize = 1073741824; 
    
    /**
     *  This appenders name
     */
	private String name;
    
    /**
     *  The appender that YukonFileAppender delegates its work
     */
    private DatedFileAppender dailyRollingFileAppender;

    /**
     * The default constructor initializes the location of
     * the file for logging an delegates most of the work
     * to DatedFileAppender. It also sets the layout for
     * the log file
     */
    public YukonFileAppender() {
    	
        //initialize dailyRollingFileAppender 
        //get the name of the application running this appender
        
        String nameOfApp = CtiUtilities.getApplicationName(); 
        
        //get the file path based on yukonbase for this system
        String fileName = CtiUtilities.getYukonBase() + "/Client/Log/" + nameOfApp + ".log";
        String directory = CtiUtilities.getYukonBase() + "/Client/Log/";
        
        //create a DatedFileAppender to take over the actual appending, rollover, and timing issues
        dailyRollingFileAppender = new DatedFileAppender(directory, nameOfApp + "_", ".log");
        dailyRollingFileAppender.setName("dailyRollingFileAppender");
        dailyRollingFileAppender.setFile(fileName);
        
        //The layout for the log file:
        //[date and time, thread that generated log event, the log level left justified, 
        // the logger name which is usually the fq-class name, log message and newline] 
        Layout layout = new PatternLayout("%d [%t] %-5p %c - %m%n");
        dailyRollingFileAppender.setLayout(layout);
        
        //Inherited from AppenderSkeleton. Calls once options are set
        dailyRollingFileAppender.activateOptions();
        
    }

    /**
     * @Override append in AppenderSkeleton
     */
    protected void append(LoggingEvent arg0) {
        dailyRollingFileAppender.append(arg0);
    }

    /**
     * @Override requiresLayout in AppenderSkeleton
     */
    public boolean requiresLayout() {
        return dailyRollingFileAppender.requiresLayout();
    }

    /**
     * @Override close in AppenderSkeleton
     */
    public void close() {
       dailyRollingFileAppender.close();
    }
	
    /**
     * @return name for this appender
     */
	public final String getAppenderName() {
		return this.name;
	}
    
	/**
	 * @param name is the name of this appender
	 */
	public final void setAppenderName(String name) {
		this.name = name;
	}
    
    /**
     * return the maximum file size for the log file
     * default is set to 1 gigabyte
     */
    public static final long getMaxFileSize() {
        return maxFileSize;
    }
    
    /**
     * @param size the size in bytes 
     * set the maximum file size for the log file
     * default is set to 1 gigabyte
     */
    public static final void setMaxFileSize(long size) {
        maxFileSize = size;
    }
}



