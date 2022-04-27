package com.cannontech.multispeak.dao.impl.v4;

import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.impl.MeterRowMapper;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.multispeak.block.v4.Block;
import com.cannontech.multispeak.dao.v4.FormattedBlockProcessingService;
import com.cannontech.multispeak.dao.v4.MeterReadingProcessingService;
import com.cannontech.multispeak.dao.v4.MspRawPointHistoryDao;
import com.cannontech.multispeak.data.v4.MspBlockReturnList;
import com.cannontech.multispeak.data.v4.MspMeterReadingReturnList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MspRawPointHistoryDaoImpl implements MspRawPointHistoryDao {
    private final Logger log = YukonLogManager.getLogger(MspRawPointHistoryDaoImpl.class);

    @Autowired private MeterReadingProcessingService meterReadingProcessingService;
    @Autowired private MeterRowMapper meterRowMapper;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public MspMeterReadingReturnList retrieveMeterReading(ReadBy readBy, String readByValue, Date startDate,
            Date endDate, String lastReceived, int maxRecords) {

        List<YukonMeter> meters = getPaoList(readBy, readByValue, lastReceived, maxRecords);

        final Date timerStart = new Date();
        EnumMap<BuiltInAttribute, ListMultimap<PaoIdentifier, PointValueQualityHolder>> resultsPerAttribute = Maps
                .newEnumMap(BuiltInAttribute.class);

        int estimatedSize = 0;

        EnumSet<BuiltInAttribute> attributesToLoad = EnumSet.of(BuiltInAttribute.USAGE, BuiltInAttribute.PEAK_DEMAND);
        Range<Date> dateRange = new Range<Date>(startDate, true, endDate, true);
        // load up results for each attribute
        for (BuiltInAttribute attribute : attributesToLoad) {
            ListMultimap<PaoIdentifier, PointValueQualityHolder> resultsForAttribute = rawPointHistoryDao.getAttributeData(meters,
                    attribute, false,
                    dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), Order.FORWARD, null);

            resultsPerAttribute.put(attribute, resultsForAttribute);
            estimatedSize += resultsForAttribute.size();
        }

        // build the actual MeterReading objects in the order they are to be output getPaoList returns the meters in
        List<MeterReading> meterReadings = Lists.newArrayListWithExpectedSize(estimatedSize);

        // loop over meters, results will be returned in whatever order getPaoList returns the meters in
        for (YukonMeter meter : meters) {
            for (BuiltInAttribute attribute : attributesToLoad) {
                List<PointValueQualityHolder> rawValues = resultsPerAttribute.get(attribute).removeAll(meter.getPaoIdentifier()); // remove  to keep our
                                                                                                                                  // memory consumption
                                                                                                                                 // somewhat in check

                for (PointValueQualityHolder pointValueQualityHolder : rawValues) {
                    MeterReading meterReading = meterReadingProcessingService.createMeterReading(meter);
                    meterReadingProcessingService.updateMeterReading(meterReading, attribute, pointValueQualityHolder);
                    meterReadings.add(meterReading);
                }
            }
        }

        MspMeterReadingReturnList mspMeterReadingReturn = new MspMeterReadingReturnList();
        mspMeterReadingReturn.setMeterReading(meterReadings);
        mspMeterReadingReturn.setReturnFields(meters, maxRecords);

        log.debug("Retrieved " + meterReadings.size() + " MeterReading. (" + (new Date().getTime() - timerStart.getTime()) * .001
                + " secs)");

        return mspMeterReadingReturn;

    } 
    
    @Override
    public MspMeterReadingReturnList retrieveLatestMeterReading(ReadBy readBy, String readByValue, String lastReceived, int maxRecords) {

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

        List<MeterReading> meterReadings = Lists.newArrayListWithExpectedSize(estimatedSize);
        // loop over meters, results will be returned in whatever order getPaoList returns the meters in
        // attempt to group all attributes for one meter together, because we know we only have one pointValue per meter per attribute. 
        for (YukonMeter meter : meters) {

            MeterReading meterReading = meterReadingProcessingService.createMeterReading(meter);
            boolean hasReadings = false; 
            
            for (BuiltInAttribute attribute : attributesToLoad) { 
                
                PointValueQualityHolder pointValueQualityHolder = 
                    resultsPerAttribute.get(attribute).remove(meter.getPaoIdentifier()); // remove to keep our memory consumption somewhat in check
                
                if (pointValueQualityHolder != null) {
                    meterReadingProcessingService.updateMeterReading(meterReading, attribute, pointValueQualityHolder);
                    hasReadings = true;
                }
            }
            
            if (hasReadings) {  // only add to the return list if we have actual readings.
                meterReadings.add(meterReading);
            }
        }

        MspMeterReadingReturnList mspMeterReadingReturn = new MspMeterReadingReturnList();
        mspMeterReadingReturn.setMeterReading(meterReadings);
        mspMeterReadingReturn.setReturnFields(meters, maxRecords);
        
        log.debug("Retrieved " + meterReadings.size() + " Latest MeterReads. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");
        
        return mspMeterReadingReturn;
    }

    /**
     * Helper method to retrieve a limited set of Meters
     * 
     * @param readBy       - readings collected for ReadBy value (ReadBy = NONE then ReadBy option not used).
     * @param readByValue  corresponding value to be used with readBy
     * @param lastReceived - Results are retrieved for meterNumber > lastReceived. LastReceived == null means start from
     *                     beginning.
     * @param maxRecords   - maximum number of meters to return.
     * @return
     */
    private List<YukonMeter> getPaoList(ReadBy readBy, String readByValue, String lastReceived,
            int maxRecords) {
        final Date timerStart = new Date();

        // get the paos we want, using readBy, readByValue, and lastReceived
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(meterRowMapper.getSql());
        if (readBy == ReadBy.METER_NUMBER) {
            sql.append("WHERE dmg.meterNumber").eq(readByValue);
        } else if (StringUtils.isNotBlank(lastReceived)) {
            sql.append("WHERE dmg.meterNumber").gt(lastReceived);
        }
        sql.append("ORDER BY dmg.meterNumber");

        List<YukonMeter> result = yukonJdbcTemplate.queryForLimitedResults(sql, meterRowMapper, maxRecords);

        log.debug("Retrieved " + result.size() + " paos to process. (" + (new Date().getTime() - timerStart.getTime()) * .001
                + " secs)");
        return result;
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
        Range<Date> dateRange = new Range<Date>(startDate, true, endDate, true);
        // load up results for each attribute
        for (BuiltInAttribute attribute : attributesToLoad) {

            ListMultimap<PaoIdentifier, PointValueQualityHolder> resultsForAttribute;
            resultsForAttribute =
                rawPointHistoryDao.getAttributeData(meters, attribute, false,
                    dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), Order.FORWARD, null);

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
}
