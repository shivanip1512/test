package com.cannontech.clientutils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import org.apache.log4j.FileAppender;
import org.apache.log4j.spi.LoggingEvent;

// Based on DatedFileAppender from http://minaret.biz/tips/datedFileAppender.html
// Written by Geoff Mottram (geoff at minaret dot biz).
// Placed in the public domain on August 28, 2004 by the author

/**
 * DatedFileAppender appends Log4j logging events to a different log file every
 * day. The log file name consists of a prefix (defaults to "application."),
 * today's date (as YYYY-MM-DD) and a suffix (defaults to ".log").
 * DatedFileAppender creates a new log file with the first message it logs on a
 * given day and continues to use that same file throughout the day.
 * <p>
 * Two static utility methods are provided for Calendar manipuation:
 * {@link #datestamp datestamp(Calendar calendar)} (which creates a string in
 * the form of "YYYY-MM-DD") and {@link #tomorrow tomorrow(Calendar calendar)}
 * (which sets <code>calendar</code> to the start of the next day).
 * </p>
 * <p>
 * DatedFileAppender supports buffered writes to its log file. By default, no
 * buffering is used and log entries are written to the log file as soon as they
 * are received. This insures that no new entries will be added to a log file
 * after midnight of any given day. On a heavily loaded system there could be
 * cases where the logger is preempted while writing an entry just before
 * midnight such that the log file would not be physically updated until a
 * little after midnight. Any log file related cron jobs should be run a
 * reasonable time after midnight to be on the safe side.
 * </p>
 * <p>
 * If you enable buffering by setting the <code>BufferedIO=true</code>
 * property in your log4j.properties file, there is no guarantee that
 * DatedFileAppender has finished writing to a daily log file after midnight. A
 * new message must be logged after midnight to force the previous day's log
 * file to be flushed and closed.
 * </p>
 * <p>
 * If you are logging so much data that you require write buffering, you will
 * probably log a message soon after midnight, forcing a log file roll-over. To
 * be absolutely sure, any cron based external log file processing that is done
 * could wait for the new day's log file to appear before starting its work on
 * what is now yesterday's log file.
 * </p>
 * The following sample log4j.properties file explains the configuration options
 * for this class and may be used to configure Log4j as the Tomcat system
 * logger:
 * </p>
 * <p>
 * The following sample log4j.properties file explains the configuration options
 * for this class and may be used to configure Log4j as the Tomcat system
 * logger:
 * </p>
 * 
 * <pre>
 *  #
 *  # log4j.properties
 *  #
 *  # Configures Log4j as the Tomcat system logger
 *  # using the custom DatedFileAppender.
 *  #
 *  
 *  #
 *  # Configure the logger to output ERROR or INFO level messages into
 *  # a Tomcat-style rolling, dated log file (&quot;tomcat.DATE.log&quot;).
 *  #
 *  #log4j.rootLogger=ERROR, T
 *  log4j.rootLogger=INFO, T
 *  
 *  #
 *  # Configure the appender &quot;T&quot;.
 *  #
 *  # Note that the default file name prefix is being overridden
 *  # to be &quot;tomcat.&quot; instead of &quot;application.&quot;.
 *  #
 *  log4j.appender.T=biz.minaret.log4j.DatedFileAppender
 *  log4j.appender.T.layout=org.apache.log4j.PatternLayout
 *  log4j.appender.T.layout.ConversionPattern=%d [%t] %-5p %c - %m%n
 *  #
 *  # If you don't want to use the DatedFileAppender default values
 *  # seen below, uncomment that line and change the setting.
 *  #
 *  # Directory: If the directory name does not start with a leading slash,
 *  # the directory name will be relative to your Tomcat home directory.
 *  #log4j.appender.T.Directory=logs
 *  #
 *  # Prefix: The log file name prefix.
 *  #log4j.appender.T.Prefix=application.
 *  log4j.appender.T.Prefix=tomcat.
 *  #
 *  # Suffix: The log file name suffix.
 *  #log4j.appender.T.Suffix=.log
 *  #
 *  # Append: true to append when opening a log file (good when restarting
 *  # Tomcat) or false to truncate.
 *  #log4j.appender.T.Append=true
 *  #
 *  # BufferedIO: true to use a buffered output stream to the log file (improves
 *  # performance when logging a lot of data but not so good if the system
 *  # crashes or you want to watch the logs in real time) or false to write
 *  # flush each message out to the log file.
 *  #
 *  # The default behavior of using non-buffered writes insures that a day's
 *  # log file will not be written to after midnight. When buffering is enabled,
 *  # a new message must be written to a log after midnight to force the previous
 *  # day's log file to be flushed and closed.
 *  #
 *  #log4j.appender.T.BufferedIO=false
 *  #
 *  # BufferSize: sets the size of the buffer to use if BufferedIO is true.
 *  # The default size is 8K.
 *  #log4j.appender.T.BufferSize=8192
 *  
 *  #
 *  # Application logging options
 *  #
 *  #log4j.logger.org.apache=DEBUG
 *  #log4j.logger.org.apache=INFO
 *  #log4j.logger.org.apache.struts=DEBUG
 *  #log4j.logger.org.apache.struts=INFO
 * </pre>
 * 
 * @author Geoff Mottram
 */

/**
 * The open source DatedFileAppender has been modified to work with Yukon
 * in the following ways: Original author is Geoff Mottram
 * 1. A file for each day of the current month is kept in the
 * following format: fileName_dd.log
 * 2. If the file size exceeds 1 gigabyte, logging stops for the day
 * and an error message is logged to the file
 * 3. Files from previous months are deleted when the file for the 
 * next month with the same name is created. Therefore, there are never
 * more than 31 files kept for a particular logger.
 * @see com.cannontech.clientutils.YukonFileAppender
 * 10/24/2006
 * @author dharrington
 *
 */

public class DatedFileAppender extends FileAppender {

    //----------------------------------------------------- class Variables
    /**
     * The number of milliseconds in a day
     */
    private final static long millisecondsInADay = 86400000L;

    /**
     * tells whether the maxFileSize has been reached
     */
    private static boolean isMaxFileSizeReached = false;
    
    //----------------------------------------------------- Instance Variables
    
    /**
     * The directory in which log files are created. Wihtout a leading slash,
     * this is relative to the Tomcat home directory.
     */
    private String m_directory = "logs";

    /**
     * The prefix that is added to log file filenames.
     */
    private String m_prefix = "tomcat.";

    /**
     * The suffix that is added to log file filenames.
     */
    private String m_suffix = ".log";

    /**
     * The File representation of the directory in which log files are created.
     */
    private File m_path = null;

    /**
     * A calendar object for manipulating dates and times.
     */
    private Calendar m_calendar = null;

    /**
     * The number of milliseconds since 1/1/1970 when tomorrow starts (local
     * time).
     */
    private long m_tomorrow = 0L;

    // ----------------------------------------------------------- Constructors

    /**
     * The default constructor will create a Tomcat FileLogger with the
     * following characteristics:
     * <ul>
     * <li>directory: "logs"</li>
     * <li>prefix: "tomcat."</li>
     * <li>suffix: ".log"</li>
     * </ul>
     */
    public DatedFileAppender() {
    }

    /**
     * Creates a new <code>DatedFileAppender</code> with the specified
     * characteristics.
     * @param directory the directory in which log files are created.
     * @param prefix the prefix that is added to log file filenames.
     * @param suffix the suffix that is added to log file filenames.
     */
    public DatedFileAppender(String directory, String prefix, String suffix) {
        m_directory = directory;
        m_prefix = prefix;
        m_suffix = suffix;

        activateOptions();
    }

    // ------------------------------------------------------------- Properties

    /**
     * Return the directory in which we create log files.
     */
    public String getDirectory() {
        return m_directory;
    }

    /**
     * Set the directory in which we create log files.
     * @param directory The new log file directory
     */
    public void setDirectory(String directory) {
        m_directory = directory;
    }

    /**
     * Return the log file prefix.
     */
    public String getPrefix() {
        return m_prefix;
    }

    /**
     * Set the log file prefix.
     * @param prefix The new log file prefix
     */
    public void setPrefix(String prefix) {
        m_prefix = prefix;
    }

    /**
     * Return the log file suffix.
     */
    public String getSuffix() {
        return m_suffix;
    }

    /**
     * Set the log file suffix.
     * @param suffix The new log file suffix
     */
    public void setSuffix(String suffix) {
        m_suffix = suffix;
    }

    // --------------------------------------- Public Methods

    /**
     * Called once all options have been set on this Appender. Calls the
     * underlying FileLogger's start() method.
     */
    public void activateOptions() {
        if (m_prefix == null) {
            m_prefix = "";
        }
        if (m_suffix == null) {
            m_suffix = "";
        }
        if ((m_directory == null) || (m_directory.length() == 0)) {
            m_directory = ".";
        }
        m_path = new File(m_directory);
        m_path.mkdirs();
        if (m_path.canWrite()) {
            m_calendar = Calendar.getInstance(); // initialized
        }
    }

    /**
     * Called by AppenderSkeleton.doAppend() to write a log message formatted
     * according to the layout defined for this appender.
     */
    public void append(LoggingEvent event) {
        File newFile = null;
        StringBuilder sb = null;
        String datestamp = null;
        if (this.layout == null) {
            errorHandler.error("No layout set for the appender named [" + name + "].");
            return;
        }
        if (this.m_calendar == null) {
            errorHandler.error("Improper initialization for the appender named [" + name + "].");
            return;
        }
        long n = System.currentTimeMillis();
        
        if (n >= m_tomorrow) {
            //If the current time is the next day, then create a new file for
            //tomorrow--always first deleting the file with the same name/day from last month
            //this condition is only met once per day
            
            m_calendar.setTimeInMillis(n); // set Calendar to current time
            
            datestamp = datestamp(m_calendar); // convert to string "yyyy-mm-dd"
            tomorrow(m_calendar); // set the Calendar to the start of tomorrow
            isMaxFileSizeReached = false; //file is always empty at beginning of 
                                          //new day, so reset to false

            //time in millis when tomorrow starts
            m_tomorrow = m_calendar.getTimeInMillis(); 
            sb = new StringBuilder(m_prefix + datestamp + m_suffix);
            newFile = new File(m_path, sb.toString());

            // if the file has a timestamp that is earlier than the beginning of
            // today, then it must be last months file, so delete its contents 
            //and start with an empty file
            long today = m_calendar.getTimeInMillis() - millisecondsInADay; 
            long lastModified = newFile.lastModified(); 

            if ((lastModified < today) && lastModified != 0) {
                //lastModified == 0 if the file doesn't exist
                newFile.delete();
            }

            this.fileName = newFile.getAbsolutePath();

            super.activateOptions(); // close current file and open new file
        } // end outer if

        if (this.qw == null) { // should never happen
            errorHandler.error("No output stream or file set for the appender named [" + name + "].");
            return;
        }

        File tempFile = new File(fileName);
        if (tempFile.canRead() && (tempFile.length() > YukonFileAppender.getMaxFileSize())) {
            //if the max file size is reached, append an error message to the file
            // (and possibly give some kind of alarm later)
            // also set isMaxSizeReached = true so no more logging occurs for the
            // day.
            isMaxFileSizeReached = true;
           
            try {
                FileWriter fw = new FileWriter(tempFile, true);
                sb = new StringBuilder("\nThe maximum file size of " + YukonFileAppender.getMaxFileSize() + " bytes has been reached, logging has been turned off for today.\n");
                fw.write(sb.toString());
                fw.close();
            } catch (IOException e) {
                errorHandler.error("Unable to write to log file--max file size exceeded for the day.");
            }
        }

        if (isMaxFileSizeReached == false) {
            //this method is inherited from WriterAppender, the actual appending of
            // the event occurs here.
            // Only allow appending if the max file size has not been reached
            subAppend(event);
        }    

    } // end append()

    /**
     * Formats a datestamp as dd using a Calendar source object.
     * @param calendar a Calendar containing the date to format.
     * @return a String in the form "dd".
     */
    public static String datestamp(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        StringBuffer sb = new StringBuffer();

        if (day < 10) {
            sb.append('0');
        }

        sb.append(Integer.toString(day));
        return sb.toString();
    }

    /**
     * Sets a calendar to the start of tomorrow, with all time values reset to
     * zero.
     * <p>
     * Takes advantage of the fact that the Java Calendar implementations are
     * mercifully accommodating in handling non-existent dates. For example,
     * June 31 is understood to mean July 1. This allows you to simply add one
     * to today's day of the month to generate tomorrow's date. It also works
     * for years, so that December 32, 2004 is converted into January 1, 2005.
     * </p>
     */
    public static void tomorrow(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH) + 1;
        calendar.clear(); // clear all fields
        calendar.set(year, month, day); // set tomorrow's date
    }

}
