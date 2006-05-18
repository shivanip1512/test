package com.cannontech.billing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.cannontech.billing.record.CTICSVRecord;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.database.db.point.UnitMeasure;

/**
 * Billing format for CTICSV format
 */
public class CTICSVFormat extends FileFormatBase {

    /**
     * CTICSVFormat constructor comment.
     */
    public CTICSVFormat() {
        super();
    }

    /**
     * Retrieves values from the database and inserts them in a FileFormatBase
     * object
     * @param dbAlias - Alias for db to retrieve values from
     * @return True if retrieval was successful
     */
    public boolean retrieveBillingData(String dbAlias) {
        long timer = System.currentTimeMillis();

        if (dbAlias == null)
            dbAlias = CtiUtilities.getDatabaseAlias();

        String[] SELECT_COLUMNS = { SQLStringBuilder.PAO_PAONAME, SQLStringBuilder.RPH_VALUE,
                SQLStringBuilder.RPH_TIMESTAMP, SQLStringBuilder.PT_POINTID,
                SQLStringBuilder.UM_UOMNAME, SQLStringBuilder.PT_POINTOFFSET };

        String[] FROM_TABLES = { YukonPAObject.TABLE_NAME, RawPointHistory.TABLE_NAME,
                Point.TABLE_NAME, DeviceMeterGroup.TABLE_NAME, PointUnit.TABLE_NAME,
                UnitMeasure.TABLE_NAME };

        SQLStringBuilder builder = new SQLStringBuilder();
        String sql = new String((builder.buildSQLStatement(SELECT_COLUMNS,
                                                           FROM_TABLES,
                                                           getBillingDefaults(),
                                                           validAnalogPtOffsets,
                                                           validAccPtOffsets)).toString());
        sql += " ORDER BY " + SQLStringBuilder.PAO_PAONAME + ", " + SQLStringBuilder.PT_POINTOFFSET + ", " + SQLStringBuilder.PT_POINTID + ", " + SQLStringBuilder.RPH_TIMESTAMP + " DESC ";

        /*
         * String sql = "SELECT PAO.PAONAME, RPH.VALUE, RPH.TIMESTAMP,
         * PT.POINTID, UM.UOMNAME, PT.POINTOFFSET " + " FROM YUKONPAOBJECT PAO,
         * RAWPOINTHISTORY RPH, POINT PT, DEVICEMETERGROUP DMG, " + " POINTUNIT
         * PU, UNITMEASURE UM " + " WHERE PAO.PAOBJECTID = DMG.DEVICEID " + "
         * AND PAO.PAOBJECTID = PT.PAOBJECTID " + " AND RPH.TIMESTAMP <= ? " +
         * //endDate timeStamp " AND DMG.COLLECTIONGROUP IN ('" +
         * collectionGroups.get(0) + "'"; for (int i = 1; i <
         * collectionGroups.size(); i++) { sql += ", '" +
         * collectionGroups.get(i) + "'"; } sql += ") AND PT.POINTID =
         * RPH.POINTID " + " AND PT.POINTID = PU.POINTID " + " AND PU.UOMID =
         * UM.UOMID " + " AND ( (POINTTYPE = 'Analog' AND POINTOFFSET IN (" +
         * validAnalogPtOffsets[0]; for (int i = 1; i <
         * validAnalogPtOffsets.length; i++) { sql += ", " +
         * validAnalogPtOffsets[i]; } sql += ")) OR (POINTTYPE =
         * 'PulseAccumulator' AND POINTOFFSET IN (" + validAccPtOffsets[0]; for
         * (int i = 1; i < validAccPtOffsets.length; i++) { sql += ", " +
         * validAccPtOffsets[i]; } sql += ") )) ORDER BY PAO.PAONAME,
         * PT.POINTOFFSET, PT.POINTID, RPH.TIMESTAMP DESC";
         * com.cannontech.clientutils.CTILogger.info(" Local SQL: " + sql);
         */
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

                CTILogger.info(" *Start looping through return resultset");

                int currentPointID = 0;
                int lastPointID = 0;
                while (rset.next()) {
                    Timestamp ts = rset.getTimestamp(3);
                    Date tsDate = new Date(ts.getTime());
                    if (tsDate.compareTo(getBillingDefaults().getEndDate()) <= 0)
                    // ts <= maxtime, CONTINUE ON!
                    {
                        currentPointID = rset.getInt(4);
                        double multiplier = 1;
                        if (getBillingDefaults().isRemoveMultiplier()) {
                            multiplier = ((Double) getPointIDMultiplierHashTable().get(new Integer(currentPointID))).doubleValue();
                        }

                        // Our break label so we can exit the if-else checks
                        inValidTimestamp: if (currentPointID != lastPointID)
                        // just getting max time for each point
                        {
                            int ptOffset = rset.getInt(6);

                            if (isKWH(ptOffset) || ptOffset == 1) {
                                if (tsDate.compareTo(getBillingDefaults().getEnergyStartDate()) <= 0)
                                    // ts <= mintime, fail!
                                    break inValidTimestamp;
                            } else if (isKW(ptOffset) || isKVAR(ptOffset)) {
                                if (tsDate.compareTo(getBillingDefaults().getDemandStartDate()) <= 0)
                                    // ts <= mintime, fail!
                                    break inValidTimestamp;
                            }

                            lastPointID = currentPointID;

                            String paoName = rset.getString(1);
                            double reading = rset.getDouble(2) / multiplier;
                            String unitMeasure = rset.getString(5);

                            CTICSVRecord csvRec = new CTICSVRecord(paoName,
                                                                   reading,
                                                                   unitMeasure,
                                                                   ts);
                            getRecordVector().addElement(csvRec);

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
}
