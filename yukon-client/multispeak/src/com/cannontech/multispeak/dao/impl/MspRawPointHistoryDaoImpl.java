package com.cannontech.multispeak.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.point.PointType;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.FormattedBlockService;
import com.cannontech.multispeak.dao.MspRawPointHistoryDao;
import com.cannontech.multispeak.data.MeterReadFactory;
import com.cannontech.multispeak.data.ReadableDevice;
import com.cannontech.multispeak.deploy.service.FormattedBlock;
import com.cannontech.multispeak.deploy.service.MeterRead;

public class MspRawPointHistoryDaoImpl implements MspRawPointHistoryDao
{
	private final Logger log = YukonLogManager.getLogger(MspRawPointHistoryDaoImpl.class);
	
	private YukonJdbcTemplate yukonJdbcTemplate = null;

	@Override
    public MeterRead[] retrieveMeterReads(ReadBy readBy, String readByValue, Date startDate, 
            Date endDate, String lastReceived, int maxRecords) {
    	
    	final long startTime = System.currentTimeMillis();
    	
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT p.pointid, timestamp, value, p.pointOffset, p.pointType,");
        sql.append("pao.type, pao.paobjectId, dmg.meterNumber");
        sql.append("FROM RawPointHistory rph JOIN Point p ON rph.pointId = p.pointId");
        sql.append("JOIN YukonPaobject pao ON p.paobjectId = pao.paobjectId");
        sql.append("JOIN DeviceMeterGroup dmg ON pao.paobjectId = dmg.deviceId");
        sql.append("WHERE ( pointOffset < 101 OR pointOffset > 104)");
        sql.append("AND timestamp").gte(startDate);
        sql.append("AND timestamp").lte(endDate);
    	if (readBy == ReadBy.METER_NUMBER) {
    		sql.append("AND dmg.meterNumber").eq(readByValue);
    	}
    	if (!StringUtils.isBlank(lastReceived) ){
    		sql.append("AND dmg.meterNumber").gt(lastReceived);
    	}
        sql.append("ORDER BY dmg.meterNumber, pao.paobjectId, timestamp"); 
        
        log.info("Data Collection Started: START DATE >= " + startDate + " - STOP DATE <= " + endDate);
        List<MeterRead> meterReadList = new ArrayList<MeterRead>();  
        yukonJdbcTemplate.query(sql, new MeterReadResultSetExtractor(maxRecords, meterReadList));
        
        MeterRead[] meterReadArray = new MeterRead[meterReadList.size()];
        meterReadList.toArray(meterReadArray);

        final long stopTime = System.currentTimeMillis(); 
        log.info( (stopTime - startTime)*.001 + "secs to load RPH data for " + readBy.name() + ": " + readByValue + 
        		" for lastReceived: " + lastReceived + ". RecordSize: " + meterReadArray.length);
        
        return meterReadArray;
    }
    
    @Override
    public MeterRead[] retrieveLatestMeterReads(String lastReceived, int maxRecords) {
    	
    	final long startTime = System.currentTimeMillis();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT p.pointid, timestamp, value, p.pointOffset, p.pointType,");
        sql.append("pao.type, pao.paobjectId, dmg.meterNumber");
        sql.append("FROM RawPointHistory rph JOIN Point p ON rph.pointId = p.pointId");
        sql.append("JOIN YukonPaobject pao ON p.paobjectId = pao.paobjectId");
        sql.append("JOIN DeviceMeterGroup dmg ON pao.paobjectId = dmg.deviceId");
        sql.append("JOIN (SELECT DISTINCT r.pointId, MAX(r.timestamp) AS rDate");
        sql.append(" FROM RawPointHistory r GROUP BY pointId) irph ");
        sql.append(  "ON rph.pointId = irph.pointId AND rph.timestamp = irph.rdate");
        sql.append("WHERE ( pointOffset < 101 OR pointOffset > 104)");
    	if (!StringUtils.isBlank(lastReceived) ){
    		sql.append("AND dmg.meterNumber").gt(lastReceived);
    	}
        sql.append("ORDER BY dmg.meterNumber, pao.paobjectId, timestamp"); 

        log.info("Data Collection Started for Latest Reads.");
        List<MeterRead> meterReadList = new ArrayList<MeterRead>();  
        yukonJdbcTemplate.query(sql, new MeterReadResultSetExtractor(maxRecords, meterReadList));

        MeterRead[] meterReadArray = new MeterRead[meterReadList.size()];
        meterReadList.toArray(meterReadArray);
        
        final long stopTime = System.currentTimeMillis(); 
        log.info( (stopTime - startTime)*.001 + "secs to load RPH data for lastReceived: " + lastReceived + ". RecordSize: " + meterReadArray.length);
        
        return meterReadArray;
    }
    
    /**
     * Inner class used to create a list of meterReads from a result set
     */
    private class MeterReadResultSetExtractor implements ResultSetExtractor {
        
    	private int maxRecords = 0;
    	private List<MeterRead> meterReads; 
    	
    	public MeterReadResultSetExtractor(int maxRecords, List<MeterRead> resultsHolder) {
    		this.maxRecords = maxRecords;
    		this.meterReads = resultsHolder;
    	}

        public Object extractData(ResultSet rset) throws SQLException, DataAccessException {
            
            int prevPaobjectId = -1;
            int paobjectId = 0;
            Date prevDateTime = new Date();
            
            ReadableDevice device = null;
            while( rset.next()) {
//            	int pointId = rset.getInt("pointid");	// not used, commented out to keep record that it's returned
                Date dateTime = rset.getTimestamp("timestamp");
                double value = rset.getDouble("value");
                int pointOffset = rset.getInt("pointoffset");
                PointType pointType = PointType.getForString(rset.getString("pointtype"));
                PointIdentifier pointIdentifier = new PointIdentifier(pointType, pointOffset);
                PaoType paoType = PaoType.getForDbString(rset.getString("type"));
                paobjectId = rset.getInt("paobjectid");
                String meterNumber = rset.getString("meternumber");
                
                //Store any previous meter readings.
                if (dateTime.after(prevDateTime) || prevPaobjectId != paobjectId) {
                    if( device != null && device.isPopulated()) {
                        meterReads.add(device.getMeterRead());
                    }
                    device = null;
                    if( prevPaobjectId != paobjectId && meterReads.size() >= maxRecords) {
                        break;
                    }
                }

                if( device == null) {
                    device = MeterReadFactory.createMeterReadObject(paoType, meterNumber);
                }

                if (device != null) {	//a device exists and was successfully created by the factory
                	device.populate( pointIdentifier, dateTime, value);
                }
                prevDateTime.setTime(dateTime.getTime());
                prevPaobjectId = paobjectId;
            }
            
            //Add the last meterRead object
            if( device != null && device.isPopulated() &&   //made it all the way through and need to add last one 
                    prevPaobjectId == paobjectId) {  //but make sure we didn't exit from the break statement
                meterReads.add(device.getMeterRead());
            }
            return meterReads;
        }
    }
    
    @Override
    public FormattedBlock retrieveBlock(FormattedBlockService<Block> block, 
    		Date startDate, Date endDate, String lastReceived, int maxRecords) {
        
    	final long startTime = System.currentTimeMillis();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT p.pointid, timestamp, value, p.pointOffset, p.pointType, ");
        sql.append("pao.type, pao.paobjectId, dmg.meterNumber, rph.quality");
        sql.append("FROM RawPointHistory rph JOIN Point p ON rph.pointId = p.pointId");
        sql.append("JOIN YukonPaobject pao ON p.paobjectId = pao.paobjectId");
        sql.append("JOIN DeviceMeterGroup dmg ON p.paobjectId = dmg.deviceId");
        sql.append("WHERE timestamp").gte(startDate);
        sql.append("AND timestamp").lte(endDate);
        if (!StringUtils.isBlank(lastReceived)) {
        	sql.append("AND dmg.meterNumber").gt(lastReceived);
        }
        sql.append("ORDER BY dmg.meterNumber, pao.paobjectId, timestamp");
        
        
        log.info("Data Collection Started: START DATE >= " + startDate + " - STOP DATE <= " + endDate);
        List<Block> blockList = new ArrayList<Block>();  
        yukonJdbcTemplate.query(sql, new FormattedBlockResultSetExtractor(maxRecords, block, blockList, false));
        
        final long stopTime = System.currentTimeMillis();
        log.info( (stopTime - startTime)*.001 + "secs to load RPH data for lastReceived: " + lastReceived + ". RecordSize: " + blockList.size());
        
        return block.createFormattedBlock(blockList);
    }
    
    @Override
    public FormattedBlock retrieveLatestBlock(FormattedBlockService<Block> block, String lastReceived, int maxRecords) {
        
    	final long startTime = System.currentTimeMillis();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT p.pointid, timestamp, value, p.pointOffset, p.pointType, ");
        sql.append("pao.type, pao.paobjectId, dmg.meterNumber, rph.quality");
        sql.append("FROM RawPointHistory rph JOIN Point p ON rph.pointId = p.pointId");
        sql.append("JOIN YukonPaobject pao ON p.paobjectId = pao.paobjectId");
        sql.append("JOIN DeviceMeterGroup dmg ON p.paobjectId = dmg.deviceId");
        sql.append("JOIN (SELECT DISTINCT r.pointid, MAX(r.timestamp) AS rDate");
        sql.append(" FROM RawPointHistory r GROUP BY pointId) irph ");
        sql.append("ON rph.pointId = irph.pointId AND rph.timestamp = irph.rdate");
        if (!StringUtils.isBlank(lastReceived)) {
        	sql.append("WHERE dmg.meterNumber").eq(lastReceived);
        }
        sql.append("ORDER BY dmg.meterNumber, pao.paobjectId, timestamp");
        
        
        log.info("Data Collection Started for Latest Reads.");
        List<Block> blockList = new ArrayList<Block>();  
        yukonJdbcTemplate.query(sql, new FormattedBlockResultSetExtractor(maxRecords, block, blockList, true));
        
        final long stopTime = System.currentTimeMillis(); 
        log.info( (stopTime - startTime)*.001 + "secs to load RPH data for lastReceived: " + lastReceived + ". RecordSize: " + blockList.size());

        return block.createFormattedBlock(blockList);
    }
    
    @Override
    public FormattedBlock retrieveBlockByMeterNo(FormattedBlockService<Block> block, Date startDate, 
                                                Date endDate, String meterNumber, int maxRecords) {
    	final long startTime = System.currentTimeMillis();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT p.pointid, timestamp, value, p.pointOffset, p.pointType, ");
        sql.append("pao.type, pao.paobjectId, dmg.meterNumber, rph.quality");
        sql.append("FROM RawPointHistory rph JOIN Point p ON rph.pointId = p.pointId");
        sql.append("JOIN YukonPaobject pao ON p.paobjectId = pao.paobjectId");
        sql.append("JOIN DeviceMeterGroup dmg ON p.paobjectId = dmg.deviceId");
        sql.append("WHERE timestamp").gte(startDate);
        sql.append("AND timestamp").lte(endDate);
        sql.append("AND dmg.meterNumber").eq(meterNumber);
        sql.append("ORDER BY dmg.meterNumber, pao.paobjectId, timestamp");
        
        log.info("Data Collection Started: START DATE >= " + startDate + " - STOP DATE <= " + endDate);
        List<Block> blockList = new ArrayList<Block>();  
        yukonJdbcTemplate.query(sql, new FormattedBlockResultSetExtractor(maxRecords, block, blockList, false));
        
        final long stopTime = System.currentTimeMillis(); 
        log.info( (stopTime - startTime)*.001 + "secs to load RPH data for meterNumber: " + meterNumber + ". RecordSize: " + blockList.size());

        return block.createFormattedBlock(blockList);
    }

    /**
     * Inner class used to create a list of meterReads from a result set
     */
    private class FormattedBlockResultSetExtractor implements ResultSetExtractor {
        
    	private int maxRecords = 0;
    	private FormattedBlockService<Block> block;
    	private List<Block> blocks; 
    	private boolean keepTogether = false;
    	
    	public FormattedBlockResultSetExtractor(int maxRecords, FormattedBlockService<Block> block, 
    			List<Block> resultsHolder, boolean keepTogether) {
    		this.maxRecords = maxRecords;
    		this.block = block;
    		this.blocks = resultsHolder;
    		this.keepTogether = keepTogether;
    	}

        public Object extractData(ResultSet rset) throws SQLException, DataAccessException {
            
        	int lastPaobjectId = 0;
            int paobjectId = 0;
            Date prevDateTime = new Date();
            
            Block b = block.getNewBlock();
            
            while( rset.next()) {
//                int pointID = rset.getInt("pointid");	// loaded by PointValueBuilder.build()
//                Date dateTime = rset.getTimestamp("timestamp");	// loaded by PointValueBuilder.build()
//                double value = rset.getDouble("value");	// loaded by PointValueBuilder.build()
                int pointOffset = rset.getInt("pointoffset");
                PointType pointType = PointType.getForString(rset.getString("pointtype"));
                PaoType paoType = PaoType.getForDbString(rset.getString("type"));
                paobjectId = rset.getInt("paobjectid");
                String meterNumber = rset.getString("meternumber");
 
                PaoPointIdentifier paoPointIdentifier = 
                	PaoPointIdentifier.createPaoPointIdentifier(paobjectId, paoType, pointType, pointOffset);
                
                //This is only a partial Meter object load
                Meter meter = new Meter();
                meter.setDeviceId(paobjectId);
                meter.setPaoType(paoType);
                meter.setMeterNumber(meterNumber);
                
                PointValueBuilder builder = PointValueBuilder.create();
                builder.withResultSet(rset);
                builder.withType(pointType);
                PointValueQualityHolder pointData = builder.build();
                
                RichPointData richPointData = new RichPointData(pointData, paoPointIdentifier);

                //Store any previous meter readings.
                boolean newRecord = isNewRecord(richPointData, prevDateTime, lastPaobjectId);
                if (newRecord) {
                    if( b.hasData()) {
                        blocks.add(b);
                    }
                    b = block.getNewBlock();
                    
                    // Break on maxRecords, only when the paobjectId has changed to prevent breaking over one paobject.
                    if( lastPaobjectId != paobjectId && blocks.size() >= maxRecords) {
                        break;
                    }
                }

                b.populate( meter, richPointData);
                lastPaobjectId = paobjectId;
                prevDateTime.setTime(richPointData.getPointValue().getPointDataTimeStamp().getTime());
            }
            
            //Add the last meterRead object
            if (b.hasData() &&   //made it all the way through and need to add last one
            		lastPaobjectId == paobjectId) {  //but make sure we didn't exit from the break statement
                blocks.add(b);
            }

            return blocks;
        }
        
        private boolean isNewRecord(RichPointData thisPointData, Date prevDateTime, int prevPaobjectId) {

        	final int thisPaobjectId = thisPointData.getPaoPointIdentifier().getPaoIdentifier().getPaoId();
        	if (prevPaobjectId != thisPaobjectId) {	// Always new record if paobject changes
        		return true;
        	} else {
        		if (keepTogether) {	// Don't create new record (for same paobject)
        			return false;
        		} else {	// Else keep only same dateTime records together (for same paobject)
        			return thisPointData.getPointValue().getPointDataTimeStamp().after(prevDateTime);
        		}
        	}
        }
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
}