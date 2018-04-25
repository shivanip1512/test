package com.cannontech.clientutils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.Deflater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.FileUtil;

/**
 * YukonRollingFileAppender is a custom log4j2 appender that locates
 * where the current system should log messages. YukonRollingFileAppender
 * handles actual appending and roll over (time and size based).
 * YukonRollingFileAppender does set the pattern required for rolling of 
 * log file and determines the log directory, application that is running 
 * this appender. This also clean log files that are past the retention
 * date in the log directory
 */

@Plugin(name = "YukonRollingFile", category = "Core", elementType = "appender", printObject = true)
public class YukonRollingFileAppender extends AbstractOutputStreamAppender<RollingFileManager> {

    private static volatile YukonRollingFileAppender instance;

    public final static int LOG_RETENTION_DAYS = 90;

    protected final static String filenameDateFormat = "yyyyMMdd";

    private final static String REMOTE_LOGGING_DIRECTORY = CtiUtilities.getConfigDirPath() + "Yukon/Log/";
    /**
     * tells whether the maxFileSize has been reached
     */
    private static boolean isMaxFileSizeReached = false;

    /**
     * The directory in which log files are created.
     */
    private String directory = null;

    /**
     * The prefix that is added to log file filenames.
     */
    private String prefix = null;

    /**
     * The suffix that is added to log file filenames.Default is .log.
     */
    private String suffix = ".log";

    /**
     * A calendar object for manipulating dates and times.
     */
    private Calendar calendar = null;

    /**
     * The number of milliseconds since 1/1/1970 when tomorrow starts (local
     * time).
     */
    private long tomorrow = 0L;

    /**
     * String describing the process being logged and the current system configuration
     */
    private String systemInfoString = "";

    /**
     * The maximum log file size. If this is exceeded, logging will stop for the
     * rest of the day. Default is 1G.
     */
    private long maxFileSize = 1073741824L;

    /**
     * String representation of the date and time this log was started.
     */
    private String startDate = "";

    /**
     * logRetentionDays is used to delete archive files.
     */
    private int logRetentionDays = LOG_RETENTION_DAYS;

    /**
     * Name of the current logging file.
     */
    private String fileName;

    public YukonRollingFileAppender(final String name, final Filter filter, final Layout<? extends Serializable> layout,
            String fileName, String pattern, TriggeringPolicy policy, RolloverStrategy strategy, String applicationName,
            String directory, RollingFileManager manager) {
        super(name, layout, filter, true, true, manager);
        this.fileName = fileName;
        this.directory = directory;
        systemInfoString = CtiUtilities.getSystemInfoString();
        calendar = Calendar.getInstance();
        prefix = applicationName + "_";
        setStartDate();

    }

    @PluginFactory
    public static YukonRollingFileAppender createAppender(@PluginAttribute("name") String name,
            @PluginAttribute("pattern") String pattern,
            @PluginElement("Layout") Layout<? extends Serializable> layout, 
            @PluginElement("Filters") Filter filter,
            @PluginElement("Policy") TriggeringPolicy policy,
            @PluginElement("Strategy") RolloverStrategy strategy) {

        String applicationName = BootstrapUtils.getApplicationName();
        String directory = null;
        if (CtiUtilities.isRunningAsClient()) {
            directory = REMOTE_LOGGING_DIRECTORY;
        } else {
            directory = BootstrapUtils.getServerLogDir();
        }
        String fileName = directory + applicationName + ".log";

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        if (pattern == null) {
            pattern = directory + applicationName + "_" + "%d{" + filenameDateFormat + "}.log";
        }

        if (strategy == null) {
            DefaultRolloverStrategy.newBuilder().withCompressionLevelStr(String.valueOf(Deflater.DEFAULT_COMPRESSION))
                                                .withConfig(((Logger) LogManager.getLogger(
                                                            YukonRollingFileAppender.class)).getContext().getConfiguration()).build();
        }
        final RollingFileManager manager = RollingFileManager.getFileManager(fileName, pattern, true, false, policy,
            strategy, new File(fileName).toURI().toString(), layout, 8192, false, true, "wr", null, null,
            ((Logger) LogManager.getLogger(YukonRollingFileAppender.class)).getContext().getConfiguration());

        instance = new YukonRollingFileAppender(name, filter, layout, fileName, pattern, policy, strategy, applicationName, directory,manager);
        
        if (manager != null) {
            manager.initialize();
        }
        
        return instance;

    }

    @Override
    public void append(final LogEvent event) {
        synchronized (this) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= tomorrow) {

                // If the current time is the next day, then create a new file for
                // tomorrow--always first deleting the file with the same name/day from last month
                // this condition is only met once per day

                tomorrow(calendar); // set the Calendar to the start of tomorrow
                isMaxFileSizeReached = false; // file is always empty at beginning of
                // new day, so reset to false

                // time in millis when tomorrow starts
                tomorrow = calendar.getTimeInMillis();
                this.getManager().checkRollover(event);
                // Do any necessary log cleanup in the directory.
                cleanUpOldLogFiles();

                // append a header including version info at the start of the new log file
                try (FileWriter fwriter = new FileWriter(fileName, true)) {
                    String header = "LOG CONTINUES (Running since " + startDate + ")\n" + systemInfoString + "\n";
                    fwriter.write(header);
                } catch (IOException e) {
                    LOGGER.error("Unable to write header to new log file.");
                }
            } // end outer if

            File tempFile = new File(fileName);

            if (tempFile.canRead() && (tempFile.length() > maxFileSize) && !isMaxFileSizeReached) {
                // if the max file size is reached, append an error message to the file
                // (and possibly give some kind of alarm later)
                // also set isMaxSizeReached = true so no more logging occurs for the
                // day.
                isMaxFileSizeReached = true;
                try (FileWriter fwriter = new FileWriter(tempFile, true)) {
                    String output = "The maximum file size of " + maxFileSize
                        + " bytes has been reached, logging has been turned off for today.\n";
                    fwriter.write("\n");
                    fwriter.write(output);
                } catch (IOException e) {
                    LOGGER.error("Unable to write to log file--max file size exceeded for the day.");
                }
            }
        }
        if (isMaxFileSizeReached == false) {
            super.append(event);
        }
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

    /**
     * Deletes log files that are past the retention date in the log directory.
     */
    private void cleanUpOldLogFiles() {
        if (logRetentionDays == 0) {
            // Keep files forever. No need to do cleanup.
            return;
        }

        File currentDirectory = new File(directory);
        File[] files = currentDirectory.listFiles(new LogFilesToDeleteFilter());

        for (File file : files) {
            file.delete();
        }
    }

    private void setStartDate() {
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        startDate = formatter.format(currentDate);
    }

    /**
     * Filter files based on the previous (Webserver_##) and new (Webserver_####) format. Will not include
     * files
     * which are newer than logRetentionDays.
     * 
     * Files which PASS this filter will be deleted.
     */
    private class LogFilesToDeleteFilter implements FileFilter {
        String regex = "^" + prefix + "([0-9]{2}|[0-9]{8})" + suffix;

        @Override
        public boolean accept(File file) {
            Calendar retentionDate = Calendar.getInstance();
            retentionDate.setTime(calendar.getTime());
            retentionDate.add(Calendar.DAY_OF_YEAR, -logRetentionDays);

            return file.getName().matches(regex) && getLogCreationDate(file).before(retentionDate.getTime());
        }
    }

    /**
     * Returns the log creation date. First tries to use date in log filename, if the format isn't correct
     * it will return the file creation date.
     */
    private Date getLogCreationDate(File file) {
        try { /* lets try to parse the filename for the date */
            DateFormat dateFormat = new SimpleDateFormat(filenameDateFormat);
            return dateFormat.parse(file.getName().replace(prefix, ""));
        } catch (ParseException e) {/* Can't use filename (could be an old format) */}

        try { /* Use the files creation date */
            return FileUtil.getCreationDate(file);
        } catch (IOException e) {
            return new Date(file.lastModified());
        }
    }
}
