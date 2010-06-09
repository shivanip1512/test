package com.cannontech.multispeak.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.database.db.point.UnitMeasure;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.FormattedBlockService;
import com.cannontech.multispeak.dao.MspRawPointHistoryDao;
import com.cannontech.multispeak.data.MeterReadFactory;
import com.cannontech.multispeak.data.ReadableDevice;
import com.cannontech.multispeak.deploy.service.FormattedBlock;
import com.cannontech.multispeak.deploy.service.MeterRead;

public class MspRawPointHistoryDaoImpl implements MspRawPointHistoryDao
{
    /**
     * Returns a MeterRead[] with kW and kWh readings for MeterNo, > startDate and <= endDate 
     * @param meterNo
     * @param startDate
     * @param endDate
     * @return
     */
    public MeterRead[] retrieveMeterReads(ReadBy readBy, String readByValue, Date startDate, 
            Date endDate, String lastReceived, int maxRecords) {
        Date timerStart = new Date();
        
        MeterRead[] meterReadArray = new MeterRead[0];
        String sql = "SELECT DISTINCT P.POINTID, TIMESTAMP, VALUE, P.POINTOFFSET, P.POINTTYPE, UOM.UOMID, " + 
                     " PAO.TYPE, PAO.CATEGORY, PAO.PAOBJECTID, DMG.METERNUMBER " + 
                     " FROM " + RawPointHistory.TABLE_NAME + " rph, " + Point.TABLE_NAME + " p, " +
                     PointUnit.TABLE_NAME + " pu, " + UnitMeasure.TABLE_NAME + " uom, " + 
                     YukonPAObject.TABLE_NAME + " pao, " + DeviceMeterGroup.TABLE_NAME + " dmg " +
                     " WHERE RPH.POINTID = P.POINTID " +
                     " AND P.PAOBJECTID = PAO.PAOBJECTID " +
                     " AND PAO.PAOBJECTID = DMG.DEVICEID " +
                     " AND P.POINTID = PU.POINTID " +
                     " AND PU.UOMID = UOM.UOMID " +
                     " AND (POINTOFFSET < 101 or POINTOFFSET > 104)";   //exclude Profile data for now.
                     if( readBy == ReadBy.METER_NUMBER)
                         sql += " AND DMG.METERNUMBER = ? ";
                     sql += " AND TIMESTAMP >= ? AND TIMESTAMP <= ? ";
                     if ( !StringUtils.isBlank(lastReceived) )
                         sql += " AND DMG.METERNUMBER > ? ";
                     sql += " ORDER BY DMG.METERNUMBER, P.POINTID, TIMESTAMP";  //P.POINTID, 
        
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
                if ( !StringUtils.isBlank(lastReceived) )
                    pstmt.setString(4, lastReceived);
                
                CTILogger.info("Data Collection Started: START DATE >= " + startDate + " - STOP DATE < " + endDate);
                rset = pstmt.executeQuery();

                int lastPaoID = -1;
                int paobjectID = 0;
                Date prevDateTime = new Date();
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
                    int paoType = PAOGroups.getPAOType(category, type);
                    paobjectID = rset.getInt(9);
                    String meterNumber = rset.getString(10);
                    
                    //Store any previous meter readings.
                    if (dateTime.after(prevDateTime) || lastPaoID != paobjectID) {
                        if( device != null && device.isPopulated())
                            meterReadList.add(device.getMeterRead());
                        device = null;
                        if( lastPaoID != paobjectID && meterReadList.size() >= maxRecords)
                            break;
                    }

                    if( device == null)
                        device = MeterReadFactory.createMeterReadObject(paoType, meterNumber);

                    if (device != null) {	//a device exists and was successfully created by the factory
                    	device.populate( pointType, pointOffset, uomId, dateTime, new Double(value));
                    }
                    prevDateTime.setTime(dateTime.getTime());
                    lastPaoID = paobjectID;
                }
                
                //Add the last meterRead object
                if( device != null && device.isPopulated() &&   //made it all the way through and need to add last one 
                        lastPaoID == paobjectID)  //but make sure we didn't exit from the break statement
                    meterReadList.add(device.getMeterRead());

                meterReadArray = new MeterRead[meterReadList.size()];
                meterReadList.toArray(meterReadArray);
                
                CTILogger.info( (new Date().getTime() - timerStart.getTime())*.001 + 
                                                          " Secs for RPH Data for MeterNumber \"" + readByValue + "\" to be loaded" );                
            }
        }
        
        catch( java.sql.SQLException e ) {
            CTILogger.error(e);
        }
        finally
        {
        	SqlUtils.close(rset, pstmt, conn );
        }
        return meterReadArray;
    }
    
    public MeterRead[] retrieveLatestMeterReads(String lastReceived, int maxRecords) {
        Date timerStart = new Date();
        
        MeterRead[] meterReadArray = new MeterRead[0];
        String sql = "SELECT DISTINCT P.POINTID, TIMESTAMP, VALUE, P.POINTOFFSET, P.POINTTYPE, UOM.UOMID, " + 
                     " PAO.TYPE, PAO.CATEGORY, PAO.PAOBJECTID, DMG.METERNUMBER " + 
                     " FROM " + RawPointHistory.TABLE_NAME + " rph, " + Point.TABLE_NAME + " p, " +
                     PointUnit.TABLE_NAME + " pu, " + UnitMeasure.TABLE_NAME + " uom, " + 
                     YukonPAObject.TABLE_NAME + " pao, " + DeviceMeterGroup.TABLE_NAME + " dmg, " +
                     " (SELECT distinct r.pointid, max(r.timestamp) AS rDate " + 
              		 " FROM rawpointhistory r " +
              		 " GROUP BY pointid) irph " +
                     " WHERE RPH.POINTID = P.POINTID " +
                     " AND RPH.POINTID = IRPH.POINTID " +
                     " AND RPH.TIMESTAMP = IRPH.RDATE " +
                     " AND P.PAOBJECTID = PAO.PAOBJECTID " +
                     " AND PAO.PAOBJECTID = DMG.DEVICEID " +
                     " AND P.POINTID = PU.POINTID " +
                     " AND PU.UOMID = UOM.UOMID " +
                     " AND (POINTOFFSET < 101 or POINTOFFSET > 104)";   //exclude Profile data for now.
                     if ( !StringUtils.isBlank(lastReceived) )
                         sql += " AND DMG.METERNUMBER > ? ";
                     sql += " ORDER BY METERNUMBER, PAO.PAOBJECTID, P.POINTID, TIMESTAMP";  //P.POINTID, 
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            if( conn == null ) {
                CTILogger.error(getClass() + ":  Error getting database connection.");
                return meterReadArray;
            } else {
                pstmt = conn.prepareStatement(sql);
                if ( !StringUtils.isBlank(lastReceived) ) {
                    pstmt.setString(1, lastReceived);
                }
                
                rset = pstmt.executeQuery();

                int lastPaoID = -1;
                int paobjectID = 0;
                Date prevDateTime = new Date();
                List<MeterRead> meterReadList = new ArrayList<MeterRead>();
                
                ReadableDevice device = null;
                while( rset.next()) {
                	
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
                    int paoType = PAOGroups.getPAOType(category, type);
                    paobjectID = rset.getInt(9);
                    String meterNumber = rset.getString(10);
                    
                    //Store any previous meter readings.
                    if (dateTime.after(prevDateTime) || lastPaoID != paobjectID) {
                        if( device != null && device.isPopulated()) {
                            meterReadList.add(device.getMeterRead());
                        }
                        device = null;
                        if( lastPaoID != paobjectID && meterReadList.size() >= maxRecords) {
                            break;
                        }
                    }

                    if( device == null) {
                        device = MeterReadFactory.createMeterReadObject(paoType, meterNumber);
                    }

                    if (device != null) {	//a device exists and was successfully created by the factory
                    	device.populate( pointType, pointOffset, uomId, dateTime, new Double(value));
                    }
                    prevDateTime.setTime(dateTime.getTime());
                    lastPaoID = paobjectID;
                }
                
                //Add the last meterRead object
                if( device != null && device.isPopulated() &&   //made it all the way through and need to add last one 
                        lastPaoID == paobjectID) { //but make sure we didn't exit from the break statement
                    meterReadList.add(device.getMeterRead());
                }

                meterReadArray = new MeterRead[meterReadList.size()];
                meterReadList.toArray(meterReadArray);
                
                CTILogger.info( (new Date().getTime() - timerStart.getTime())*.001 + " Secs for Latest RPH Data to be loaded" );                
            }
        }
        
        catch( java.sql.SQLException e ) {
            CTILogger.error(e);
        } finally {
        	SqlUtils.close(rset, pstmt, conn );
        }
        return meterReadArray;
    }
    
    public FormattedBlock retrieveBlock(FormattedBlockService<Block> block, Date startDate, Date endDate, String lastReceived, int maxRecords) {
        Date timerStart = new Date();
        
        List<Block> blockList = new ArrayList<Block>();
        
        String sql = "SELECT DISTINCT P.POINTID, TIMESTAMP, VALUE, P.POINTTYPE, P.POINTOFFSET, " + 
                     " PAO.TYPE, PAO.PAOBJECTID, DMG.METERNUMBER, RPH.QUALITY " + 
                     " FROM " + RawPointHistory.TABLE_NAME + " rph, " + Point.TABLE_NAME + " p, " +
                     YukonPAObject.TABLE_NAME + " pao, " + DeviceMeterGroup.TABLE_NAME + " dmg " +
                     " WHERE RPH.POINTID = P.POINTID " +
                     " AND P.PAOBJECTID = PAO.PAOBJECTID " +
                     " AND PAO.PAOBJECTID = DMG.DEVICEID " +
                     
                     " AND TIMESTAMP >= ? AND TIMESTAMP <= ? ";
                     if ( !StringUtils.isBlank(lastReceived) )
                         sql += " AND DMG.METERNUMBER > ? ";
                     sql += " ORDER BY DMG.METERNUMBER, PAO.PAOBJECTID, TIMESTAMP";  //P.POINTID, 
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try
        {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            if( conn == null ) {
                CTILogger.error(getClass() + ":  Error getting database connection.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setTimestamp(1, new Timestamp( startDate.getTime() ));
                pstmt.setTimestamp(2, new Timestamp( endDate.getTime() ));
                if ( !StringUtils.isBlank(lastReceived) )
                    pstmt.setString(3, lastReceived);
                
                CTILogger.info("Data Collection Started: START DATE >= " + startDate + " - STOP DATE < " + endDate);
                rset = pstmt.executeQuery();

                int lastPaoID = -1;
                int paobjectID = 0;
                Date prevDateTime = new Date();
                
                Block b = block.getNewBlock();
                while( rset.next())
                {
                    int pointID = rset.getInt(1);
                    Timestamp ts = rset.getTimestamp(2);
                    Date dateTime = new Date();
                    dateTime.setTime(ts.getTime());
                    double value = rset.getDouble(3);
                    String ptType = rset.getString(4);
                    PointType pointType = PointType.getForString(ptType);
                    int pointOffset = rset.getInt(5);
                    String type = rset.getString(6);
                    PaoType paoType = PaoType.getForDbString(type);
                    paobjectID = rset.getInt(7);
                    String meterNumber = rset.getString(8);

                    //This is only a partial Meter object load
                    Meter meter = new Meter();
                    meter.setMeterNumber(meterNumber);
                    meter.setDeviceType(paoType);
                    meter.setTypeStr(type);
                    meter.setDeviceId(paobjectID);
                    
                    RichPointData richPointData = buildRichPointData(rset, pointType, pointOffset, paobjectID, paoType);
                    
                    //Store any previous meter readings.
                    if (dateTime.after(prevDateTime) || lastPaoID != paobjectID) {
                        if( b.hasData())
                            blockList.add(b);
                        b = null;
                        if( lastPaoID != paobjectID && blockList.size() >= maxRecords)
                            break;
                    }

                    if( b == null) 
                        b = block.getNewBlock();

                    b.populate( meter, richPointData);
                    prevDateTime.setTime(dateTime.getTime());
                    lastPaoID = paobjectID;
                }
                
                //Add the last meterRead object
                if( b != null && b.hasData() &&   //made it all the way through and need to add last one 
                        lastPaoID == paobjectID)  //but make sure we didn't exit from the break statement
                    blockList.add(b);

                CTILogger.info( (new Date().getTime() - timerStart.getTime())*.001 + 
                                                          " Secs for RPH Data for formattedBlock to be loaded" );                
            }
        }
        
        catch( java.sql.SQLException e ) {
            CTILogger.error(e);
        }
        finally
        {
        	SqlUtils.close(rset, pstmt, conn );
        }
        return block.createFormattedBlock(blockList);
    }
    
    public FormattedBlock retrieveLatestBlock(FormattedBlockService<Block> block, String lastReceived, int maxRecords) {
        Date timerStart = new Date();
        
        List<Block> blockList = new ArrayList<Block>();
        
        String sql = "SELECT DISTINCT P.POINTID, TIMESTAMP, VALUE, P.POINTTYPE, P.POINTOFFSET, " + 
                     " PAO.TYPE, PAO.PAOBJECTID, DMG.METERNUMBER,  RPH.QUALITY " + 
                     " FROM " + RawPointHistory.TABLE_NAME + " rph, " + Point.TABLE_NAME + " p, " +
                     YukonPAObject.TABLE_NAME + " pao, " + DeviceMeterGroup.TABLE_NAME + " dmg, " +
                     " (SELECT distinct r.pointid, max(r.timestamp) AS rDate " + 
              		 " FROM rawpointhistory r " +
              		 " GROUP BY pointid) irph " +
                     " WHERE RPH.POINTID = P.POINTID " +
                     " AND RPH.POINTID = IRPH.POINTID " +
                     " AND RPH.TIMESTAMP = IRPH.RDATE " +
                     " AND P.PAOBJECTID = PAO.PAOBJECTID " +
                     " AND PAO.PAOBJECTID = DMG.DEVICEID ";
                     if ( !StringUtils.isBlank(lastReceived) )
                         sql += " AND DMG.METERNUMBER > ? ";
                     sql += " ORDER BY DMG.METERNUMBER, PAO.PAOBJECTID, TIMESTAMP";  //P.POINTID, 
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try
        {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            if( conn == null ) {
                CTILogger.error(getClass() + ":  Error getting database connection.");
            } else {
                pstmt = conn.prepareStatement(sql);
                if ( !StringUtils.isBlank(lastReceived) ) {
                    pstmt.setString(1, lastReceived);
                }
                
                rset = pstmt.executeQuery();

                int lastPaoID = -1;
                int paobjectID = 0;
                
                Block b = block.getNewBlock();
                
                while( rset.next()) {
                	
                    int pointID = rset.getInt(1);
                    Timestamp ts = rset.getTimestamp(2);
                    Date dateTime = new Date();
                    dateTime.setTime(ts.getTime());
                    double value = rset.getDouble(3);
                    String ptType = rset.getString(4);
                    PointType pointType = PointType.getForString(ptType);
                    int pointOffset = rset.getInt(5);
                    String type = rset.getString(6);
                    PaoType paoType = PaoType.getForDbString(type);
                    paobjectID = rset.getInt(7);
                    String meterNumber = rset.getString(8);
                    Integer quality = rset.getInt(9);
                    //This is only a partial Meter object load
                    Meter meter = new Meter();
                    meter.setMeterNumber(meterNumber);
                    meter.setDeviceType(paoType);
                    meter.setTypeStr(type);
                    meter.setDeviceId(paobjectID);
                    
                    RichPointData richPointData = buildRichPointData(rset, pointType, pointOffset, paobjectID, paoType);

                    //Store any previous meter readings.
                    if (lastPaoID != paobjectID) {
                        if( b.hasData()) {
                            blockList.add(b);
                        }
                        b = null;
                        if( lastPaoID != paobjectID && blockList.size() >= maxRecords) {
                            break;
                        }
                    }

                    if( b == null) { 
                        b = block.getNewBlock();
                    }

                    b.populate( meter, richPointData);
                    lastPaoID = paobjectID;
                }
                
                //Add the last meterRead object
                if( b != null && b.hasData() ) {  //made it all the way through and need to add last one 
                    blockList.add(b);
                }

                CTILogger.info( (new Date().getTime() - timerStart.getTime())*.001 + 
                                                          " Secs for RPH Data for latest formattedBlock to be loaded" );                
            }
        }
        
        catch( java.sql.SQLException e ) {
            CTILogger.error(e);
        }
        finally
        {
        	SqlUtils.close(rset, pstmt, conn );
        }
        return block.createFormattedBlock(blockList);
    }
    
    public FormattedBlock retrieveBlockByMeterNo(FormattedBlockService<Block> block, Date startDate, 
                                                Date endDate, String meterNumber) {
        Date timerStart = new Date();
        
        List<Block> blockList = new ArrayList<Block>();
        
        String sql = "SELECT DISTINCT P.POINTID, TIMESTAMP, VALUE, P.POINTTYPE, P.POINTOFFSET, " + 
                     " PAO.TYPE, PAO.PAOBJECTID, RPH.QUALITY " + 
                     " FROM " + RawPointHistory.TABLE_NAME + " rph, " + Point.TABLE_NAME + " p, " +
                     YukonPAObject.TABLE_NAME + " pao, " + DeviceMeterGroup.TABLE_NAME + " dmg " +
                     " WHERE RPH.POINTID = P.POINTID " +
                     " AND P.PAOBJECTID = PAO.PAOBJECTID " +
                     " AND PAO.PAOBJECTID = DMG.DEVICEID " +
                     
                     " AND TIMESTAMP >= ? AND TIMESTAMP <= ? " +
                     " AND DMG.METERNUMBER = ? ";
                     sql += " ORDER BY PAO.PAOBJECTID, TIMESTAMP";  //P.POINTID, 
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try
        {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            if( conn == null ) {
                CTILogger.error(getClass() + ":  Error getting database connection.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setTimestamp(1, new Timestamp( startDate.getTime() ));
                pstmt.setTimestamp(2, new Timestamp( endDate.getTime() ));
                pstmt.setString(3, meterNumber);
                
                CTILogger.info("Data Collection Started: START DATE >= " + startDate + " - STOP DATE < " + endDate);
                rset = pstmt.executeQuery();

                int paobjectID = 0;
                Date prevDateTime = new Date();
                
                Block b = block.getNewBlock();
                while( rset.next())
                {
                    int pointID = rset.getInt(1);
                    Timestamp ts = rset.getTimestamp(2);
                    Date dateTime = new Date();
                    dateTime.setTime(ts.getTime());
                    double value = rset.getDouble(3);
                    String ptType = rset.getString(4);
                    PointType pointType = PointType.getForString(ptType);
                    int pointOffset = rset.getInt(5);
                    String type = rset.getString(6);
                    PaoType paoType = PaoType.getForDbString(type);
                    paobjectID = rset.getInt(7);

                    //This is only a partial Meter object load
                    Meter meter = new Meter();
                    meter.setMeterNumber(meterNumber);
                    meter.setDeviceType(paoType);
                    meter.setTypeStr(type);
                    meter.setDeviceId(paobjectID);
                    
                    RichPointData richPointData = buildRichPointData(rset, pointType, pointOffset, paobjectID, paoType);
                    
                    //Store any previous meter readings.
                    if (dateTime.after(prevDateTime)) {
                        if( b.hasData())
                            blockList.add(b);
                        b = null;
                    }

                    if( b == null) 
                        b = block.getNewBlock();

                    b.populate( meter, richPointData);
                    prevDateTime.setTime(dateTime.getTime());
                }
                
                //Add the last meterRead object
                if( b.hasData()) {   //made it all the way through and need to add last one 
                    blockList.add(b);
                }

                CTILogger.info( (new Date().getTime() - timerStart.getTime())*.001 + 
                                                          " Secs for RPH Data for formattedBlock to be loaded" );                
            }
        }
        
        catch( java.sql.SQLException e ) {
            CTILogger.error(e);
        }
        finally
        {
        	SqlUtils.close(rset, pstmt, conn );
        }
        return block.createFormattedBlock(blockList);
    }

    private RichPointData buildRichPointData(final ResultSet rset, PointType pointType, int pointOffset, int paobjectID, PaoType paoType) throws SQLException {
        PointValueBuilder builder = PointValueBuilder.create();
        builder.withResultSet(rset);
        builder.withType(pointType);
        PointValueQualityHolder pointData = builder.build();
        
        PaoPointIdentifier paoPointIdentifier = PaoPointIdentifier.createPaoPointIdentifier(paobjectID, paoType, pointType, pointOffset);
        
        RichPointData richPointData = new RichPointData(pointData, paoPointIdentifier);
        return richPointData;
    }
}
