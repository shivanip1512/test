package com.cannontech.clientutils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import com.cannontech.common.util.BootstrapUtils;
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
     * maximum file size as a string, defaults to 1 gigabyte
     * Accepts forms: "1G" or "1g", ".5M", etc.
     */
    private String maxFileSizeString;
    private String logRetentionDaysString;

    private int maxFileOpenRetries = DatedFileAppender.MAX_FILE_OPEN_RETRIES;
    private int retryDelayInMs = DatedFileAppender.RETRY_DELAY_IN_MS;
    private int logRetentionDays = DatedFileAppender.LOG_RETENTION_DAYS;

    /**
     *  This appenders name
     */
    private String name;

    /**
     * The conversion pattern sets the layout for the log4j logging
     * See the log4j docs for specifics on formatting characters. The
     * default string uses: [date and time, thread that generated log event,
     * the log level left justified, the logger name which is usually the fq-class name,
     * log message and newline]
     */
    private String conversionPattern = "%d [%t] %-5p %c - %m%n";

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
    }

    @Override
    public void activateOptions() {
        super.activateOptions();

        //initialize dailyRollingFileAppender
        //get the name of the application running this appender

        String nameOfApp = CtiUtilities.getApplicationName();
        String directory = BootstrapUtils.getServerLogDir();
        String fileName = directory + nameOfApp + ".log";

        //create a DatedFileAppender to take over the actual appending, rollover, and timing issues
        dailyRollingFileAppender = new DatedFileAppender(directory, nameOfApp + "_", ".log");
        dailyRollingFileAppender.setName("dailyRollingFileAppender");
        dailyRollingFileAppender.setFile(fileName);
        dailyRollingFileAppender.setSystemInfoString(CtiUtilities.getSystemInfoString());
        dailyRollingFileAppender.setMaxFileSize(maxFileSize);

        dailyRollingFileAppender.setMaxFileOpenRetries(maxFileOpenRetries);
        dailyRollingFileAppender.setRetryDelayInMillis(retryDelayInMs);
        dailyRollingFileAppender.setLogRetentionDays(logRetentionDays);

        //The layout for the log file:
        Layout layout = new PatternLayout(conversionPattern);
        dailyRollingFileAppender.setLayout(layout);

        //Inherited from AppenderSkeleton. Calls once options are set
        dailyRollingFileAppender.activateOptions();
    }

    /**
     * @Override append in AppenderSkeleton
     */
    @Override
    protected void append(LoggingEvent arg0) {
        dailyRollingFileAppender.append(arg0);
    }

    /**
     * @Override requiresLayout in AppenderSkeleton
     */
    @Override
    public boolean requiresLayout() {
        return dailyRollingFileAppender.requiresLayout();
    }

    /**
     * @Override close in AppenderSkeleton
     */
    @Override
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
     * @param maxFileSizeString the maximum file size entered
     * as: 1G or .5M or 1000K or 100000000B or just 1000000000
     * sets the maxFileSize variable to the size of file in bytes
     */
    public void setMaxFileSizeString(String maxFileSizeString){
        this.maxFileSizeString = maxFileSizeString;
        maxFileSize = parseMaxFileSizeString(maxFileSizeString);
    }

    public void setMaxFileOpenRetries(int maxFileOpenRetries) {
        this.maxFileOpenRetries = maxFileOpenRetries;
    }

    public void setRetryDelayInMs(int retryDelayInMs) {
        this.retryDelayInMs = retryDelayInMs;
    }

    public void setLogRetentionDaysString(String logRetentionDaysString) {
        this.logRetentionDaysString = logRetentionDaysString;
        logRetentionDays = Integer.parseInt(logRetentionDaysString);
    }

    public String getLogRetentionDaysString() {
        return logRetentionDaysString;
    }

    /**
     * @return the maximum file size as a string
     */
    public String getMaxFileSizeString(){
        return maxFileSizeString;
    }

    /**
     * @return the maximum file size as a long
     */
    public static long getMaxFileSize(){
        return maxFileSize;
    }

    /**
     * @return the maximum file size as a long
     */
    public static void setMaxFileSize(long maxFileSize){
        YukonFileAppender.maxFileSize = maxFileSize;
    }

    /**
     * Takes a string that represents the maximum files size to be used for logging
     * and parses it into a number value and units. Examples: 1G, .5M  1000k etc.
     * Defaults to a byte value if no units are given.
     * @return numberOfBytes as a long value. Represents the max file size for logging
     * @throws NumberFormatException if the string cannot be converted to a number
     */
    public long parseMaxFileSizeString(String maxFileSizeString) {
        long numberOfBytes = 0L;
        Double number = 1.0; //default is 1 gig
        Character units = 'G'; //dafault value is 1G

        //the units as either G, M, K, or B (gigabyte, megabyte, etc.)
        Map<Character, Long> unitMap = new HashMap<Character, Long>();
        unitMap.put(Character.valueOf('G'), Long.valueOf(1073741824));
        unitMap.put(Character.valueOf('M'), Long.valueOf(1048576));
        unitMap.put(Character.valueOf('K'), Long.valueOf(1024));
        unitMap.put(Character.valueOf('B'), Long.valueOf(1));

        Pattern p = Pattern.compile("^([\\d.]+)([A-Za-z]?)$");
        Matcher m = p.matcher(maxFileSizeString);
        if (m.matches()) {

            //extract number from string and handle NaN
            try {
                number = Double.parseDouble(m.group(1));
            } catch (NumberFormatException e){
                errorHandler.error("Incorrect NaN value for file size, using default 1G file size ");
                return numberOfBytes = unitMap.get('G');
            }

            //handle case with no units given--default to bytes
            if(m.group(2).length() > 0) {
                units = m.group(2).toUpperCase().charAt(0);
            }
            else {
                units = 'B'; //no units so default to bytes
            }

            //do the conversion of string to bytes
            if (unitMap.containsKey(units)) {
                // if units in map,
                double tempDouble = unitMap.get(units) * number;
                numberOfBytes = Math.round(tempDouble);
            } else {
                numberOfBytes = Math.round(number);
            }
        }
        //if failed to parse maxFileSizeString or file size is less than 1 byte
        //log an error and use the default 1G file size
        if(numberOfBytes <= 1) {
            errorHandler.error("Max log file string format was incorrect, use: .5G, 1000M, 30000K, or 25000000");
            numberOfBytes = unitMap.get('G');
        }

        return numberOfBytes;
    }

    /**
     * @return the conversion pattern for logging
     * default conversionPattern = "%d [%t] %-5p %c - %m%n"
     */
    public String getConversionPattern() {
        return conversionPattern;
    }

    /**
     * Set the log4j layout to something different than the default
     * default conversionPattern = "%d [%t] %-5p %c - %m%n"
     * see log4j docs for specifics on conversion characters.
     * @param conversionPattern the pattern for the layout of logging output
     */
    public void setConversionPattern(String conversionPattern) {
        this.conversionPattern = conversionPattern;
    }
}
