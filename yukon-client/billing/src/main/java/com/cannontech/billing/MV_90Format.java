package com.cannontech.billing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.billing.record.MV_90Record;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.spring.YukonSpringHook;

public class MV_90Format extends FileFormatBase {
    private static final int validProfileDemandAccOffsets[] = { 101, 102, 103, 104 };

    /**
     * Retrieves values from the database and inserts them in a FileFormatBase object
     */
    @Override
    public boolean retrieveBillingData() {
        long timer = System.currentTimeMillis();

        String[] SELECT_COLUMNS =
            { SQLStringBuilder.DMG_METERNUMBER, SQLStringBuilder.DMG_DEVICEID, SQLStringBuilder.RPH_TIMESTAMP,
                SQLStringBuilder.PT_POINTID, SQLStringBuilder.PT_POINTOFFSET, SQLStringBuilder.RPH_VALUE };

        String[] FROM_TABLES = { RawPointHistory.TABLE_NAME, Point.TABLE_NAME, DeviceMeterGroup.TABLE_NAME };

        SQLStringBuilder builder = new SQLStringBuilder();
        SqlStatementBuilder sql =
            builder.buildSQLStatement(SELECT_COLUMNS, FROM_TABLES, getBillingFileDefaults(), null, null,
                validProfileDemandAccOffsets, getBillingFileDefaults().getDemandStartDate());
        sql.append(" ORDER BY ");
        sql.append(SQLStringBuilder.DMG_METERNUMBER, ", ");
        sql.append(SQLStringBuilder.PT_POINTOFFSET, ", ");
        sql.append(SQLStringBuilder.RPH_TIMESTAMP);

        YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);

        jdbcTemplate.query(sql, new ResultSetExtractor<Object>() {
            @Override
            public Object extractData(ResultSet rset) throws SQLException, DataAccessException {

                CTILogger.info(" * Start looping through return resultset");

                int recCount = 0;
                Timestamp lastTimeStamp = new Timestamp(new Date().getTime() + 3600);
                Timestamp lpDemandRateTimeStamp = null;

                String lastMeterNumber = "";
                long lpDemandRate = 3600000;
                while (rset.next()) {
                    Timestamp ts = rset.getTimestamp(3);
                    Date tsDate = new Date(ts.getTime());

                    if (tsDate.compareTo(getBillingFileDefaults().getEndDate()) <= 0) {
                        int pointID = rset.getInt(4);
                        Vector<Double> readingVector = null;
                        double multiplier = 1;
                        if (getBillingFileDefaults().isRemoveMultiplier()) {
                            multiplier = getPointIDMultiplierHashTable().get(new Integer(pointID)).doubleValue();
                        }

                        String meterNumber = rset.getString(1);
                        int deviceID = rset.getInt(2);
                        // int ptOffset = rset.getInt(5);
                        double reading = rset.getDouble(6) / multiplier;

                        inValidTimestamp: if (lastTimeStamp.compareTo(ts) == 0) {
                            if (tsDate.compareTo(getBillingFileDefaults().getDemandStartDate()) <= 0) {
                                break inValidTimestamp;
                            }

                            // ** Get the last record and add to it the other pointOffsets' values. **//
                            MV_90Record lastRecord = (MV_90Record) getRecordVector().get(recCount - 1);

                            lastRecord.getReadingKWVector().add(new Double(reading));
                            lastRecord.setDateTimeKW(ts);
                        } else {
                            if (tsDate.compareTo(getBillingFileDefaults().getDemandStartDate()) <= 0) {
                                break inValidTimestamp;
                            }

                            MV_90Record mv90Rec = new MV_90Record(meterNumber);

                            // Check for a new meterNumber. MV_90 records are grouped by meterNumber rather
                            // than deviceID
                            if (!meterNumber.equalsIgnoreCase(lastMeterNumber)) {
                                mv90Rec.setNewMeterNumber(true);
                                lpDemandRate = retrieveLoadProfileDemandRate(deviceID);
                                lpDemandRateTimeStamp =
                                    new Timestamp(getBillingFileDefaults().getDemandStartDate().getTime());
                            }

                            // Increment the interval that rows MUST occur at.
                            lpDemandRateTimeStamp = new Timestamp(lpDemandRateTimeStamp.getTime() + lpDemandRate);
                            while (lpDemandRateTimeStamp.compareTo(ts) < 0) {
                                // If intervalTimeStamp is less than ts, we must enter 'dummy' data into
                                // recordVector.
                                if (lpDemandRateTimeStamp.compareTo(new Timestamp(
                                    getBillingFileDefaults().getEndDate().getTime())) > 0) {
                                    break inValidTimestamp;
                                }

                                readingVector = new Vector<Double>(4); // best guess capacity is 4
                                readingVector.add(new Double(-99999D));

                                mv90Rec.setReadingKWVector(readingVector);
                                mv90Rec.setDateTimeKW(lpDemandRateTimeStamp);
                                getRecordVector().addElement(mv90Rec);
                                recCount++;

                                lpDemandRateTimeStamp = new Timestamp(lpDemandRateTimeStamp.getTime() + lpDemandRate);
                                mv90Rec = new MV_90Record(meterNumber);
                            }

                            readingVector = new Vector<Double>(4); // best guess capacity is 4
                            readingVector.add(new Double(reading));
                            mv90Rec.setReadingKWVector(readingVector);

                            mv90Rec.setDateTimeKW(ts);

                            lastMeterNumber = meterNumber;
                            getRecordVector().addElement(mv90Rec);
                            recCount++;
                        }
                        lastTimeStamp = ts;
                    }
                }
                return null;
            }
        });
        CTILogger.info("@" + this.toString() + " Data Collection : Took " + (System.currentTimeMillis() - timer));
        return true;
    }

    private int retrieveLoadProfileDemandRate(int deviceID) {
        // long timer = System.currentTimeMillis();
        int demandRate = 3600000; // defaults to 3600000 seconds (1 hour)

        StringBuffer sql =
            new StringBuffer("SELECT LOADPROFILEDEMANDRATE FROM YUKONPAOBJECT PAO, DEVICELOADPROFILE DLP WHERE ");
        sql.append(" PAO.PAOBJECTID = " + deviceID);
        sql.append(" AND PAO.PAOBJECTID = DLP.DEVICEID");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            if (conn == null) {
                CTILogger.info(getClass() + ":  Error getting database connection.");
                return demandRate;
            }
            stmt = conn.prepareStatement(sql.toString());
            rset = stmt.executeQuery();

            while (rset.next()) {
                if (rset.getInt(1) > 0) {
                    demandRate = rset.getInt(1) * 1000;
                }
            }
        } catch (SQLException e) {
            CTILogger.error(e);
        } finally {
            SqlUtils.close(rset, stmt, conn);
        }
        return demandRate;
    }
}
