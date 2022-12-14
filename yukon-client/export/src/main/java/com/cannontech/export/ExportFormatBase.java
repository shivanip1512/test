package com.cannontech.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.LogWriter;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.db.notification.NotificationGroup;
import com.cannontech.export.record.RecordBase;

/**
 * Base class for export file format types.
 * Creation date: (4/8/2002 11:11:48 AM)
 * 
 * @author:
 */
public abstract class ExportFormatBase {
    private ExportPropertiesBase exportProperties = null; // Export format properties
    private static LogWriter logger = null; // Log writer for export formats.
    private static Thread sleepThread = new Thread(); // sleeper thread for main do/while loop.
    private static boolean isService = true; // is Export format being ran as a service.
    private boolean isAppend = false;

    private Vector importStringVector = null; // contains the (String) elements read from the import
                                              // file.
    private String importDirectory = null; // directory for import file deployment.
    private String importFileName = "import.csv"; // fileName for importdata.

    private Vector recordVector = null; // contains the (String) elements to write to the export
                                        // file.
    private String exportDirectory = null; // directory for export file deployment.
    private String exportFileName = "export.csv"; // fileName for exportdata.
    private String lastIDFileName = "\\LASTID.dat"; // dat file for last id exported.

    public GregorianCalendar nextRunTime = null; // Next run time the application will create a
                                                 // file.
    private long runTimeIntervalInMillis = 86400000; // Interval between run times.

    private NotificationGroup emailGroup = null; // not implemented
                                                 // 2/20/03

    public static final String CONFIG_DIRECTORY = CtiUtilities.getConfigDirPath();

    /**
     * ExportFormatBase constructor comment.
     */
    public ExportFormatBase() {
        super();
    }

    /**
     * Return an instance of the ExportFormatBase based on formatID
     * set ExportProperties.formatId with param.formatID
     * 
     * @param formatID com.cannontech.export.ExportFormatType.FORMATTYPE
     * @return ExportFormatBase
     */
    public final static ExportFormatBase createFileFormat(int formatID) {
        ExportFormatBase returnFormat = null;

        if (formatID == ExportFormatTypes.DBPURGE_FORMAT) {
            returnFormat = new DBPurgeFormat();
        } else if (formatID == ExportFormatTypes.CSVBILLING_FORMAT) {
            returnFormat = new CSVBillingFormat();
        } else if (formatID == ExportFormatTypes.IONEVENTLOG_FORMAT) {
            returnFormat = new IONEventLogFormat();
        } else if (formatID == ExportFormatTypes.LMCTRLHIST_EXPORT_FORMAT) {
            returnFormat = new LMControlHistoryFormat();
        } else if (formatID == ExportFormatTypes.LMCTRLHIST_IMPORT_FORMAT) {
            returnFormat = new LMControlHistoryImportFormat();
        } else {
            CTILogger.info("Unrecognized file formatID " + formatID);
        }
        returnFormat.getExportProperties().setFormatID(formatID);
        return returnFormat;
    }

    /**
     * Compute nextRunTime.
     * Using nextRunTime = nextRunTime + runTimeIntervalInMillis.
     * Method figureNextRunTime.
     */
    private void figureNextRunTime() {
        if (nextRunTime == null) {
            nextRunTime = new GregorianCalendar();
            // First time this will run is at 1:00 AM. Taking a chance that the program
            // is not getting started up around this time.
            nextRunTime.set(java.util.GregorianCalendar.HOUR_OF_DAY, getExportProperties().getRunTimeHour().intValue());
            nextRunTime.set(java.util.GregorianCalendar.MINUTE, 0);
            nextRunTime.set(java.util.GregorianCalendar.SECOND, 0);
        } else {
            long lastRunTime = getNextRunTime().getTime().getTime();
            nextRunTime.setTime(new Date(lastRunTime + getRunTimeIntervalInMillis()));
        }
        logEvent("Next RunTime will occur: " + nextRunTime.getTime(), com.cannontech.common.util.LogWriter.INFO);
    }

    /**
     * Return directory.
     * Set directory according to ResourceBundle "export_file_directory" when null
     * or default to CtiUtilities.getExportDirPath() when bundle not found.
     * Method getDirectory.
     * 
     * @return String
     */
    public String getExportDirectory() {
        if (exportDirectory == null) {
            exportDirectory = CtiUtilities.getExportDirPath();

            logEvent("Export File Directory : " + exportDirectory, com.cannontech.common.util.LogWriter.INFO);

            File file = new File(exportDirectory);
            file.mkdirs();
        }
        return exportDirectory;
    }

    /**
     * Return emailGroup.
     * 
     * @return NotificationGroup
     */
    private NotificationGroup getEmailGroup() {
        return emailGroup;
    }

    /**
     * Return fileName to write exportFormat to.
     * 
     * @return String
     */
    public String getExportFileName() {
        return exportFileName;
    }

    /**
     * Return fileName to write the lastID exported to.
     * 
     * @return String
     */
    public String getLastIDFileName() {
        return lastIDFileName;
    }

    /**
     * Return ExportFormatTypes.formatDatFileName according to exportProperites.getFormatID().
     * 
     * @return String
     */
    public String getDatFileName() {
        return ExportFormatTypes.getFormatDatFileName(getExportProperties().getFormatID());
    }

    /**
     * Return nextRunTime
     * Next run time the application will create a file.
     * set nextRunTime by figureNextRunTime() when null.
     * 
     * @return GregorianCalendar
     */
    public GregorianCalendar getNextRunTime() {
        if (nextRunTime == null) {
            figureNextRunTime();
        }
        return nextRunTime;
    }

    /**
     * Return recordVector which contains the (String) elements to write to the export file.
     * Creates new Vector when null.
     * 
     * @return Vector
     */
    public Vector getRecordVector() {
        if (recordVector == null) {
            recordVector = new Vector();
        }

        return recordVector;
    }

    /**
     * Return VersionTools.getYUKON_VERSION().
     * 
     * @return String
     */
    public String getVersion() {
        return VersionTools.getYUKON_VERSION();
    }

    /**
     * Return isService.
     * True when Export format being ran as a service.
     * 
     * @return boolean
     */
    private boolean isService() {
        return isService;
    }

    /**
     * Create new LogWriter if logger null.
     * Write event to log file and CTILogger, describing event with severity.
     * 
     * @param event
     * @param severity
     */
    public void logEvent(String event, int severity) {
        if (logger == null) {
            try {
                String dataDir = "../log/";
                File file = new File(dataDir);
                file.mkdirs();

                GregorianCalendar cal = new GregorianCalendar();
                String filename = dataDir + this.toString() + ".log";
                FileOutputStream out = new FileOutputStream(filename, true);
                PrintWriter writer = new PrintWriter(out, true);
                logger = new LogWriter(this.toString(), com.cannontech.common.util.LogWriter.DEBUG, writer);

                logger.log("Starting up " + this.toString(), com.cannontech.common.util.LogWriter.INFO);
                logger.log("Version: " + VersionTools.getYUKON_VERSION() + getVersion() + ".",
                    com.cannontech.common.util.LogWriter.INFO);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (severity == com.cannontech.common.util.LogWriter.INFO) {
            CTILogger.info(event);
        } else if (severity == com.cannontech.common.util.LogWriter.ERROR) {
            CTILogger.error(event);
        }

        logger.log(event, severity);
    }

    /**
     * args must contain FORMAT=ExportFormatType.FORMATID to run successfully.
     * parseDatFile based on formatID.
     * Runs once if isService=false, else runs while isService=true.
     * 
     * @param args
     */
    public static void main(String[] args) {
        ExportFormatBase formatBase = null;
        int format = -1;

        for (int i = 0; i < args.length; i++) {
            String argLowerCase = args[i].toLowerCase();

            if (argLowerCase.startsWith("format")) {
                int startIndex = argLowerCase.indexOf("=") + 1;
                String subString = argLowerCase.substring(startIndex);
                format = Integer.valueOf(subString).intValue();
            }
        }
        if (format < 0) {
            formatBase.logEvent("** Missing FORMAT=<format type> from commandline.",
                com.cannontech.common.util.LogWriter.INFO);
            formatBase.logEvent("** No File Format Specified, Exporting process will exit...",
                com.cannontech.common.util.LogWriter.INFO);
            System.exit(0);
        } else {
            formatBase = createFileFormat(format);
            formatBase.parseDatFile();
        }

        try {
            formatBase.sleepThread.sleep(1000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        do {
            Date now = new Date();

            if (formatBase.getNextRunTime().getTime().compareTo(now) <= 0) {
                if (formatBase.getExportProperties().getFormatID() == ExportFormatTypes.LMCTRLHIST_IMPORT_FORMAT) {
                    formatBase.retrieveData();
                    formatBase.updateDatabase();
                } else {
                    formatBase.retrieveData();
                    formatBase.writeToFile();
                }
                formatBase.figureNextRunTime();

                // com.cannontech.message.dispatch.message.EmailMsg emailMsg = new
                // com.cannontech.message.dispatch.message.EmailMsg();
                // emailMsg.setSender("snebben@cannontech.com");
            } else {
                try {
                    Thread.sleep(1000); // 1 second
                    System.gc();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                    return;
                }

            }
        } while (formatBase.isService());

        formatBase.logEvent("..Finished " + formatBase.getClass().getName() + " File Export.",
            com.cannontech.common.util.LogWriter.INFO);
        logger.getPrintWriter().close();
        logger = null;

        System.exit(0);
    }

    /**
     * Read dat file keys and values to populate exportProperties and/or local properties.
     */
    public abstract void parseDatFile();

    public void updateDatabase() {
        CTILogger.info("updateDatabase() - Not implemented by all extendors yet!");
    }

    /**
     * Query the database for export Data.
     * Populate recordVector for each valid record created.
     */
    abstract public void retrieveData();

    /**
     * Main method for use with a GUI implementation (such as com.cannontech.export.gui.ExportGUI.
     * Retrieves the exportData, writes it to the export file and exits.
     * 
     * @param formatBase
     */
    public static void runMainWithGui(ExportFormatBase formatBase) {
        if (formatBase.getExportProperties().getFormatID() < 0) {
            formatBase.logEvent("** Missing format=<format type> from commandline.",
                com.cannontech.common.util.LogWriter.INFO);
            formatBase.logEvent("** No File Format Specified, Exporting process will exit...",
                com.cannontech.common.util.LogWriter.INFO);
            System.exit(0);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        if (formatBase.getExportProperties().getFormatID() == ExportFormatTypes.LMCTRLHIST_IMPORT_FORMAT) {
            formatBase.retrieveData();
            formatBase.updateDatabase();
        } else {
            formatBase.retrieveData();
            formatBase.writeToFile();
        }
        formatBase.figureNextRunTime();

        System.gc();

        formatBase.logEvent("Finished " + formatBase.getClass().getName() + " File Export.",
            com.cannontech.common.util.LogWriter.INFO);
        logger.getPrintWriter().close();
        logger = null;
    }

    /**
     * Method setExportProperties.
     * 
     * @param exportProps com.cannontech.export.ExportPropertiesBase
     */
    public void setExportProperties(ExportPropertiesBase exportProps) {
        exportProperties = exportProps;
    }

    /**
     * Return exportProperties.
     * Creates new ExportPropertiesBase when null.
     * 
     * @return ExportPropertiesBase
     */
    public ExportPropertiesBase getExportProperties() {
        if (exportProperties == null) {
            exportProperties = new ExportPropertiesBase();
        }
        return exportProperties;
    }

    /**
     * Set directory, create directory if it does not already exist.
     * 
     * @param newDirectory java.lang.String
     */
    public void setExportDirectory(String newExpDir) {
        exportDirectory = newExpDir;
        File file = new File(exportDirectory);
        file.mkdirs();

        logEvent("Files exported to directory: " + exportDirectory, com.cannontech.common.util.LogWriter.INFO);
    }

    /**
     * Method setEmailGroup.
     * 
     * @param newEmailGroup com.cannontech.database.db.notification.NotificationGroup
     */
    private void setEmailGroup(NotificationGroup newEmailGroup) {
        emailGroup = newEmailGroup;
    }

    /**
     * Method setIsService.
     * True when running as service or console application.
     * 
     * @param value boolean
     */
    public void setIsService(boolean value) {
        isService = value;
    }

    /**
     * Write recordVector to getDirector()+getFileName().
     * set recordVector = null when write to file is completed.
     * Method writeToFile.
     */
    public void writeToFile() {
        try {
            FileWriter outputFileWriter = new FileWriter(getExportDirectory() + getExportFileName(), isAppend());
            outputFileWriter.write(getOutputAsStringBuffer().toString());
            outputFileWriter.flush();
            outputFileWriter.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (getRecordVector().size() <= 0) {
                logEvent("...Exported * 0 * Records.  No file generated.", com.cannontech.common.util.LogWriter.INFO);
            } else {
                // DISCARD ALL RECORD DATA SO WE CAN START OVER AGAIN.
                logEvent("...Exported * " + recordVector.size() + " * Records to file " + getExportDirectory()
                    + getExportFileName(), com.cannontech.common.util.LogWriter.INFO);
                recordVector = null;
            }
        }
    }

    /**
     * Write the greatest logid to file LASTLOGID_FILENAME
     * 
     * @param maxLogID
     */
    public void writeLastIDToFile(int maxID) {
        try {
            File file = new File(getCONFIG_DIRECTORY());
            file.mkdirs();

            FileWriter writer = new FileWriter(file.getPath() + getLastIDFileName());

            // write the max log id recorded
            writer.write(String.valueOf(maxID) + "\r\n");

            writer.close();
        } catch (IOException e) {
            System.out.print(" IOException in writeLastLogIDToFile()");
            e.printStackTrace();
        }
    }

    /**
     * Return the lastLogID used for generating the export format.
     * Reads the logId from LASTLOGID_FILENAME.
     * 
     * @return int
     */
    public int getLastID() {
        int lastLogID = -1;
        RandomAccessFile raFile = null;
        File inFile = new File(getCONFIG_DIRECTORY() + getLastIDFileName());
        try {
            // open file
            if (inFile.exists()) {
                raFile = new RandomAccessFile(inFile, "r");

                // Believe we should only have one line to read!
                String line = raFile.readLine(); // read a line in
                lastLogID = Integer.valueOf(line).intValue();

                // Close file
                raFile.close();
            }
        } catch (IOException ex) {
            System.out.print("IOException in getLastID()");
            ex.printStackTrace();
        } finally {
            try {
                // Try to close the file, again.
                if (inFile.exists()) {
                    raFile.close();
                }
            } catch (IOException ex) {}
        }
        return lastLogID;
    }

    /**
     * Method buildKeysAndValues.
     * 
     * @return String[][]
     */
    public abstract String[][] buildKeysAndValues();

    /**
     * Write Export format's properites to dat file for later reference.
     * Used mainly for service installments.
     * File in 'KEY=VALUE' format
     */
    public void writeDatFile() {
        try {
            File file = new File(CtiUtilities.getConfigDirPath());
            file.mkdirs();

            FileWriter writer = new FileWriter(file.getCanonicalFile() + getDatFileName());
            String[][] keysAndValues = buildKeysAndValues();
            String keys[] = keysAndValues[0];
            String values[] = keysAndValues[1];

            String endline = "\r\n";

            for (int i = 0; i < keys.length; i++) {
                writer.write(keys[i] + "=" + values[i] + endline);
            }
            writer.close();
        } catch (IOException e) {
            System.out.print(" IOException in writeDatFile");
            e.printStackTrace();
        }
    }

    /**
     * Return getRunTimeIntervalInMillis.
     * Interval in which the application will continually run at.
     * 
     * @return long
     */
    public long getRunTimeIntervalInMillis() {
        return runTimeIntervalInMillis;
    }

    /**
     * Set runTimeIntervalInMillis, the millisecond interval between run times.
     * 
     * @param millis long
     */
    public void setRunTimeIntervalInMillis(long millis) {
        this.runTimeIntervalInMillis = millis;
    }

    /**
     * Return ExoprtFormatTypes.getFormatTypeName(formatID) as the string value.
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ExportFormatTypes.getFormatTypeName(getExportProperties().getFormatID());
    }

    private static String getCONFIG_DIRECTORY() {
        return CONFIG_DIRECTORY;
    }

    /**
     * Sets the exportFileName.
     * 
     * @param exportFileName The exportFileName to set
     */
    public void setExportFileName(String exportFileName) {
        this.exportFileName = exportFileName;
    }

    /**
     * Sets the lastIDFileName.
     * 
     * @param lastIDFileName The lastIDFileName to set
     */
    public void setLastIDFileName(String lastIDFileName) {
        this.lastIDFileName = lastIDFileName;
    }

    /**
     * Returns the importRecordVector.
     * 
     * @return java.util.Vector
     */
    public Vector getImportStringVector() {
        if (importStringVector == null) {
            importStringVector = new Vector();
        }
        return importStringVector;
    }

    /**
     * Sets the importRecordVector.
     * 
     * @param importRecordVector The importRecordVector to set
     */
    public void setImportStringVector(Vector importStringVector) {
        this.importStringVector = importStringVector;
    }

    /**
     * Returns the record vector as a string buffer formatted
     * by each RecordBase dataToString() format.
     * Creation date: (11/29/00)
     * 
     * @return java.lang.StringBuffer
     */
    public StringBuffer getOutputAsStringBuffer() {
        StringBuffer returnBuffer = new StringBuffer();
        Vector records = getRecordVector();

        for (int i = 0; i < records.size(); i++) {
            String dataString = ((RecordBase) records.get(i)).dataToString();
            if (dataString != null) {
                returnBuffer.append(dataString);
            }
        }

        return returnBuffer;
    }

    /**
     * Returns the importDirectory.
     * 
     * @return String
     */
    public String getImportDirectory() {
        if (importDirectory == null) {
            importDirectory = getExportDirectory();// com.cannontech.common.util.CtiUtilities.getExportDirPath();
        }
        return importDirectory;
    }

    /**
     * Returns the importFileName.
     * 
     * @return String
     */
    public String getImportFileName() {
        return importFileName;
    }

    /**
     * Sets the importDirectory.
     * 
     * @param importDirectory The importDirectory to set
     */
    public void setImportDirectory(String importDirectory) {
        this.importDirectory = importDirectory;
    }

    /**
     * Sets the importFileName.
     * 
     * @param importFileName The importFileName to set
     */
    public void setImportFileName(String importFileName) {
        this.importFileName = importFileName;
    }

    /**
     * Returns the isAppend.
     * 
     * @return boolean
     */
    public boolean isAppend() {
        return isAppend;
    }

    /**
     * Sets the isAppend.
     * 
     * @param isAppend The isAppend to set
     */
    public void setIsAppend(boolean isAppend) {
        this.isAppend = isAppend;
    }
}
