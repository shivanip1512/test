package com.cannontech.export;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.KeysAndValues;
import com.cannontech.common.util.KeysAndValuesFile;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.pao.LMControlHistory;
import com.cannontech.export.record.DynamicLMProgramDirectRecord;
import com.cannontech.export.record.DynamicLMProgramRecord;
import com.cannontech.export.record.LMControlHistoryRecord;

/**
 * @author snebben
 *
 *         To change this generated comment edit the template variable "typecomment":
 *         Window>Preferences>Java>Templates.
 *         To enable and disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class LMControlHistoryFormat extends ExportFormatBase {
    private final String EXPORT_FILENAME = "lmctrlhist.csv";
    private final String LASTID_FILENAME = "\\LMCHID.DAT"; // LM Control History ID (last read).dat

    public LMControlHistoryFormat() {
        setExportFileName(EXPORT_FILENAME);
        setLastIDFileName(LASTID_FILENAME);
    }

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
                if (keys[i].equalsIgnoreCase("DIR")) {
                    setExportDirectory(values[i].toString());
                    File file = new File(getExportDirectory());
                    file.mkdirs();
                } else if (keys[i].equalsIgnoreCase("FILE")) {
                    setExportFileName(values[i].toString());
                } else if (keys[i].equalsIgnoreCase("INT")) {
                    // INT parameter is in MINUTES but we need millis
                    long minuteInterval = Long.valueOf(values[i].trim()).longValue();
                    long millisPerMinute = 60L * 1000L; // 60 seconds * 1000 millis
                    setRunTimeIntervalInMillis(minuteInterval * millisPerMinute);
                }
            }
        } else {
            // MODIFY THE LOG EVENT HERE!!!
            logEvent(
                "Usage:  format=<formatID> dir=<exportfileDirectory> file=<exportFileName> int=<RunTimeIntervalInMinutes>",
                com.cannontech.common.util.LogWriter.INFO);
            logEvent("Ex.	  format=3 dir=c:/yukon/client/export/ file=export.csv int=30",
                com.cannontech.common.util.LogWriter.INFO);
            logEvent("** All parameters will be defaulted to the above if not specified",
                com.cannontech.common.util.LogWriter.INFO);
        }

    }

    /**
     * @see com.cannontech.export.ExportFormatBase#retrieveExportData()
     */
    @Override
    public void retrieveData() {
        retrieveDynamicExportData();
        long timer = System.currentTimeMillis();

        StringBuffer sql =
            new StringBuffer("SELECT LMCTRLHISTID, PAOBJECTID, STARTDATETIME, SOE_TAG, CONTROLDURATION, CONTROLTYPE, ");
        sql.append(" CURRENTDAILYTIME, CURRENTMONTHLYTIME, CURRENTSEASONALTIME, CURRENTANNUALTIME, ACTIVERESTORE, REDUCTIONVALUE, STOPDATETIME ");
        sql.append(" FROM LMCONTROLHISTORY LMCH ");
        sql.append(" WHERE LMCTRLHISTID > " + getLastID());
        sql.append(" ORDER BY LMCTRLHISTID");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        logEvent("LMControlHistory Log for Max LMCTRLHISTID = " + getLastID(),
            com.cannontech.common.util.LogWriter.INFO);

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            if (conn == null) {
                logEvent(getClass() + ":  Error getting database connection.",
                    com.cannontech.common.util.LogWriter.ERROR);
                return;
            }
            pstmt = conn.prepareStatement(sql.toString());
            rset = pstmt.executeQuery();

            logEvent(" *Start looping through return resultset", com.cannontech.common.util.LogWriter.INFO);

            int lastLogID = -1;
            while (rset.next()) {
                int lmctrlhistid = rset.getInt(1);
                lastLogID = lmctrlhistid;
                int paobjectid = rset.getInt(2);
                Timestamp startdatetime = rset.getTimestamp(3);
                int soe_tag = rset.getInt(4);
                int controlduration = rset.getInt(5);
                String controltype = rset.getString(6);
                int currentdailytime = rset.getInt(7);
                int currentmonthlytime = rset.getInt(8);
                int currentseasonaltime = rset.getInt(9);
                int currentannualtime = rset.getInt(10);
                String activerestore = rset.getString(11);
                double reductionvalue = rset.getDouble(12);
                Timestamp stopdatetime = rset.getTimestamp(13);

                LMControlHistory lmctrlhist = new LMControlHistory(new Integer(lmctrlhistid));
                lmctrlhist.setPaObjectID(new Integer(paobjectid));
                lmctrlhist.setStartDateTime(new Date(startdatetime.getTime()));
                lmctrlhist.setSoeTag(new Integer(soe_tag));
                lmctrlhist.setControlDuration(new Integer(controlduration));
                lmctrlhist.setControlType(controltype);
                lmctrlhist.setCurrentDailyTime(new Integer(currentdailytime));
                lmctrlhist.setCurrentMonthlyTime(new Integer(currentmonthlytime));
                lmctrlhist.setCurrentSeasonalTime(new Integer(currentseasonaltime));
                lmctrlhist.setCurrentAnnualTime(new Integer(currentannualtime));
                lmctrlhist.setActiveRestore(activerestore);
                lmctrlhist.setReductionValue(new Double(reductionvalue));
                lmctrlhist.setStopDateTime(new Date(stopdatetime.getTime()));

                LMControlHistoryRecord lmctrlhistRecord = new LMControlHistoryRecord(lmctrlhist);

                getRecordVector().addElement(lmctrlhistRecord);
            }

            if (lastLogID > 0) {
                writeLastIDToFile(lastLogID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rset != null) {
                    rset.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();// sometin is up
            }
        }
        logEvent("@" + this.toString() + " Data Collection : Took " + (System.currentTimeMillis() - timer) + " millis",
            com.cannontech.common.util.LogWriter.INFO);
    }

    /**
     * @see com.cannontech.export.ExportFormatBase#buildKeysAndValues()
     */
    @Override
    public String[][] buildKeysAndValues() {
        String[] keys = new String[4];
        String[] values = new String[4];

        int i = 0;
        keys[i] = "FORMAT";
        values[i++] = String.valueOf(ExportFormatTypes.LMCTRLHIST_EXPORT_FORMAT);

        keys[i] = "DIR";
        values[i++] = getExportDirectory();

        keys[i] = "FILE";
        values[i++] = getExportFileName();

        long millisPerMinute = 60L * 1000L; // 60 seconds * 1000millis
        keys[i] = "INT";
        values[i++] = String.valueOf(getRunTimeIntervalInMillis() / millisPerMinute);

        return new String[][] { keys, values };
    }

    /**
     * @see com.cannontech.export.ExportFormatBase#retrieveExportData()
     */
    private void retrieveDynamicExportData() {
        long timer = System.currentTimeMillis();

        StringBuffer sql = new StringBuffer("SELECT DLMP.DEVICEID, PROGRAMSTATE, STARTTIME, STOPTIME ");
        sql.append(" FROM DYNAMICLMPROGRAM DLMP, DYNAMICLMPROGRAMDIRECT DLMPD ");
        sql.append(" WHERE DLMP.DEVICEID = DLMPD.DEVICEID ");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            if (conn == null) {
                logEvent(getClass() + ":  Error getting database connection.",
                    com.cannontech.common.util.LogWriter.ERROR);
                return;
            }
            pstmt = conn.prepareStatement(sql.toString());
            rset = pstmt.executeQuery();

            logEvent(" *Start looping through return resultset", com.cannontech.common.util.LogWriter.INFO);

            while (rset.next()) {
                int deviceid = rset.getInt(1);
                int programstate = rset.getInt(2);
                Timestamp starttime = rset.getTimestamp(3);
                Date start = new Date(starttime.getTime());
                Timestamp stoptime = rset.getTimestamp(4);
                Date stop = new Date(stoptime.getTime());

                DynamicLMProgramRecord dynamicLMPRecord = new DynamicLMProgramRecord(deviceid, programstate);

                getRecordVector().addElement(dynamicLMPRecord);

                DynamicLMProgramDirectRecord dynamicLMPDRecord =
                    new DynamicLMProgramDirectRecord(deviceid, start, stop);

                getRecordVector().addElement(dynamicLMPDRecord);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rset != null) {
                    rset.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e2) {
                e2.printStackTrace();// sometin is up
            }
        }
        logEvent("@" + this.toString() + " Data Collection : Took " + (System.currentTimeMillis() - timer) + " millis",
            com.cannontech.common.util.LogWriter.INFO);
    }

}
