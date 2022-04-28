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
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.msp.beans.v4.ScadaAnalog;
import com.cannontech.multispeak.client.MspRawPointHistoryHelper;
import com.cannontech.multispeak.dao.v4.MeterReadingProcessingService;
import com.cannontech.multispeak.dao.v4.MspRawPointHistoryDao;
import com.cannontech.multispeak.data.v4.MspMeterReadingReturnList;
import com.cannontech.multispeak.data.v4.MspScadaAnalogReturnList;
import com.google.common.collect.BiMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MspRawPointHistoryDaoImpl implements MspRawPointHistoryDao {
    private final Logger log = YukonLogManager.getLogger(MspRawPointHistoryDaoImpl.class);

    @Autowired private AttributeService attributeService;
    @Autowired private MspRawPointHistoryHelper mspRawPointHistoryHelper;
    @Autowired private MeterReadingProcessingService meterReadingProcessingService;
    @Autowired private ScadaAnalogProcessingServiceImpl scadaAnalogProcessingServiceImpl;
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
    public MspScadaAnalogReturnList retrieveLatestScadaAnalogs(LiteYukonUser user) {

        final Date timerStart = new Date();

        List<LiteYukonPAObject> programs = mspRawPointHistoryHelper.getAuthorizedProgramsList(user);
        EnumSet<BuiltInAttribute> attributesToLoad = EnumSet.of(BuiltInAttribute.CONNECTED_LOAD,
                BuiltInAttribute.DIVERSIFIED_LOAD,
                BuiltInAttribute.MAX_LOAD_REDUCTION,
                BuiltInAttribute.AVAILABLE_LOAD_REDUCTION);
        List<ScadaAnalog> scadaAnalogs = Lists.newArrayListWithExpectedSize(4 * programs.size());

        // loop over programs, results will be returned in whatever order getProgramList returns the programs in
        for (BuiltInAttribute attribute : attributesToLoad) {
            BiMap<PaoIdentifier, LitePoint> programToPoint = attributeService.getPoints(programs, attribute);
            Map<PaoIdentifier, PointValueQualityHolder> resultsForAttribute = rawPointHistoryDao.getSingleAttributeData(programs,
                    attribute, false, null);
            for (LiteYukonPAObject program : programs) {
                LitePoint litePoint = programToPoint.get(program.getPaoIdentifier());
                PointValueQualityHolder pointValueQualityHolder = resultsForAttribute.remove(program.getPaoIdentifier());

                if (pointValueQualityHolder != null) {
                    ScadaAnalog scadaAnalog = scadaAnalogProcessingServiceImpl.createScadaAnalog(program, litePoint,
                            pointValueQualityHolder);
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

    @Override
    public MspMeterReadingReturnList retrieveLatestMeterReading(ReadBy readBy, String readByValue, String lastReceived,
            int maxRecords) {

        List<YukonMeter> meters = getPaoList(readBy, readByValue, lastReceived, maxRecords);

        final Date timerStart = new Date();

        EnumMap<BuiltInAttribute, Map<PaoIdentifier, PointValueQualityHolder>> resultsPerAttribute = Maps
                .newEnumMap(BuiltInAttribute.class);

        int estimatedSize = 0;

        EnumSet<BuiltInAttribute> attributesToLoad = EnumSet.of(BuiltInAttribute.USAGE, BuiltInAttribute.PEAK_DEMAND);
        // load up results for each attribute
        for (BuiltInAttribute attribute : attributesToLoad) {
            Map<PaoIdentifier, PointValueQualityHolder> resultsForAttribute = rawPointHistoryDao.getSingleAttributeData(meters,
                    attribute, false, null);
            resultsPerAttribute.put(attribute, resultsForAttribute);
            estimatedSize += resultsForAttribute.size();
        }

        List<MeterReading> meterReadings = Lists.newArrayListWithExpectedSize(estimatedSize);
        // loop over meters, results will be returned in whatever order getPaoList returns the meters in
        // attempt to group all attributes for one meter together, because we know we only have one pointValue per meter per
        // attribute.
        for (YukonMeter meter : meters) {

            MeterReading meterReading = meterReadingProcessingService.createMeterReading(meter);
            boolean hasReadings = false;

            for (BuiltInAttribute attribute : attributesToLoad) {

                PointValueQualityHolder pointValueQualityHolder = resultsPerAttribute.get(attribute)
                        .remove(meter.getPaoIdentifier()); // remove to keep our memory consumption somewhat in check

                if (pointValueQualityHolder != null) {
                    meterReadingProcessingService.updateMeterReading(meterReading, attribute, pointValueQualityHolder);
                    hasReadings = true;
                }
            }

            if (hasReadings) { // only add to the return list if we have actual readings.
                meterReadings.add(meterReading);
            }
        }

        MspMeterReadingReturnList mspMeterReadingReturn = new MspMeterReadingReturnList();
        mspMeterReadingReturn.setMeterReading(meterReadings);
        mspMeterReadingReturn.setReturnFields(meters, maxRecords);

        log.debug("Retrieved " + meterReadings.size() + " Latest MeterReads. ("
                + (new Date().getTime() - timerStart.getTime()) * .001 + " secs)");

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
}
