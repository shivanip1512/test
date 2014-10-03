package com.cannontech.billing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.billing.record.NCDC_Handheld;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author snebben
 *
 *         To change this generated comment edit the template variable "typecomment":
 *         Window>Preferences>Java>Templates.
 *         To enable and disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class NCDC_HandheldFormat extends FileFormatBase {
    private static final int validAccPtOffsets[] = { 1, 2, 3 };

    private class InputRecord {
        public String account = null; // 34-45
        public String meterNumber = null; // 46-55
        public String comment = null; // 139-140
        public String tou = null; // 203

        public InputRecord(String account, String meterNumber, String comment, String tou) {
            this.account = account;
            this.meterNumber = meterNumber;
            this.comment = comment;
            this.tou = tou;
        }
    }

    /**
     * @see com.cannontech.billing.FileFormatBase#retrieveBillingData(String)
     */
    @Override
    public boolean retrieveBillingData() {
        long timer = System.currentTimeMillis();

        final Hashtable<String, InputRecord> inputRecordsHashTable = retrieveInputFile();
        if (inputRecordsHashTable == null) {
            return false; // can't do anything without the input records
        }

        String[] SELECT_COLUMNS =
            { SQLStringBuilder.PT_POINTID, SQLStringBuilder.PT_POINTOFFSET, SQLStringBuilder.RPH_TIMESTAMP,
                SQLStringBuilder.RPH_VALUE, SQLStringBuilder.PAO_PAOBJECTID, SQLStringBuilder.PAO_PAONAME };

        String[] FROM_TABLES =
            { com.cannontech.database.db.pao.YukonPAObject.TABLE_NAME,
                com.cannontech.database.db.point.RawPointHistory.TABLE_NAME,
                com.cannontech.database.db.point.Point.TABLE_NAME, };

        SQLStringBuilder builder = new SQLStringBuilder();
        SqlStatementBuilder sql =
            builder.buildSQLStatement(SELECT_COLUMNS, FROM_TABLES, getBillingFileDefaults(), null, validAccPtOffsets,
                getBillingFileDefaults().getEarliestStartDate());
        sql.append(" ORDER BY ", SQLStringBuilder.PAO_PAOBJECTID, ", ");
        sql.append(SQLStringBuilder.PT_POINTOFFSET, ", ");
        sql.append(SQLStringBuilder.RPH_TIMESTAMP, " DESC ");

        YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);

        jdbcTemplate.query(sql, new ResultSetExtractor<Object>() {
            @Override
            public Object extractData(ResultSet rset) throws SQLException, DataAccessException {
                // PT_POINTID, PT_POINTOFFSET, RPH_TIMESTAMP, RPH_VALUE, PAO_PAOBJECTID, PAO_PAONAME
                int deviceID = -1;
                int lastDeviceID = -1;

                String loc = null; // paoname

                double value = 0;
                int pointID = 0;
                int ptOffset = 0;
                int lastPointID = 0;
                int recCount = 0;

                CTILogger.info(" Start looping through return resultset");

                while (rset.next()) {
                    java.sql.Timestamp ts = rset.getTimestamp(3);
                    java.util.Date tsDate = new java.util.Date(ts.getTime());
                    if (tsDate.compareTo(getBillingFileDefaults().getEndDate()) <= 0) // ts <= maxtime, pass!
                    {
                        pointID = rset.getInt(1);
                        double multiplier = 1;
                        if (getBillingFileDefaults().isRemoveMultiplier()) {
                            multiplier = getPointIDMultiplierHashTable().get(new Integer(pointID)).doubleValue();
                        }
                        if (pointID != lastPointID) // just getting max time for each point
                        {
                            lastPointID = pointID;
                            value = rset.getDouble(4) / multiplier;
                            deviceID = rset.getInt(5);
                            ptOffset = rset.getInt(2);
                            loc = rset.getString(6);

                            inValidTimestamp: if (deviceID == lastDeviceID) {
                                if (ptOffset == 1 || isKWH(ptOffset)) {
                                    if (tsDate.compareTo(getBillingFileDefaults().getEnergyStartDate()) <= 0) {
                                        break inValidTimestamp;
                                    }

                                    // ** Get the last record and add to it the other pointOffsets' values.
                                    // **//
                                    NCDC_Handheld lastRecord = (NCDC_Handheld) getRecordVector().get(recCount - 1);

                                    lastRecord.setKwhReading(new Double(value));
                                    lastRecord.setReadDate(ts);
                                    lastRecord.setReadTime(ts);
                                } else if (isKW(ptOffset)) {
                                    if (tsDate.compareTo(getBillingFileDefaults().getDemandStartDate()) <= 0) {
                                        break inValidTimestamp;
                                    }

                                    // ** Get the last record and add to it the other pointOffsets' values.
                                    // **//
                                    NCDC_Handheld lastRecord = (NCDC_Handheld) getRecordVector().get(recCount - 1);

                                    lastRecord.setKwReading(new Double(value));
                                    lastRecord.setReadDate(ts);
                                    lastRecord.setReadTime(ts);
                                }
                            } else {
                                NCDC_Handheld record = new NCDC_Handheld();

                                if (ptOffset == 1 || isKWH(ptOffset)) {
                                    if (tsDate.compareTo(getBillingFileDefaults().getEnergyStartDate()) <= 0) {
                                        break inValidTimestamp;
                                    }

                                    record.setKwhReading(new Double(value));
                                    record.setReadDate(ts);
                                    record.setReadTime(ts);

                                } else if (isKW(ptOffset)) {
                                    if (tsDate.compareTo(getBillingFileDefaults().getDemandStartDate()) <= 0) {
                                        break inValidTimestamp;
                                    }

                                    record.setKwReading(new Double(value));
                                    record.setReadDate(ts);
                                    record.setReadTime(ts);
                                }

                                // *****************************************************************************************
                                /*
                                 * Get the accountNumber from the hash table for the current meterNumber.
                                 * If the accountNumber is NOT found in the hash table then the paoName is
                                 * used as the accountNumber also
                                 */
                                InputRecord inRecord = null;
                                inRecord = inputRecordsHashTable.get(loc.trim());

                                if (inRecord == null) // we have a big problem! What is our deviceName?
                                {
                                    // I guess we have to skip this record and move on!
                                    CTILogger.info("SKIPPING DEVICE NAME " + loc
                                        + ".  DO NOT HAVE A REFERENCE TO THIS DEVICE IN THE INPUT FILE.");
                                } else {
                                    record.setLocation(loc.trim());
                                    record.setAcctNumber(inRecord.account);
                                    record.setMeterNumber(inRecord.meterNumber);
                                    record.setCommentCode(inRecord.comment);
                                    record.setTou(inRecord.tou);

                                    lastDeviceID = deviceID;
                                    getRecordVector().addElement(record);
                                    recCount++;
                                }
                            }
                        }
                    }
                }// end while
                return null;
            }
        });
        CTILogger.info("@" + this.toString() + " Data Collection : Took " + (System.currentTimeMillis() - timer));

        return true;
    }

    /**
     * Returns a hashtable of meternumber as key and account number as value.
     * Collects the meterNumber/Account number set from the database unless the file
     * meterAndAccountNumbers.txt exists containing this mapping.
     * 
     * @return java.util.Hashtable
     * @param dbAlias the database string name alias.
     */
    private Hashtable<String, InputRecord> retrieveInputFile() {
        Vector<String> linesInFile = new Vector<String>();
        Hashtable<String, InputRecord> inputRecordsHashTable = null;

        try {
            java.io.FileReader inputRecordsFileReader = new java.io.FileReader(billingFileDefaults.getInputFileDir());
            java.io.BufferedReader readBuffer = new java.io.BufferedReader(inputRecordsFileReader);

            try {
                String tempLineString = readBuffer.readLine();

                while (tempLineString != null) {
                    linesInFile.add(new String(tempLineString));
                    tempLineString = readBuffer.readLine();
                }
            } catch (java.io.IOException ioe) {
                CTILogger.error(ioe);
            }
        } catch (java.io.FileNotFoundException fnfe) {
            CTILogger.info("****************************************************************************");
            CTILogger.info("Cannot find " + getBillingFileDefaults().getInputFileDir()
                + ".  Please create file and regenerate file.");
            CTILogger.info("****************************************************************************");
            return null; // with null, we will have to exit!
        }

        if (linesInFile != null) {
            CTILogger.info("Successfully read " + linesInFile.size() + " lines from file "
                + getBillingFileDefaults().getInputFileDir() + ".");
            java.util.Collections.sort(linesInFile);
            int hashCapacity = (linesInFile.size() + 1);
            inputRecordsHashTable = new Hashtable<String, InputRecord>(hashCapacity);

            for (int i = 0; i < linesInFile.size(); i++) {
                String line = linesInFile.get(i);
                String keyLocation = line.substring(0, 25).trim();
                String valueLocation = keyLocation;
                String valueAccount = line.substring(34, 46).trim();
                String valueMeter = line.substring(46, 56).trim();
                String valueComment = line.substring(139, 141).trim();
                String valueTou = line.substring(203, 204).trim();

                InputRecord inRecord = new InputRecord(valueAccount, valueMeter, valueComment, valueTou);
                inputRecordsHashTable.put(keyLocation, inRecord);
            }
        }
        return inputRecordsHashTable;
    }

    /**
     * Returns true if offset is a valid kw offset.
     * 
     * @return boolean
     * @param offset int
     */
    private boolean isKW(int offset) {
        switch (offset) {
        case 2:
        case 4:
        case 6:
        case 8:
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns true if offset is a valid kwh offset.
     * 
     * @return boolean
     * @param offset int
     */
    private boolean isKWH(int offset) {
        switch (offset) {
        case 1:
        case 3:
        case 5:
        case 7:
        case 9:
            return true;
        default:
            return false;
        }
    }
}
