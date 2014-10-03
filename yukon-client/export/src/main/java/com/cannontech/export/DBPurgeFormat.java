package com.cannontech.export;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.KeysAndValues;
import com.cannontech.common.util.KeysAndValuesFile;
import com.cannontech.database.PoolManager;
import com.cannontech.export.record.DBPurgeRecord;

public class DBPurgeFormat extends ExportFormatBase {
    private String filePrefix = "lg";
    private String fileExtension = ".cvt";

    private GregorianCalendar maxDateToPurge = null;

    private static SimpleDateFormat FILENAME_FORMAT = new SimpleDateFormat("yyMMdd");
    private static SimpleDateFormat LOGTEXT_DATE_FORMAT = new SimpleDateFormat(" M/d/yy hh:mm:ss a ");

    /**
     * @see com.cannontech.export.ExportFormatBase#parseDatFile()
     */
    @Override
    public void parseDatFile() {
        KeysAndValuesFile kavFile = new KeysAndValuesFile(CtiUtilities.getConfigDirPath() + getDatFileName());

        KeysAndValues keysAndValues = kavFile.getKeysAndValues();

        if (keysAndValues != null) {
            String keys[] = keysAndValues.getKeys();
            String values[] = keysAndValues.getValues();
            for (int i = 0; i < keys.length; i++) {
                if (keys[i].equalsIgnoreCase("DAYS")) {
                    getExportProperties().setDaysToRetain(Integer.parseInt(values[i]));
                } else if (keys[i].equalsIgnoreCase("DIR")) {
                    setExportDirectory(values[i].toString());
                    File file = new File(getExportDirectory());
                    file.mkdirs();
                } else if (keys[i].equalsIgnoreCase("HOUR")) {
                    getExportProperties().setRunTimeHour(Integer.parseInt(values[i]));
                } else if (keys[i].equalsIgnoreCase("PURGE")) {
                    getExportProperties().setPurgeData(Boolean.valueOf(values[i]).booleanValue());
                }
            }
        } else {
            // MODIFY THE LOG EVENT HERE!!!
            logEvent("Usage:  format=<formatID> dir=<exportfileDirectory> hour=<runTimeHour(0-23)> purge=<boolean>",
                com.cannontech.common.util.LogWriter.INFO);
            logEvent("Ex.	  format=1 dir=c:/yukon/client/export/ hour=1 purge=false",
                com.cannontech.common.util.LogWriter.INFO);
            logEvent("** All parameters will be defaulted to the above if not specified",
                com.cannontech.common.util.LogWriter.INFO);
        }

    }

    /**
     * @see com.cannontech.export.ExportFormatBase#buildKeysAndValues()
     */
    @Override
    public String[][] buildKeysAndValues() {
        String[] keys = new String[5];
        String[] values = new String[5];

        int i = 0;
        keys[i] = "FORMAT";
        values[i++] = String.valueOf(ExportFormatTypes.DBPURGE_FORMAT);

        keys[i] = "DIR";
        values[i++] = getExportDirectory();

        keys[i] = "DAYS";
        values[i++] = String.valueOf(getExportProperties().getDaysToRetain());

        keys[i] = "HOUR";
        values[i++] = String.valueOf(getExportProperties().getRunTimeHour().intValue());

        keys[i] = "PURGE";
        values[i++] = String.valueOf(getExportProperties().isPurgeData());

        return new String[][] { keys, values };
    }

    /**
     * Return getMaxDateToPurge.
     * Maximum date of data to be purged or exported to file.
     * set maxDateToPurge by setMaxDateToPurge() when null.
     * 
     * @return GregorianCalendar
     */
    private GregorianCalendar getMaxDateToPurge() {
        if (maxDateToPurge == null) {
            retrieveMaxDateToPurge();
        }
        return maxDateToPurge;
    }

    /**
     * Delete Yukon.SystemLog for all values where dateTime less than maxDateToPurgea.
     */
    private void purgeSystemLog() {
        long timer = System.currentTimeMillis();

        String deleteString = "DELETE FROM SYSTEMLOG WHERE DATETIME < ?";

        logEvent("...Purging database for Max Date = " + getMaxDateToPurge().getTime(),
            com.cannontech.common.util.LogWriter.INFO);
        PreparedStatement pstmt = null;
        Connection conn = null;

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            pstmt = conn.prepareStatement(deleteString);
            pstmt.setTimestamp(1, new Timestamp(getMaxDateToPurge().getTime().getTime()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        logEvent("...PURGING SYSTEMLOG DATA: Took " + (System.currentTimeMillis() - timer) + " millis.",
            com.cannontech.common.util.LogWriter.INFO);
    }

    /**
     * @see com.cannontech.export.ExportFormatBase#retrieveExportData()
     */
    @Override
    public void retrieveData() {
        retrieveMaxDateToPurge();

        String name = new String();
        name += filePrefix;
        name += FILENAME_FORMAT.format(getMaxDateToPurge().getTime());
        name += fileExtension;
        setExportFileName(name);

        long timer = System.currentTimeMillis();

        String sqlString =
            "SELECT LOGID, SL.POINTID, DATETIME, TYPE, PRIORITY, ACTION, DESCRIPTION, P.POINTNAME, P.POINTTYPE "
                + "FROM SYSTEMLOG SL, POINT P WHERE DATETIME < ? " + "AND SL.POINTID = P.POINTID";

        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;

        logEvent("DBPurge for for Max Date = " + getMaxDateToPurge().getTime(),
            com.cannontech.common.util.LogWriter.INFO);
        try {

            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            pstmt = conn.prepareStatement(sqlString);

            pstmt.setTimestamp(1, new Timestamp(getMaxDateToPurge().getTime().getTime()));
            rset = pstmt.executeQuery();

            logEvent(" *Start looping through return resultset", com.cannontech.common.util.LogWriter.INFO);
            while (rset.next()) {
                Integer logid = new Integer(rset.getInt(1));
                Integer pointID = new Integer(rset.getInt(2));
                GregorianCalendar datetime = new GregorianCalendar();
                datetime.setTime(rset.getTimestamp(3));
                Integer priority = new Integer(rset.getString(5));
                String action = (rset.getString(6));
                String description = (rset.getString(7));
                String pointName = (rset.getString(8));
                String pointType = (rset.getString(9));

                DBPurgeRecord dbPurgeRec = new DBPurgeRecord();

                dbPurgeRec.setLogId(logid);
                dbPurgeRec.setDateTime(datetime);

                if (pointID.intValue() < 0) {
                    dbPurgeRec.setOriginator(pointName);
                } else {
                    dbPurgeRec.setOriginator(pointType + "Pt");
                }

                String logTypeText = null;
                if (priority.intValue() > 1) {
                    dbPurgeRec.setLogType("Alarm");
                    logTypeText = " A ";
                } else {
                    dbPurgeRec.setLogType("Log");
                    logTypeText = " L ";
                }

                dbPurgeRec.setSeverity(priority);

                String templogtext =
                    logTypeText + LOGTEXT_DATE_FORMAT.format(datetime.getTime()) + " " + dbPurgeRec.getOriginator();
                if (action != null) {
                    templogtext += " " + action;
                }
                if (description != null) {
                    String tempDesc = new String();
                    int indexBeginQuote = description.indexOf("\"");
                    int indexEndQuote = description.lastIndexOf("\"");
                    if (indexBeginQuote >= 0) {
                        tempDesc = description.substring(0, indexBeginQuote);
                        tempDesc += description.substring(indexBeginQuote + 1, indexEndQuote);
                        tempDesc += description.substring(indexEndQuote + 1);

                        description = tempDesc;
                    }
                    templogtext += " " + description;
                }
                dbPurgeRec.setLogText(templogtext);

                getRecordVector().add(dbPurgeRec);
            }

            if (getExportProperties().isPurgeData()) {
                purgeSystemLog();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rset != null) {
                    rset.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        logEvent("@" + this.toString() + " Data Collection : Took " + (System.currentTimeMillis() - timer) + " millis",
            com.cannontech.common.util.LogWriter.INFO);
    }

    /**
     * Set MaxDateToPurge subtracting daysToRetain from today's dae.
     */
    private void retrieveMaxDateToPurge() {
        int DAY = 86400;
        int MILLI_CONVERT = 1000;

        long daysInMillis = (new Integer(DAY * getExportProperties().getDaysToRetain())).longValue() * MILLI_CONVERT;
        GregorianCalendar today = new GregorianCalendar();
        today.set(java.util.GregorianCalendar.HOUR_OF_DAY, 0);
        today.set(java.util.GregorianCalendar.MINUTE, 0);
        today.set(java.util.GregorianCalendar.SECOND, 0);

        long newTime = (today.getTime().getTime() - daysInMillis);
        today.setTime(new Date(newTime));

        maxDateToPurge = today;
    }
}
