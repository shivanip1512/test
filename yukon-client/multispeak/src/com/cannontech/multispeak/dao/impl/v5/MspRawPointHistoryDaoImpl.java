package com.cannontech.multispeak.dao.impl.v5;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.dao.impl.MeterRowMapper;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.msp.beans.v5.multispeak.MeterReading;
import com.cannontech.msp.beans.v5.multispeak.SCADAAnalog;
import com.cannontech.multispeak.block.v5.Block;
import com.cannontech.multispeak.dao.v5.FormattedBlockProcessingService;
import com.cannontech.multispeak.dao.v5.MeterReadProcessingService;
import com.cannontech.multispeak.dao.v5.MspRawPointHistoryDao;
import com.cannontech.multispeak.data.v5.MspBlockReturnList;
import com.cannontech.multispeak.data.v5.MspMeterReadReturnList;
import com.cannontech.multispeak.data.v5.MspScadaAnalogReturnList;
import com.google.common.collect.BiMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MspRawPointHistoryDaoImpl implements MspRawPointHistoryDao
{
	private final Logger log = YukonLogManager.getLogger(MspRawPointHistoryDaoImpl.class);
	
    @Autowired private AttributeService attributeService;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private ScadaAnalogProcessingServiceImpl scadaAnalogProcessingServiceImpl;
    @Autowired private MeterDao meterDao;
    @Autowired private MeterReadProcessingService meterReadProcessingService;
    @Autowired private MeterRowMapper meterRowMapper;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public MspScadaAnalogReturnList retrieveLatestScadaAnalogs(LiteYukonUser user) {

        final Date timerStart = new Date();

        List<LiteYukonPAObject> programs = getAuthorizedProgramsList(user);
        EnumSet<BuiltInAttribute> attributesToLoad =
            EnumSet.of(BuiltInAttribute.CONNECTED_LOAD, BuiltInAttribute.DIVERSIFIED_LOAD,
                BuiltInAttribute.MAX_LOAD_REDUCTION, BuiltInAttribute.AVAILABLE_LOAD_REDUCTION);
        List<SCADAAnalog> scadaAnalogs = Lists.newArrayListWithExpectedSize(4 * programs.size());

        // loop over programs, results will be returned in whatever order getProgramList returns the programs
        // in
        for (BuiltInAttribute attribute : attributesToLoad) {
            BiMap<PaoIdentifier, LitePoint> programToPoint = attributeService.getPoints(programs, attribute);
            Map<PaoIdentifier, PointValueQualityHolder> resultsForAttribute =
                rawPointHistoryDao.getSingleAttributeData(programs, attribute, false, null);
            for (LiteYukonPAObject program : programs) {
                LitePoint litePoint = programToPoint.get(program.getPaoIdentifier());
                PointValueQualityHolder pointValueQualityHolder =
                    resultsForAttribute.remove(program.getPaoIdentifier());

                if (pointValueQualityHolder != null) {
                    SCADAAnalog scadaAnalog =
                        scadaAnalogProcessingServiceImpl.createScadaAnalog(program, litePoint, pointValueQualityHolder);
                    scadaAnalogs.add(scadaAnalog);
                }
            }
        }

        MspScadaAnalogReturnList mspScadaAnalogs = new MspScadaAnalogReturnList();
        mspScadaAnalogs.setScadaAnalogs(scadaAnalogs);
        mspScadaAnalogs.setReturnFields(scadaAnalogs, scadaAnalogs.size() + 1);

        log.debug("Retrieved " + scadaAnalogs.size() + " Latest ScadaAnalogs. ("
            + (new Date().getTime() - timerStart.getTime()) * .001 + " secs)");

        return mspScadaAnalogs;
    }

    /**
     * Returns a list of paObjects for PaoTag.LM_PROGRAM that user has permission to access.
     * @return
     */
    private List<LiteYukonPAObject> getAuthorizedProgramsList(LiteYukonUser user) {
        Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_PROGRAM);
        List<LiteYukonPAObject> programs = Lists.newArrayList();
        for (PaoType paoType : paoTypes) {
            List<LiteYukonPAObject> toFilter = paoDao.getLiteYukonPAObjectByType(paoType);
            programs.addAll(paoAuthorizationService.filterAuthorized(user, toFilter, Permission.LM_VISIBLE));
        }
        return programs;
    }
    
    @Override
    public MspMeterReadReturnList retrieveMeterReads(ReadBy readBy, List<String> readByValues, Date startDate,
            Date endDate, String lastReceived, int maxRecords) {

        List<YukonMeter> meters = getPaoList(readBy, readByValues, lastReceived, maxRecords);

        final Date timerStart = new Date();
        EnumMap<BuiltInAttribute, ListMultimap<PaoIdentifier, PointValueQualityHolder>> resultsPerAttribute =
            Maps.newEnumMap(BuiltInAttribute.class);

        int estimatedSize = 0;

        EnumSet<BuiltInAttribute> attributesToLoad = EnumSet.of(BuiltInAttribute.USAGE, BuiltInAttribute.PEAK_DEMAND);
        Range<Date> dateRange = new Range<Date>(startDate, true, endDate, true);
        // load up results for each attribute
        for (BuiltInAttribute attribute : attributesToLoad) {
            ListMultimap<PaoIdentifier, PointValueQualityHolder> resultsForAttribute =
                rawPointHistoryDao.getAttributeData(meters, attribute, false,
                    dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), Order.FORWARD, null);

            resultsPerAttribute.put(attribute, resultsForAttribute);
            estimatedSize += resultsForAttribute.size();
        }

        // build the actual MeterReading objects in the order they are to be output getPaoList returns the
        // meters in
        List<MeterReading> meterReads = Lists.newArrayListWithExpectedSize(estimatedSize);

        // loop over meters, results will be returned in whatever order getPaoList returns the meters in
        for (YukonMeter meter : meters) {
            for (BuiltInAttribute attribute : attributesToLoad) {
                List<PointValueQualityHolder> rawValues =
                    resultsPerAttribute.get(attribute).removeAll(meter.getPaoIdentifier()); // remove to keep
                                                                                            // our memory
                                                                                            // consumption
                                                                                            // somewhat in
                                                                                            // check
                for (PointValueQualityHolder pointValueQualityHolder : rawValues) {
                    MeterReading meterReading = meterReadProcessingService.createMeterRead(meter);
                    meterReadProcessingService.updateMeterRead(meterReading, attribute, pointValueQualityHolder);
                    meterReads.add(meterReading);
                }
            }
        }

        MspMeterReadReturnList mspMeterReadReturn = new MspMeterReadReturnList();
        mspMeterReadReturn.setMeterReads(meterReads);
        mspMeterReadReturn.setReturnFields(meters, maxRecords);

        log.debug("Retrieved " + meterReads.size() + " MeterReads. (" + (new Date().getTime() - timerStart.getTime())
            * .001 + " secs)");

        return mspMeterReadReturn;
    }

    @Override
    public MspMeterReadReturnList retrieveLatestMeterReads(ReadBy readBy, List<String> readByValues, String lastReceived,
            int maxRecords) {

        List<YukonMeter> meters = getPaoList(readBy, readByValues, lastReceived, maxRecords);

        final Date timerStart = new Date();

        EnumMap<BuiltInAttribute, Map<PaoIdentifier, PointValueQualityHolder>> resultsPerAttribute =
            Maps.newEnumMap(BuiltInAttribute.class);

        int estimatedSize = 0;

        EnumSet<BuiltInAttribute> attributesToLoad = EnumSet.of(BuiltInAttribute.USAGE, BuiltInAttribute.PEAK_DEMAND);
        // load up results for each attribute
        for (BuiltInAttribute attribute : attributesToLoad) {
            Map<PaoIdentifier, PointValueQualityHolder> resultsForAttribute =
                rawPointHistoryDao.getSingleAttributeData(meters, attribute, false, null);
            resultsPerAttribute.put(attribute, resultsForAttribute);
            estimatedSize += resultsForAttribute.size();
        }

        List<MeterReading> meterReads = Lists.newArrayListWithExpectedSize(estimatedSize);

        // loop over meters, results will be returned in whatever order getPaoList returns the meters in
        // attempt to group all attributes for one meter together, because we know we only have one pointValue
        // per meter per attribute.
        for (YukonMeter meter : meters) {
            // TODO In version 3.0 we had a check for SEDC vendor, in version 5 we have removed the check for SEDC
            MeterReading meterRead = meterReadProcessingService.createMeterRead(meter);
            boolean hasReadings = false;

            for (BuiltInAttribute attribute : attributesToLoad) {

                PointValueQualityHolder pointValueQualityHolder =
                    resultsPerAttribute.get(attribute).remove(meter.getPaoIdentifier()); // remove to keep our
                                                                                         // memory consumption
                                                                                         // somewhat in check

                if (pointValueQualityHolder != null) {
                    meterReadProcessingService.updateMeterRead(meterRead, attribute, pointValueQualityHolder);
                    hasReadings = true;
                }
            }

            if (hasReadings) { // only add to the return list if we have actual readings.
                meterReads.add(meterRead);
            }
        }

        MspMeterReadReturnList mspMeterReadReturn = new MspMeterReadReturnList();
        mspMeterReadReturn.setMeterReads(meterReads);
        mspMeterReadReturn.setReturnFields(meters, maxRecords);

        log.debug("Retrieved " + meterReads.size() + " Latest MeterReads. ("
            + (new Date().getTime() - timerStart.getTime()) * .001 + " secs)");

        return mspMeterReadReturn;
    }
    

    @Override
    public MspBlockReturnList retrieveLatestBlock(ReadBy readBy,
            FormattedBlockProcessingService<Block> blockProcessingService, List<String> readByValues, String lastReceived,
            int maxRecords) {

        List<YukonMeter> meters = getPaoList(readBy, readByValues, lastReceived, maxRecords);

        final Date timerStart = new Date();

        EnumMap<BuiltInAttribute, Map<PaoIdentifier, PointValueQualityHolder>> resultsPerAttribute =
            Maps.newEnumMap(BuiltInAttribute.class);

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
        // attempt to "block" all attributes for one meter together, because we know we only have one
        // pointValue per meter per attribute.
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

        log.debug("Retrieved " + blocks.size() + " Latest Blocks. (" + (new Date().getTime() - timerStart.getTime())
            * .001 + " secs)");
        return mspBlockReturn;
    }

    @Override
    public MspBlockReturnList retrieveBlock(ReadBy readBy, List<String> readByValues,
            FormattedBlockProcessingService<Block> blockProcessingService, Date startDate, Date endDate,
            String lastReceived, int maxRecords) {

        List<YukonMeter> meters = getPaoList(readBy, readByValues, lastReceived, maxRecords);

        final Date timerStart = new Date();

        EnumMap<BuiltInAttribute, ListMultimap<PaoIdentifier, PointValueQualityHolder>> resultsPerAttribute =
            Maps.newEnumMap(BuiltInAttribute.class);

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
        // this is a change from how things previously worked where we made a best guess to "block" data with
        // like timestamps.
        for (YukonMeter meter : meters) {

            for (BuiltInAttribute attribute : attributesToLoad) {
                List<PointValueQualityHolder> rawValues =
                    resultsPerAttribute.get(attribute).removeAll(meter.getPaoIdentifier()); // remove to keep our memory
                                                                                            // consumption somewhat in check

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

        log.debug("Retrieved " + blocks.size() + " Blocks. (" + (new Date().getTime() - timerStart.getTime()) * .001
            + " secs)");
        return mspBlockReturn;
    }

    /**
     * Helper method to retrieve a limited set of Meters
     * 
     * @param readBy - readings collected for ReadBy value (ReadBy = NONE then ReadBy option not used).
     * @param readByValues corresponding values to be used with readBy (this parameter is used with METER_NUMBERS)
     * @param lastReceived - Results are retrieved for meterNumber > lastReceived. LastReceived == null means start from beginning.
     * @param maxRecords - This parameter is used with lastReceived (maximum number of meters to return)
     * @return
     */
    private List<YukonMeter> getPaoList(ReadBy readBy, List<String> readByValues, String lastReceived, int maxRecords) {
        List<YukonMeter> result = new ArrayList<YukonMeter>();
        final Date timerStart = new Date();

        if (readBy == ReadBy.METER_NUMBERS) {
            List<YukonMeter> supportedMeters = meterDao.getMetersForMeterNumbers(readByValues);
            result.addAll(supportedMeters);
        } else {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(meterRowMapper.getSql());
            if (lastReceived != null) {
                sql.append("WHERE dmg.meterNumber").gt(lastReceived);
            }
            sql.append("ORDER BY dmg.meterNumber");
            result = yukonJdbcTemplate.queryForLimitedResults(sql, meterRowMapper, maxRecords);
        }

        log.debug("Retrieved " + result.size() + " paos to process. (" + (new Date().getTime() - timerStart.getTime())
            * .001 + " secs)");
        return result;
    }

}
