package com.cannontech.multispeak.dao.impl;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.impl.MeterRowMapper;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.dao.FormattedBlockProcessingService;
import com.cannontech.multispeak.dao.MeterReadProcessingService;
import com.cannontech.multispeak.dao.MspRawPointHistoryDao;
import com.cannontech.multispeak.data.MspBlockReturnList;
import com.cannontech.multispeak.data.MspMeterReadReturnList;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.multispeak.deploy.service.QualityDescription;
import com.cannontech.multispeak.deploy.service.ScadaAnalog;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MspRawPointHistoryDaoImpl implements MspRawPointHistoryDao
{
	private final Logger log = YukonLogManager.getLogger(MspRawPointHistoryDaoImpl.class);
	
	@Autowired private YukonJdbcTemplate yukonJdbcTemplate;
	@Autowired private RawPointHistoryDao rawPointHistoryDao;
	@Autowired private MeterReadProcessingService meterReadProcessingService;
    @Autowired private MeterRowMapper meterRowMapper;
    
	@Override
	public MspMeterReadReturnList retrieveMeterReads(ReadBy readBy, String readByValue, Date startDate, 
	                                      Date endDate, String lastReceived, int maxRecords) {

	    List<YukonMeter> meters = getPaoList(readBy, readByValue, lastReceived, maxRecords);
	    
	    final Date timerStart = new Date();
	    EnumMap<BuiltInAttribute, ListMultimap<PaoIdentifier, PointValueQualityHolder>> resultsPerAttribute = Maps.newEnumMap(BuiltInAttribute.class);

	    int estimatedSize = 0;

	    EnumSet<BuiltInAttribute> attributesToLoad = EnumSet.of(BuiltInAttribute.USAGE, BuiltInAttribute.PEAK_DEMAND);
	    // load up results for each attribute
	    for (BuiltInAttribute attribute : attributesToLoad) {

	        ListMultimap<PaoIdentifier, PointValueQualityHolder> resultsForAttribute = 
	            rawPointHistoryDao.getAttributeData(meters, attribute, startDate, endDate, 
	                                                false, Clusivity.INCLUSIVE_INCLUSIVE, Order.FORWARD, null);

	        resultsPerAttribute.put(attribute, resultsForAttribute);
	        estimatedSize += resultsForAttribute.size();
	    }

	    // build the actual MeterRead objects in the order they are to be output getPaoList returns the meters in
	    List<MeterRead> meterReads = Lists.newArrayListWithExpectedSize(estimatedSize);
	    

	    // loop over meters, results will be returned in whatever order getPaoList returns the meters in
        for (YukonMeter meter : meters) { 
            for (BuiltInAttribute attribute : attributesToLoad) { 
                List<PointValueQualityHolder> rawValues =  
                    resultsPerAttribute.get(attribute).removeAll(meter.getPaoIdentifier()); // remove to keep our memory consumption somewhat in check 
                for (PointValueQualityHolder pointValueQualityHolder : rawValues) { 
                    MeterRead meterRead = meterReadProcessingService.createMeterRead(meter); 
                    meterReadProcessingService.updateMeterRead(meterRead, attribute, pointValueQualityHolder); 
                    meterReads.add(meterRead); 
                } 
            } 
        }

        MspMeterReadReturnList mspMeterReadReturn = new MspMeterReadReturnList();
        mspMeterReadReturn.setMeterReads(meterReads);
        mspMeterReadReturn.setReturnFields(meters, maxRecords);

        log.debug("Retrieved " + meterReads.size() + " MeterReads. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");
        
	    return mspMeterReadReturn;
	}

	@Override
    public MspMeterReadReturnList retrieveLatestMeterReads(ReadBy readBy, String readByValue, String lastReceived, int maxRecords) {

        List<YukonMeter> meters = getPaoList(readBy, readByValue, lastReceived, maxRecords);
        
        final Date timerStart = new Date();
        
        EnumMap<BuiltInAttribute, Map<PaoIdentifier, PointValueQualityHolder>> resultsPerAttribute = Maps.newEnumMap(BuiltInAttribute.class);

        int estimatedSize = 0;

        EnumSet<BuiltInAttribute> attributesToLoad = EnumSet.of(BuiltInAttribute.USAGE, BuiltInAttribute.PEAK_DEMAND);
        // load up results for each attribute
        for (BuiltInAttribute attribute : attributesToLoad) {
            Map<PaoIdentifier, PointValueQualityHolder> resultsForAttribute = rawPointHistoryDao.getSingleAttributeData(meters, attribute, false, null);
            resultsPerAttribute.put(attribute, resultsForAttribute);
            estimatedSize += resultsForAttribute.size();
        }

        List<MeterRead> meterReads = Lists.newArrayListWithExpectedSize(estimatedSize);
        
        // loop over meters, results will be returned in whatever order getPaoList returns the meters in
        // attempt to group all attributes for one meter together, because we know we only have one pointValue per meter per attribute. 
        for (YukonMeter meter : meters) {

            MeterRead meterRead = meterReadProcessingService.createMeterRead(meter);
            boolean hasReadings = false;            
            
            for (BuiltInAttribute attribute : attributesToLoad) { 
                
                PointValueQualityHolder pointValueQualityHolder = 
                    resultsPerAttribute.get(attribute).remove(meter.getPaoIdentifier()); // remove to keep our memory consumption somewhat in check
                
                if (pointValueQualityHolder != null) {
                    meterReadProcessingService.updateMeterRead(meterRead, attribute, pointValueQualityHolder);
                    hasReadings = true;
                }
            }
            
            if (hasReadings) {  // only add to the return list if we have actual readings.
                meterReads.add(meterRead);
            }
        }

        MspMeterReadReturnList mspMeterReadReturn = new MspMeterReadReturnList();
        mspMeterReadReturn.setMeterReads(meterReads);
        mspMeterReadReturn.setReturnFields(meters, maxRecords);
        
        log.debug("Retrieved " + meterReads.size() + " Latest MeterReads. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");
        
        return mspMeterReadReturn;
    }
    
    @Override
    public MspBlockReturnList retrieveBlock(ReadBy readBy, String readByValue, 
                                     FormattedBlockProcessingService<Block> blockProcessingService,
                                     Date startDate, Date endDate, String lastReceived, int maxRecords) {

        List<YukonMeter> meters = getPaoList(readBy, readByValue, lastReceived, maxRecords);

        final Date timerStart = new Date();
        
        EnumMap<BuiltInAttribute, ListMultimap<PaoIdentifier, PointValueQualityHolder>> resultsPerAttribute = Maps.newEnumMap(BuiltInAttribute.class);

        int estimatedSize = 0;

        EnumSet<BuiltInAttribute> attributesToLoad = blockProcessingService.getAttributeSet();
        // load up results for each attribute
        for (BuiltInAttribute attribute : attributesToLoad) {

            ListMultimap<PaoIdentifier, PointValueQualityHolder> resultsForAttribute;
            resultsForAttribute = rawPointHistoryDao.getAttributeData(meters, attribute, startDate, endDate,
                                                                       false, Clusivity.INCLUSIVE_INCLUSIVE, Order.FORWARD, null);

            resultsPerAttribute.put(attribute, resultsForAttribute);
            estimatedSize += resultsForAttribute.size();
        }

        List<Block> blocks = Lists.newArrayListWithExpectedSize(estimatedSize);
         
        // loop over meters, results will be returned in whatever order getPaoList returns the meters in
        // results will be one block for every reading, no grouping of similar timstamped data into one block.
        // this is a change from how things previously worked where we made a best guess to "block" data with like timestamps.
        for (YukonMeter meter : meters) { 

            for (BuiltInAttribute attribute : attributesToLoad) {
                List<PointValueQualityHolder> rawValues =
                    resultsPerAttribute.get(attribute).removeAll(meter.getPaoIdentifier()); // remove to keep our memory consumption somewhat in check 
     
                for (PointValueQualityHolder pointValueQualityHolder : rawValues) {
                    Block block = blockProcessingService.createBlock(meter); 
                    blockProcessingService.updateFormattedBlock(block, attribute, pointValueQualityHolder); 
                    blocks.add(block); 
                } 
            } 
        }
        
        MspBlockReturnList mspBlockReturn = new MspBlockReturnList();
        mspBlockReturn.setBlocks(blocks);
        mspBlockReturn.setReturnFields(meters, maxRecords);
        
        log.debug("Retrieved " + blocks.size() + " Blocks. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");
        return mspBlockReturn;
    }

    @Override
    public MspBlockReturnList retrieveLatestBlock(FormattedBlockProcessingService<Block> blockProcessingService, String lastReceived, int maxRecords) {

        List<YukonMeter> meters = getPaoList(ReadBy.NONE, null, lastReceived, maxRecords);
        
        final Date timerStart = new Date();

        EnumMap<BuiltInAttribute, Map<PaoIdentifier, PointValueQualityHolder>> resultsPerAttribute = Maps.newEnumMap(BuiltInAttribute.class);

        int estimatedSize = 0;

        EnumSet<BuiltInAttribute> attributesToLoad = blockProcessingService.getAttributeSet();

        // load up results for each attribute
        for (BuiltInAttribute attribute : attributesToLoad) {

            Map<PaoIdentifier, PointValueQualityHolder> resultsForAttribute = 
                rawPointHistoryDao.getSingleAttributeData(meters, attribute, false, null);

            resultsPerAttribute.put(attribute, resultsForAttribute);
            estimatedSize += resultsForAttribute.size();
        }

        List<Block> blocks = Lists.newArrayListWithExpectedSize(estimatedSize);

        // loop over meters, results will be returned in whatever order getPaoList returns the meters in
        // attempt to "block" all attributes for one meter together, because we know we only have one pointValue per meter per attribute.
        for (YukonMeter meter : meters) {
            Block block = blockProcessingService.createBlock(meter);
            for (BuiltInAttribute attribute : attributesToLoad) { 
                PointValueQualityHolder rawValue = resultsPerAttribute.get(attribute).remove(meter.getPaoIdentifier());
                if (rawValue != null) {
                    blockProcessingService.updateFormattedBlock(block, attribute, rawValue);
                }
            }
            if (block.hasData()) {
                blocks.add(block);
            }
        }

        MspBlockReturnList mspBlockReturn = new MspBlockReturnList();
        mspBlockReturn.setBlocks(blocks);
        mspBlockReturn.setReturnFields(meters, maxRecords);
        
        log.debug("Retrieved " + blocks.size() + " Latest Blocks. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");
        return mspBlockReturn;
    }
 
    /**
     * Helper method to retrieve a limited set of Meters
     * @param readBy - readings collected for ReadBy value (ReadBy = NONE then ReadBy option not used).
     * @param readByValue corresponding value to be used with readBy
     * @param lastReceived - Results are retrieved for meterNumber > lastRecieved. LastReceived == null means start from beginning.
     * @param maxRecords - maximum number of meters to return.
     * @return
     */
    private List<YukonMeter> getPaoList(ReadBy readBy, String readByValue, String lastReceived,
                                           int maxRecords) {
        final Date timerStart = new Date();
        
        // get the paos we want, using readBy, readByValue, and lastReceived
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(meterRowMapper.getSql());
        if (readBy == ReadBy.METER_NUMBER) {
            sql.append(  "WHERE dmg.meterNumber").eq(readByValue);
        } else if (StringUtils.isNotBlank(lastReceived) ){
            sql.append(  "WHERE dmg.meterNumber").gt(lastReceived);
        }
        sql.append("ORDER BY dmg.meterNumber"); 

        List<YukonMeter> result = yukonJdbcTemplate.queryForLimitedResults(sql, meterRowMapper, maxRecords);

        log.debug("Retrieved " + result.size() + " paos to process. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");
        return result;
    }

    @Override
    public List<ScadaAnalog> getAllSCADAAnalogData() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypao.PAOName, t.PointName, t.PointOffset, rph.Value, rph.Timestamp");
        sql.append("from RawPointHistory rph");
        sql.append("join (");
        sql.append("    select p.PointId, p.PAObjectID, p.PointName, p.PointOffset, max(rph.Timestamp) as MostRecent ");
        sql.append("    from RawPointHistory rph");
        sql.append("    join Point p on p.PointId = rph.PointId");
        sql.append("    where p.PointId in (");
        sql.append("        select PointId");
        sql.append("        from Point");
        sql.append("        where PointType = 'Analog'");
        sql.append("          and PAObjectID in (select PAObjectID");
        sql.append("                             from YukonPAObject");
        sql.append("                             where Type IN ('LM SEP PROGRAM', 'LM DIRECT PROGRAM')))");
        sql.append("    group by p.PointId, p.PAObjectID, p.PointName, p.PointOffset) t on (rph.PointId = t.PointId and rph.Timestamp = t.MostRecent)");
        sql.append("join YukonPAObject ypao on ypao.PAObjectID = t.PAObjectID");
        sql.append("order by PaoName, PointOffset");
        
        return yukonJdbcTemplate.query(sql, new YukonRowMapper<ScadaAnalog>() {
            @Override
            public ScadaAnalog mapRow(YukonResultSet rs) throws SQLException {
                ScadaAnalog scadaAnalog = new ScadaAnalog();

                // objectId identifier is limited to 52 characters by customer system, so take first 
                // 50 characters of pao name plus '.#' where # is the point offset, 1-4.
                String paoName = rs.getString("PAOName");
                String objectId = (paoName.length() > 50 ? paoName.substring(0, 50) : paoName) + "."
                        + rs.getInt("PointOffset");
                String comments = rs.getString("PAOName") + " - " + rs.getString("PointName");
                scadaAnalog.setObjectID(objectId);
                scadaAnalog.setComments(comments);
                scadaAnalog.setQuality(QualityDescription.Measured);  // Corresponds to PointQuality.NORMAL.
                Calendar cal = new GregorianCalendar();
                cal.setTime(rs.getDate("Timestamp"));
                scadaAnalog.setTimeStamp(cal);
                scadaAnalog.setValue(rs.getFloat("Value"));
                return scadaAnalog;
            }
        });
    }
}