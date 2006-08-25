package com.cannontech.multispeak.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.database.db.point.UnitMeasure;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.multispeak.dao.RawPointHistoryDao;
import com.cannontech.multispeak.data.MeterReadFactory;
import com.cannontech.multispeak.data.ReadableDevice;
import com.cannontech.multispeak.service.MeterRead;
import com.cannontech.yukon.IDatabaseCache;

public class RawPointHistoryDaoImpl implements RawPointHistoryDao
{
    private JdbcOperations jdbcOps;
    private TransactionTemplate transactionTemplate;
    private IDatabaseCache databaseCache;
    private NextValueHelper nextValueHelper;
    /**
     * Returns a MeterRead[] with kW and kWh readings for MeterNo, > startDate and <= endDate 
     * @param meterNo
     * @param startDate
     * @param endDate
     * @return
     */
    public MeterRead[] retrieveMeterReads(ReadBy readBy, String readByValue, Date startDate, Date endDate) {
        
        MeterRead[] meterReadArray = new MeterRead[0];
        String sql = "SELECT DISTINCT P.POINTID, TIMESTAMP, VALUE, P.POINTOFFSET, P.POINTTYPE, UOM.UOMID, " + 
                     " PAO.TYPE, PAO.CATEGORY, PAO.PAOBJECTID, DMG.METERNUMBER " + 
                     " FROM " + RawPointHistory.TABLE_NAME + " rph, " + Point.TABLE_NAME + " p, " +
                     PointUnit.TABLE_NAME + " pu, " + UnitMeasure.TABLE_NAME + " uom, " + 
                     YukonPAObject.TABLE_NAME + " pao, " + DeviceMeterGroup.TABLE_NAME + " dmg " +
                     " WHERE RPH.POINTID = P.POINTID " +
                     " AND P.PAOBJECTID = PAO.PAOBJECTID " +
                     " AND PAO.PAOBJECTID = DMG.DEVICEID ";
                     if( readBy == ReadBy.METER_NUMBER)
                         sql += " AND DMG.METERNUMBER = ? ";
                     else if (readBy == ReadBy.COLL_GROUP)
                         sql += " AND DMG.COLLECTIONGROUP = ? ";
                     sql += " AND P.POINTID = PU.POINTID " +
                     " AND PU.UOMID = UOM.UOMID " +
                     " AND TIMESTAMP > ? AND TIMESTAMP <= ? " +
                     " ORDER BY PAO.PAOBJECTID, TIMESTAMP";//, P.POINTID ";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try
        {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            if( conn == null )
            {
                CTILogger.error(getClass() + ":  Error getting database connection.");
                return meterReadArray;
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, readByValue);
                pstmt.setTimestamp(2, new Timestamp( startDate.getTime() ));
                pstmt.setTimestamp(3, new Timestamp( endDate.getTime() ));
                CTILogger.info("Data Collection Started: START DATE >= " + startDate + " - STOP DATE < " + endDate);
                rset = pstmt.executeQuery();

                int lastPointOffset = -1;
                int lastPaoID = -1;
                List<MeterRead> meterReadList = new ArrayList<MeterRead>();
                
                ReadableDevice device = null;
                while( rset.next())
                {
                    int pointID = rset.getInt(1);
                    Timestamp ts = rset.getTimestamp(2);
                    Date dateTime = new Date();
                    dateTime.setTime(ts.getTime());
                    double value = rset.getDouble(3);
                    int pointOffset = rset.getInt(4);
                    String ptType = rset.getString(5);
                    int pointType = PointTypes.getType(ptType);
                    int uomId = rset.getInt(6);
                    String type = rset.getString(7);
                    String category = rset.getString(8);
                    int paoCategory = PAOGroups.getCategory(category);
                    int paoType = PAOGroups.getPAOType(category, type);
                    int paobjectID = rset.getInt(9);
                    String meterNumber = rset.getString(10);
                    
                    //Store any previous meter readings.
                    if (pointOffset <= lastPointOffset || lastPaoID != paobjectID) {
                        if( device.isPopulated())
                            meterReadList.add(device.getMeterRead());
                        device = null;
                    }

                    if( device == null)
                        device = MeterReadFactory.createMeterReadObject(paoCategory, paoType, meterNumber);

                    device.populate( pointType, pointOffset, uomId, dateTime, value);
                    lastPointOffset = pointOffset;
                    lastPaoID = paobjectID;
                }
                
                //Add the last meterRead object
                if( device != null && device.isPopulated())
                    meterReadList.add(device.getMeterRead());

                meterReadArray = new MeterRead[meterReadList.size()];
                meterReadList.toArray(meterReadArray);
            }
        }
        
        catch( java.sql.SQLException e )
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if( pstmt != null )
                    pstmt.close();
                if( conn != null )
                    conn.close();
            }
            catch( java.sql.SQLException e )
            {
                e.printStackTrace();
            }
        }
        return meterReadArray;
    }
    /**
     * @param databaseCache The databaseCache to set.
     */
    public void setDatabaseCache(IDatabaseCache databaseCache)
    {
        this.databaseCache = databaseCache;
    }
    /**
     * @param jdbcOps The jdbcOps to set.
     */
    public void setJdbcOps(JdbcOperations jdbcOps)
    {
        this.jdbcOps = jdbcOps;
    }
    /**
     * @param nextValueHelper The nextValueHelper to set.
     */
    public void setNextValueHelper(NextValueHelper nextValueHelper)
    {
        this.nextValueHelper = nextValueHelper;
    }
    /**
     * @param transactionTemplate The transactionTemplate to set.
     */
    public void setTransactionTemplate(TransactionTemplate transactionTemplate)
    {
        this.transactionTemplate = transactionTemplate;
    }
}
