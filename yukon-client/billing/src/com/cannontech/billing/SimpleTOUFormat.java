package com.cannontech.billing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.cannontech.billing.record.SimpleTOURecord;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.RawPointHistory;

/**
 * Billing format for Simple TOU
 */
public class SimpleTOUFormat extends FileFormatBase {
    public SimpleTOUFormat() {
        super();
    }

    /**
     * Retrieves values from the database and inserts them in a FileFormatBase
     * object
     */
    public boolean retrieveBillingData(String dbAlias) {
        long timer = System.currentTimeMillis();

        if (dbAlias == null)
            dbAlias = CtiUtilities.getDatabaseAlias();

        String[] SELECT_COLUMNS = { SQLStringBuilder.DMG_METERNUMBER, SQLStringBuilder.PT_POINTID,
                SQLStringBuilder.RPH_TIMESTAMP, SQLStringBuilder.RPH_VALUE,
                SQLStringBuilder.DMG_DEVICEID, SQLStringBuilder.PT_POINTOFFSET };

        String[] FROM_TABLES = { RawPointHistory.TABLE_NAME, Point.TABLE_NAME,
                DeviceMeterGroup.TABLE_NAME };

        SQLStringBuilder builder = new SQLStringBuilder();
        String sql = new String((builder.buildSQLStatement(SELECT_COLUMNS,
                                                           FROM_TABLES,
                                                           getBillingDefaults(),
                                                           null,
                                                           validTOUAccPtOffsets,
                                                           validTOUPeakDemandAccOffsets)).toString());
        sql += " ORDER BY " + SQLStringBuilder.DMG_METERNUMBER + ", " + SQLStringBuilder.DMG_DEVICEID + ", " + SQLStringBuilder.PT_POINTOFFSET + ", " + SQLStringBuilder.RPH_TIMESTAMP + " DESC ";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try {
            conn = PoolManager.getInstance().getConnection(dbAlias);

            if (conn == null) {
                CTILogger.info(getClass() + ":  Error getting database connection.");
                return false;
            } else {
                pstmt = conn.prepareStatement(sql);

                pstmt.setTimestamp(1, new Timestamp(getBillingDefaults().getEarliestStartDate()
                                                                        .getTime()));
                rset = pstmt.executeQuery();

                CTILogger.info(" * Start looping through return resultset");

                int recCount = 0;
                int currentPointID = 0;
                int currentDeviceID = 0;
                int lastPointID = 0;
                int lastDeviceID = 0;

                while (rset.next()) {
                    Timestamp ts = rset.getTimestamp(3);
                    Date tsDate = new Date(ts.getTime());
                    if (tsDate.compareTo(getBillingDefaults().getEndDate()) <= 0) {
                        currentPointID = rset.getInt(2);
                        double multiplier = 1;
                        if (getBillingDefaults().isRemoveMultiplier()) {
                            multiplier = ((Double) getPointIDMultiplierHashTable().get(new Integer(currentPointID))).doubleValue();
                        }

                        if (currentPointID != lastPointID) {
                            lastPointID = currentPointID;

                            String meterNumber = rset.getString(1);
                            double reading = rset.getDouble(4) / multiplier;
                            currentDeviceID = rset.getInt(5);
                            int ptOffset = rset.getInt(6);

                            if (currentDeviceID == lastDeviceID) {

                                populateRecord(tsDate,
                                               (SimpleTOURecord) getRecordVector().get(recCount - 1),
                                               ptOffset,
                                               reading,
                                               ts);

                            } else {
                                SimpleTOURecord record = new SimpleTOURecord(meterNumber);

                                if (populateRecord(tsDate, record, ptOffset, reading, ts)) {

                                    lastDeviceID = currentDeviceID;
                                    getRecordVector().addElement(record);
                                    recCount++;
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rset != null)
                    rset.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e2) {
                e2.printStackTrace();// sometin is up
            }
        }
        CTILogger.info("@" + this.toString() + " Data Collection : Took " + (System.currentTimeMillis() - timer));
        return true;
    }

    /*
     * Helper method to populate a record
     */
    protected boolean populateRecord(Date tsDate, SimpleTOURecord record, int ptOffset,
            double reading, Timestamp timestamp) {

        if (tsDate.compareTo(getBillingDefaults().getEnergyStartDate()) <= 0) {
            return false;
        }

        switch (ptOffset) {

        case 1: // KWh
            record.setTotalConsumption(reading);
            record.setTimeTotal(timestamp);
            record.setDateTotal(timestamp);
            break;

        case 101: // On-Peak KWh
            record.setOnPeakReadingKWh(reading);
            break;

        case 121: // Off-Peak KWh
            record.setOffPeakReadingKWh(reading);
            break;

        case 11: // KW
            record.setReadingKW(reading);
            record.setTimeKW(timestamp);
            record.setDateKW(timestamp);
            break;

        case 111: // On-Peak KW
            record.setOnPeakReadingKW(reading);
            record.setOnPeakTimeKW(timestamp);
            record.setOnPeakDateKW(timestamp);
            break;

        case 131: // Off-Peak KW
            record.setOffPeakReadingKW(reading);
            record.setOffPeakTimeKW(timestamp);
            record.setOffPeakDateKW(timestamp);
            break;
        }

        return true;
    }

}
