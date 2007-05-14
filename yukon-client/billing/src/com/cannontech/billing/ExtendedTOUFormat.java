package com.cannontech.billing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.cannontech.billing.record.ExtendedTOURecord;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.database.db.point.UnitMeasure;

/**
 * Billing format for Simple TOU
 */
public class ExtendedTOUFormat extends FileFormatBase {

    public static final String GALLONS = "Gallons";
    public static final String GasCFT = "GAS-CFT";

    public ExtendedTOUFormat() {
        super();
    }

    /**
     * Retrieves values from the database and inserts them in a FileFormatBase
     * object
     */
    @Override
    public boolean retrieveBillingData( ) {
        long timer = System.currentTimeMillis();

        String[] SELECT_COLUMNS = { SQLStringBuilder.DMG_METERNUMBER, SQLStringBuilder.PT_POINTID,
                SQLStringBuilder.RPH_TIMESTAMP, SQLStringBuilder.RPH_VALUE,
                SQLStringBuilder.DMG_DEVICEID, SQLStringBuilder.PT_POINTOFFSET,
                SQLStringBuilder.UM_UOMNAME };

        String[] FROM_TABLES = { RawPointHistory.TABLE_NAME, Point.TABLE_NAME,
                DeviceMeterGroup.TABLE_NAME, PointUnit.TABLE_NAME, UnitMeasure.TABLE_NAME };

        SQLStringBuilder builder = new SQLStringBuilder();
        String sql = new String((builder.buildSQLStatement(SELECT_COLUMNS,
                                                           FROM_TABLES,
                                                           getBillingDefaults(),
                                                           null,
                                                           validTOUAccPtOffsets,
                                                           validTOUPeakDemandAccOffsets)).toString());
        sql += " ORDER BY " + SQLStringBuilder.DMG_METERNUMBER + ", " + SQLStringBuilder.DMG_DEVICEID + ", " + SQLStringBuilder.UM_UOMNAME + " DESC, " + SQLStringBuilder.PT_POINTOFFSET + ", " + SQLStringBuilder.RPH_TIMESTAMP + " DESC ";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

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
                            String unitOfMeasure = rset.getString(7);

                            if ((currentDeviceID == lastDeviceID) && !unitOfMeasure.equals(ExtendedTOUFormat.GALLONS) && !unitOfMeasure.equals(ExtendedTOUFormat.GasCFT)) {

                                populateRecord(tsDate,
                                               (ExtendedTOURecord) getRecordVector().get(recCount - 1),
                                               ptOffset,
                                               reading,
                                               ts,
                                               unitOfMeasure);

                            } else {
                                ExtendedTOURecord record = new ExtendedTOURecord(meterNumber);

                                if (populateRecord(tsDate,
                                                   record,
                                                   ptOffset,
                                                   reading,
                                                   ts,
                                                   unitOfMeasure)) {

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
            CTILogger.error(e);
        } finally {
            try {
                if (rset != null)
                    rset.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e2) {
                CTILogger.error(e2);
            }
        }
        CTILogger.info("@" + this.toString() + " Data Collection : Took " + (System.currentTimeMillis() - timer));
        return true;
    }

    /*
     * Helper method to populate a record
     */
    protected boolean populateRecord(Date tsDate, ExtendedTOURecord record, int ptOffset,
            double reading, Timestamp timestamp, String unitOfMeasure) {

        if (tsDate.compareTo(getBillingDefaults().getEnergyStartDate()) <= 0) {
            return false;
        }

        switch (ptOffset) {

        case 1: // KWh
            record.setTotalConsumption(reading);
            record.setTimeTotal(timestamp);
            record.setDateTotal(timestamp);

            record.setReadingCode(ExtendedTOURecord.ELECTRIC_CODE);

            break;

        case 2: // Gas or Water
            record.setTotalConsumption(reading);
            record.setTimeTotal(timestamp);
            record.setDateTotal(timestamp);

            record.setReadingCode(getReadingCode(unitOfMeasure));

            break;

        case 3: // Gas or Water
            record.setTotalConsumption(reading);
            record.setTimeTotal(timestamp);
            record.setDateTotal(timestamp);

            record.setReadingCode(getReadingCode(unitOfMeasure));

            break;

        case 101: // Rate A KWh
            record.setOnPeakReadingKWh(reading);
            record.setReadingCode(ExtendedTOURecord.ELECTRIC_CODE);
            record.setTimeTotalForRate(timestamp);
            record.setDateTotalForRate(timestamp);
            break;

        case 121: // Rate B KWh
            record.setOffPeakReadingKWh(reading);
            record.setReadingCode(ExtendedTOURecord.ELECTRIC_CODE);
            record.setTimeTotalForRate(timestamp);
            record.setDateTotalForRate(timestamp);
            break;

        case 141: // Rate C KWh
            record.setRateCReadingKWh(reading);
            record.setReadingCode(ExtendedTOURecord.ELECTRIC_CODE);
            record.setTimeTotalForRate(timestamp);
            record.setDateTotalForRate(timestamp);
            break;

        case 161: // Rate D KWh
            record.setRateDReadingKWh(reading);
            record.setReadingCode(ExtendedTOURecord.ELECTRIC_CODE);
            record.setTimeTotalForRate(timestamp);
            record.setDateTotalForRate(timestamp);
            break;

        case 11: // KW
            record.setReadingKW(reading);
            record.setTimeKW(timestamp);
            record.setDateKW(timestamp);
            break;

        case 111: // Rate A KW
            record.setOnPeakReadingKW(reading);
            record.setOnPeakTimeKW(timestamp);
            record.setOnPeakDateKW(timestamp);
            break;

        case 131: // Rate B KW
            record.setOffPeakReadingKW(reading);
            record.setOffPeakTimeKW(timestamp);
            record.setOffPeakDateKW(timestamp);
            break;

        case 151: // Rate C KW
            record.setRateCReadingKW(reading);
            record.setRateCTimeKW(timestamp);
            record.setRateCDateKW(timestamp);
            break;

        case 171: // Rate D KW
            record.setRateDReadingKW(reading);
            record.setRateDTimeKW(timestamp);
            record.setRateDDateKW(timestamp);
            break;
        }

        return true;
    }

    /*
     * Helper method to determine the correct reading code based on the unit of
     * measure
     */
    private String getReadingCode(String unitOfMeasure) {
        String code = "";

        if (unitOfMeasure.equals(GALLONS)) {
            code = ExtendedTOURecord.WATER_CODE;
        } else if (unitOfMeasure.equals(GasCFT)) {
            code = ExtendedTOURecord.GAS_CODE;
        } else //default to Electric
        	code = ExtendedTOURecord.ELECTRIC_CODE;

        return code;
    }

}
