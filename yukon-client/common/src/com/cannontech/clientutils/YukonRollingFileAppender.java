package com.cannontech.clientutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
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
import org.joda.time.DateTime;

import com.cannontech.common.config.MasterConfigHelper;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.FileUtil;
import com.cannontech.tools.zip.ZipWriter;

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
    private boolean isMaxFileSizeReached = false;

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
    private long maxFileSize = 0;

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

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
        maxFileSize = BootstrapUtils.getLogMaxFileSize();

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
        if (BootstrapUtils.isWebStartClient() && !MasterConfigHelper.isLocalConfigAvailable()) {
            directory = REMOTE_LOGGING_DIRECTORY;
        } else {
            directory = BootstrapUtils.getServerLogDir();
        }
        String fileName = directory + applicationName + ".log";
        // Check and rename if current file already exist with old creation date. 
        checkForTimeBasedRollover(directory, applicationName);
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        if (pattern == null) {
            pattern = directory + applicationName + "_" + "%d{" + filenameDateFormat + "}.log.zip";
        }

        if (strategy == null) {
            strategy = DefaultRolloverStrategy.newBuilder().withCompressionLevelStr(String.valueOf(Deflater.DEFAULT_COMPRESSION))
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
                // Rename zipped file from .log.zip format to .zip format
                renameZippedFiles();
                // Fall back if renaming do not happen in initial attempt.
                scheduleZippedFilesRename();

                // append a header including version info at the start of the new log file
                try (FileWriter fwriter = new FileWriter(fileName, true)) {
                    // Add log Creation date on top of log file. 
                    String logCreationDate = "Log Creation Date : "+ new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    String header = logCreationDate + "\r\n" + "LOG CONTINUES (Running since " + BootstrapUtils.getServiceStartTime() + ")\r\n" + systemInfoString + "\r\n";
                    fwriter.write(header);
                } catch (IOException e) {
                    LOGGER.error("Unable to write header to new log file.");
                }
            } // end outer if

            File tempFile = new File(fileName);

            if (tempFile.length() < maxFileSize) {
                isMaxFileSizeReached = false;
            }

            if (tempFile.canRead() && (tempFile.length() > maxFileSize) && !isMaxFileSizeReached) {
                // if the max file size is reached, append an error message to the file
                // (and possibly give some kind of alarm later)
                // also set isMaxSizeReached = true so no more logging occurs for the
                // day.
                isMaxFileSizeReached = true;
                try (FileWriter fwriter = new FileWriter(tempFile, true)) {
                    String output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS").format(new Date()) + " The maximum file size of " + maxFileSize
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

    /**
     * Starts a daemon thread which try to rename .log.zip files to .zip format in the log directory after 5
     * minutes. This is fall back in case it fails in initial attempt. 
     * Its a demon thread so it do not blocks the applications which runs for few minutes.
     */
    private void scheduleZippedFilesRename() {
        Thread renameThread = new Thread("renameThread") {
            @Override
            public void run() {
                try {
                    // Sleep for 5 min
                    Thread.sleep(300000);
                    renameZippedFiles();
                } catch (InterruptedException e) {
                    return;
                }
            }
        };
        renameThread.setDaemon(true);
        renameThread.start();
    }

    /**
     * Rename .log.zip files to .zip format in the log directory.
     */
    private void renameZippedFiles() {
        File currentDirectory = new File(directory);
        File[] filesForRename = currentDirectory.listFiles(new LogFilesToRenameFilter());
        for (File file : filesForRename) {
            String[] output = file.getAbsolutePath().split("\\.");
            file.renameTo(new File(output[0] + ".zip"));
        }
    }

    /**
     * Filter files based on the previous (Webserver_##) and new (Webserver_####) format. Will not include
     * files
     * which are newer than logRetentionDays.
     * 
     * Files which PASS this filter will be deleted.
     */
    private class LogFilesToDeleteFilter implements FileFilter {
        String regex = "^" + prefix + "([0-9]{2}|[0-9]{8})";
        @Override
        public boolean accept(File file) {
            Calendar retentionDate = Calendar.getInstance();
            retentionDate.setTime(calendar.getTime());
            retentionDate.add(Calendar.DAY_OF_YEAR, -logRetentionDays);
            Date fileDate = null;
            if (FileUtil.isJavaLogFile(file.getName(), regex)) {
                fileDate = FileUtil.getLogCreationDate(file.getName(), prefix);
            } else if (FileUtil.isOldRfnCommLogFile(file.getName())) {
                // Check for old rfnCommLogs files which are currently renamed.
                fileDate = FileUtil.getLogCreationDate(file.getName(), "RfnCommsLog_");
            } else {
                try {
                    // Get the date of old client log file format like DBEditor[10.24.26.137]20180531.log.
                    fileDate = FileUtil.getOldClientLogDate(file.getName());
                } catch (ParseException e) {
                    // Don't do anything
                }
            }
            if (fileDate == null) {
                try {
                    /* Use the files creation date */
                    fileDate = FileUtil.getCreationDate(file);
                } catch (IOException e) {
                    fileDate = new Date(file.lastModified());
                }
            }
            return fileDate.before(retentionDate.getTime());
        }
    }

    /**
     * Filter files based on file name format i.e files in .log.zip format (Webserver_YYYYMMDD.log.zip)
     * 
     * Files which PASS this filter will be renamed.
     */
    private class LogFilesToRenameFilter implements FileFilter {
        String regex = "^" + prefix + "([0-9]{2}|[0-9]{8})" + suffix + ".zip";

        @Override
        public boolean accept(File file) {
            return (file.getName().matches(regex));
        }
    }

    /**
     * Check and roll current log file if creation date of log file is old.
     * <p>
     * This is used to identify actual log file creation date when sever is stopped for entire day or more.
     * Restarting server next day will require rolling and zipping of old log file based on creation date.
     * For Example : WebServer is running and stopped at 9PM on 1-1-2018 and current log file is Webserver.log.
     * Restarting WebServer next day will continue logging to Webserver.log. 
     * To prevent this logging on the same log file created on 1-1-2018, this method will first identify 
     * the actual creation date of Webserver.log (1-1-2018 for our case) and rename this file to Webserver_20180101.log 
     * and zip it to Webserver_20180101.log.zip format.
     * Current logging will continue for next day (2-1-2018) on Webserver.log.
     * </p>
     */
    protected static void checkForTimeBasedRollover(String directory, String applicationName) {
        File tmpDir = new File(directory + applicationName + ".log");
        DateTime fileDate = null;
        // Check if file is already existing
        if (tmpDir.exists()) {
            try (BufferedReader fileHeader = new BufferedReader(new FileReader(tmpDir))) {
                fileDate = FileUtil.parseLogCreationDate(fileHeader.readLine());
            } catch (Exception e) {
                LOGGER.error("Unable to read file header from log file.");
            }
            // Check if existing file is old (Try to get its creation date)
            if (fileDate != null &&  fileDate.isBefore(new DateTime().withTimeAtStartOfDay())) {
                String creationDate = new SimpleDateFormat(filenameDateFormat).format(fileDate.toDate());
                // Rename current file (append file creation date i.e. fileName_date.log)
                String datedFilePath = directory + applicationName + "_" + creationDate + ".log";
                File newDatedFile = new File(datedFilePath);
                if (tmpDir.renameTo(newDatedFile)) {
                    try {
                        File datedZipFile = new File(datedFilePath + ".zip");
                        ZipWriter zipWriter = new ZipWriter(datedZipFile);
                        FileInputStream fis = new FileInputStream(newDatedFile);
                        zipWriter.writeRawInputStream(fis, newDatedFile.getName());
                        zipWriter.close();
                        // Check if zip file is created
                        if (datedZipFile.exists()) {
                            // Delete .log file (fileName_YYYYMMDD.log)
                            newDatedFile.delete();
                        }
                    } catch (IOException e) {
                        LOGGER.error("Unable to zip log file.");
                    }
                } else {
                    LOGGER.error("Unable to rename existing log fileName.");
                }
            }
        }
    }
}
