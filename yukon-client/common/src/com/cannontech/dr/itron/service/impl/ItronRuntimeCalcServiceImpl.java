package com.cannontech.dr.itron.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.dr.itron.ItronDataEventType;
import com.cannontech.dr.itron.service.ItronRuntimeCalcService;
import com.cannontech.dr.service.RuntimeCalcService;
import com.cannontech.dr.service.RelayLogInterval;
import com.cannontech.dr.service.impl.DatedRuntimeStatus;
import com.cannontech.dr.service.impl.DatedShedtimeStatus;
import com.cannontech.dr.service.impl.DatedStatus;
import com.cannontech.dr.service.impl.RuntimeStatus;
import com.cannontech.dr.service.impl.ShedtimeStatus;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ItronRuntimeCalcServiceImpl implements ItronRuntimeCalcService {
    private static final Logger log = YukonLogManager.getLogger(ItronRuntimeCalcServiceImpl.class);
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PointDao pointDao;
    @Autowired private AttributeService attributeService;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private RuntimeCalcService runtimeCalcService;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private DynamicLcrCommunicationsDao dynamicLcrCommunicationsDao;
    
    @PostConstruct
    public void init() {
        //Schedule calculateRuntimes() every 6 hours, with the first run 1 minute after the Itron services init.
        scheduledExecutor.scheduleAtFixedRate(this::calculateDataLogs, 1, Hours.SIX.toStandardMinutes().getMinutes(), TimeUnit.MINUTES);
        log.info("Initialized ItronRuntimeCalcService");
    }
    
    @Override
    public void calculateDataLogs() {
        try {
            List<YukonPao> itronDevices = getAllDevices();
            if (itronDevices.isEmpty()) {
                return;
            }
            log.info("Calculating runtime/shedtime for Itron devices");
            log.debug("Found {} Itron devices", itronDevices.size());
            
            Map<PaoIdentifier, PaoMultiPointIdentifier> deviceDataLogPoints =
                    attributeService.findPaoMultiPointIdentifiersForAttributes(itronDevices, ItronRelayDataLogs.getDataLogAttributes()).stream()
                        .collect(StreamUtils.mapToSelf(PaoMultiPointIdentifier::getPao));
            
            Map<PaoIdentifier, PaoMultiPointIdentifier> deviceRelayStatusPoints =
                    attributeService.findPaoMultiPointIdentifiersForAttributes(itronDevices, ItronRelayDataLogs.getRelayStatusAttributes()).stream()
                        .collect(StreamUtils.mapToSelf(PaoMultiPointIdentifier::getPao));

            itronDevices.forEach(device -> {
                try {
                    var dataLogPoints = Optional.ofNullable(deviceDataLogPoints.get(device.getPaoIdentifier()))
                            .map(PaoMultiPointIdentifier::getPaoPointIdentifiers)
                            .orElseThrow(() -> new IllegalArgumentException("no data log points"));
                    
                    var relayStatusPoints = Optional.ofNullable(deviceRelayStatusPoints.get(device.getPaoIdentifier()))
                            .map(PaoMultiPointIdentifier::getPaoPointIdentifiers)
                            .orElseThrow(() -> new IllegalArgumentException("no relay state points"));

                    calculateDeviceDataLogs(device, relayStatusPoints, dataLogPoints);
                    
                } catch (IllegalArgumentException ex) {
                    log.warn("Skipping runtime/shedtime calculations for " + device, ex);
                }
            });

            log.info("Finished calculating runtime/shedtime for Itron devices");
        } catch (Exception e) {
            log.error("Error occurred in Itron runtime/shedtime calculation", e);
        }
    }

    /**
     * Calculates runtime and shedtime for an individual device.
     * @param device The device for which to calculate runtime and shedtime. 
     * @param relayStatusPoints The PaoPointIdentifiers of the runtime/shedtime status points
     * @param dataLogPoints The PaoPointIdentifiers of the runtime/shedtime data logs
     */
    private void calculateDeviceDataLogs(YukonPao device, Set<PaoPointIdentifier> relayStatusPoints, Set<PaoPointIdentifier> dataLogPoints) {
        Map<PaoPointIdentifier, PointValueQualityHolder> recentData = getRecentData(device);
        
        //  Get the most recent timestamp from all available point data on the device.
        Instant endOfRange = 
                getLatestTimestamp(recentData.values())
                    .map(RelayLogInterval.LOG_60_MINUTE::start)  //  round down to a 60 minute interval to allow full calculation of all interval lengths
                    .map(DateTime::toInstant)
                    .orElse(null);
        
        if (endOfRange == null) {
            log.debug("No recent point data found for " + device);
            return;
        }

        //  Map of point IDs to data log point values 
        Map<PaoPointIdentifier, PointValueQualityHolder> dataLogValues = getPointData(dataLogPoints, recentData);
        
        //  Map of PaoPointIdentifiers to point IDs
        Map<PaoPointIdentifier, Integer> dataLogIdLookup = Maps.transformValues(dataLogValues, PointValueHolder::getId);
        
        //  Map of PaoPointIdentifiers to point IDs 
        Map<PaoPointIdentifier, Integer> relayStatusIdLookup = getPointIds(relayStatusPoints, recentData); 
        
        Set<Integer> relayStatusPointIds = Sets.newHashSet(relayStatusIdLookup.values());
        
        //  Latest data log point timestamp
        Instant startOfRange = 
                getLatestTimestamp(dataLogValues.values())
                    .map(DateTime::toInstant)
                    .orElse(null);

        var logRange = Range.inclusive(startOfRange, endOfRange); 

        //  Get all relay state information in RPH since the last data log update, and split it by point ID
        Map<Integer, List<PointValueHolder>> relayStatusData = 
                rphDao.getPointData(relayStatusPointIds, logRange, false, Order.FORWARD).stream()
                    .collect(Collectors.groupingBy(PointValueHolder::getId)); 

        for (var relayInfo : ItronRelayDataLogs.values()) {
            calculateRelayDataLogs(device, logRange, relayStatusData, relayInfo, relayStatusIdLookup, dataLogIdLookup);
        }
    }

    /**
     * Calculates runtime and shedtime for an individual relay.
     * @param device The device the relay is on.
     * @param logRange The range over which to calculate runtime and shedtime. 
     * @param relayStatusData The raw relay status data to be processed into data logs.
     * @param relayInfo The relay data log attributes and intervals 
     * @param relayStatusIdLookup A mapping of the point IDs of the device's relay status PaoPointIdentifiers 
     * @param dataLogIdLookup A mapping of the point IDs of the device's data log PaoPointIdentifiers
     */
    private void calculateRelayDataLogs(YukonPao device, Range<Instant> logRange,
            Map<Integer, List<PointValueHolder>> relayStatusData, ItronRelayDataLogs relayInfo, 
            Map<PaoPointIdentifier, Integer> relayStatusIdLookup, Map<PaoPointIdentifier, Integer> dataLogIdLookup) {
        BuiltInAttribute relayStatusAttribute = relayInfo.getRelayStatusAttribute();
        
        //  Map of point ID to its RelayLogInterval
        Map<Integer, RelayLogInterval> dataLogIntervals = relayInfo.getDataLogIntervals().entrySet().stream()
                //  Start with the attribute...
                .flatMap(e -> Optional.of(e.getKey())
                         //  use it to retrieve the attribute definition (if it exists)...  
                         .flatMap(attribute -> paoDefinitionDao.findAttributeLookup(device.getPaoIdentifier().getPaoType(), attribute))
                         //  turn that into the PaoPointIdentifier for our device type...
                         .map(attributeDefinition -> attributeDefinition.getPaoPointIdentifier(device))
                         //  use the PaoPointIdentifier to get the point ID from the dataLogPointIds map...
                         .map(dataLogIdLookup::get)
                         //  Then map the point ID to a RelayLogInterval.
                         .map(pointId -> Pair.of(pointId, e.getValue()))
                         .stream())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        
        //  Look up the relay state attribute...
        paoDefinitionDao.findAttributeLookup(device.getPaoIdentifier().getPaoType(), relayStatusAttribute)
            //  get the PaoPointIdentifier...
            .map(attributeDefinition -> attributeDefinition.getPaoPointIdentifier(device))
            //  use that to look up the relay state point ID...
            .map(relayStatusIdLookup::get)
            //  and use the point ID to insert the runtime/shedtime
            .ifPresent(relayStatusPointId -> {
                //  Add the preceding relay state reading from RawPointHistory if available.
                Iterable<PointValueHolder> relayStatuses = addBoundaryValues(relayStatusData.get(relayStatusPointId), logRange);

                if (relayInfo.isRuntime()) {
                    //  Transform the raw relay state data into runtime status 
                    Iterable<? extends DatedStatus> statuses = Iterables.transform(relayStatuses, ItronRuntimeCalcServiceImpl::getRuntimeStatusFromPoint);
                    
                    insertRelayRuntime(device, dataLogIntervals, statuses, logRange, relayInfo.getRelayNumber());
                } else {
                    //  Transform the raw relay state data into shedtime status
                    Iterable<? extends DatedStatus> statuses = Iterables.transform(relayStatuses, ItronRuntimeCalcServiceImpl::getShedtimeStatusFromPoint);
                            
                    insertRelayShedtime(device, dataLogIntervals, statuses, logRange);
                }
            });
    }

    /**
     * Gets the status point entry preceding the range, if the start of the range is specified, and extends the last status out to the end of the range.
     * This is needed to calculate runtime for the portion of the hour prior to the "first" status, and the portion of the hour after the status.
     * @param relayStatuses The existing raw relay statuses.
     * @param logRange The beginning of the range, or null if none.
     * @return The activity data with the new entry added, if retrieved.
     */
    private Iterable<PointValueHolder> addBoundaryValues(Iterable<PointValueHolder> relayStatuses, Range<Instant> logRange) {
        PointValueHolder firstStatus = Iterables.getFirst(relayStatuses, null);
        PointValueHolder lastStatus = Iterables.getLast(relayStatuses, null);
        
        //  Get the entry preceding the range, if the start of the range is defined
        if (firstStatus != null && logRange.getMin() != null) {
            int pointId = firstStatus.getId();
            Date centerDate = firstStatus.getPointDataTimeStamp();
            Range<Date> dateRange = new Range<>(null, true, centerDate, false);
            List<PointValueHolder> precedingValue = rphDao.getLimitedPointData(pointId,
                    dateRange.translate(CtiUtilities.INSTANT_FROM_DATE)
                    /* Clusivity.INCLUSIVE_EXCLUSIVE */, false,
                    Order.REVERSE, 1);
            relayStatuses = Iterables.concat(precedingValue, relayStatuses);
        }
        //  Extend the last status to the end of the range, which must be defined
        if (lastStatus != null && lastStatus.getPointDataTimeStamp().getTime() != logRange.getMax().getMillis()) {
            var trailingValue = List.of(cloneWithNewTime(lastStatus, logRange.getMax()));
            relayStatuses = Iterables.concat(relayStatuses, trailingValue);
        }
        return relayStatuses;
    }

    private PointValueHolder cloneWithNewTime(PointValueHolder lastStatus, Instant instant) {
        return new PointValueHolder() {
            @Override
            public int getId() {
                return lastStatus.getId();
            }

            @Override
            public Date getPointDataTimeStamp() {
                return new Date(instant.getMillis());
            }

            @Override
            public int getType() {
                return lastStatus.getType();
            }

            @Override
            public double getValue() {
                return lastStatus.getValue();
            }
        };
    }
    
    /**
     * Inserts relay shedtime for each shedtime data log interval point that is defined on the device.
     * @param device The device the relay is on 
     * @param dataLogIntervals A map of data log point IDs to their RelayLogInterval details.
     * @param statuses The raw shedtime status to be processed into shedtime logs.
     * @param logRange The range over which to log shedtime.
     */
    private void insertRelayShedtime(YukonPao device, Map<Integer, RelayLogInterval> dataLogIntervals, Iterable<? extends DatedStatus> statuses, 
            Range<Instant> logRange) {
        dataLogIntervals.forEach((relayDataLogPointId, interval) ->
            insertRelayDataLogs(device, statuses, logRange, relayDataLogPointId, interval));
    }

    /**
     * Inserts relay runtime for each runtime data log interval point that is defined on the device, and 
     * updates the relay's AssetAvailability with the latest runtime.
     * @param device The device the relay is on 
     * @param dataLogIntervals A map of data log point IDs to their RelayLogInterval details.
     * @param statuses The raw runtime status to be processed into shedtime logs.
     * @param logRange The range over which to log runtime.
     * @param relayNumber The relay number for the Asset Availability update.
     */
    private void insertRelayRuntime(YukonPao device, Map<Integer, RelayLogInterval> dataLogIntervals, Iterable<? extends DatedStatus> statuses, 
            Range<Instant> logRange, int relayNumber) {
        dataLogIntervals.entrySet().stream()
            .flatMap(e ->
                insertRelayDataLogs(device, statuses, logRange, e.getKey(), e.getValue()).entrySet().stream()
                    //  find nonzero runtimes 
                    .filter(entry -> entry.getValue() > 0)
                    .map(Entry::getKey)
                    //  find the latest date
                    .max(DateTime::compareTo)
                    .stream())
            .max(DateTime::compareTo)
            .map(DateTime::toInstant)
            .ifPresent(lastRuntime -> {
                var newTimes = new AssetAvailabilityPointDataTimes(device.getPaoIdentifier().getPaoId());
                newTimes.setRelayRuntime(relayNumber, lastRuntime);
                dynamicLcrCommunicationsDao.insertData(newTimes);
            });
    }

    /**
     * Calculate relay data logs from raw relay statuses and send the logs to Dispatch. 
     * @param device The device the relay is on.
     * @param statuses The raw runtime status to be prcessed into data logs.
     * @param logRange The range over which to log runtime.
     * @param relayDataLogPointId The data log point ID.
     * @param interval The interval at which to construct the data logs.
     * @return The runtime logs, in seconds per interval.
     */
    private Map<DateTime, Integer> insertRelayDataLogs(YukonPao device, Iterable<? extends DatedStatus> statuses, Range<Instant> logRange,
            Integer relayDataLogPointId, RelayLogInterval interval) {
        Map<DateTime, Integer> runtimeSeconds = runtimeCalcService.getIntervalRelayLogs(statuses, interval);
        
        log.trace("Inserting data logs for Itron device: {}", device);
        
        if (runtimeSeconds.isEmpty()) {
            log.info("Skipping data log insertion for {}. No new data is available for calculation.", device.getPaoIdentifier());
            return runtimeSeconds;
        } 
        
        List<PointData> pointDatas = runtimeSeconds.entrySet().stream()
                .filter(entry -> logRange.intersects(entry.getKey().toInstant()))
                .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))  //  send oldest to newest
                .map(entry -> makeRelayLogPointData(relayDataLogPointId, entry.getKey().toDate(), entry.getValue()))
                .collect(Collectors.toList());
        try {
            asyncDynamicDataSource.putValues(pointDatas);
        } catch (DispatchNotConnectedException e) {
            log.error("Unable to insert data logs for " + device.getPaoIdentifier() + " - no dispatch connection.  Will attempt to recalculate on next execution.", e);
        }
        
        return runtimeSeconds;
    }

    private PointData makeRelayLogPointData(Integer relayDataLogPointId, Date logDate, int activeSeconds) {
        Double activeMinutes = activeSeconds / 60.0;
        
        PointData pointData = new PointData();
        pointData.setId(relayDataLogPointId);
        pointData.setType(PointType.Analog.getPointTypeId());
        pointData.setMillis(0);
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setTime(logDate);
        pointData.setValue(activeMinutes);
        pointData.setTagsPointMustArchive(true);
        return pointData;
    }

    private List<YukonPao> getAllDevices() {
        return PaoType.getItronTypes().stream()
            .flatMap(type -> paoDao.getLiteYukonPAObjectByType(type).stream())
            .collect(Collectors.toList());
    }
    
    private static Optional<DateTime> getLatestTimestamp(Collection<PointValueQualityHolder> pointData) {
        return pointData.stream()
            .map(PointValueQualityHolder::getPointDataTimeStamp)
            .max(Date::compareTo)
            .map(DateTime::new);
    }
    
    private Map<PaoPointIdentifier, PointValueQualityHolder> getRecentData(YukonPao device) {
        Map<Integer, PaoPointIdentifier> ppiById = 
                pointDao.getLitePointsByPaObjectId(device.getPaoIdentifier().getPaoId()).stream()
                    .collect(Collectors.toMap(LitePoint::getPointID, 
                                          litePoint -> PaoPointIdentifier.createPaoPointIdentifier(litePoint, device)));
        
        return asyncDynamicDataSource.getPointDataOnce(ppiById.keySet()).stream()
                    .collect(StreamUtils.mapToSelf(pvqh -> ppiById.get(pvqh.getId())));
    }

    private static DatedRuntimeStatus getRuntimeStatusFromPoint(PointValueHolder pointValue) {
        DateTime date = new DateTime(pointValue.getPointDataTimeStamp());
        RuntimeStatus status = RuntimeStatus.STOPPED;
        
        if (pointValue.getValue() == ItronDataEventType.LOAD_ON.getValue()) {
            status = RuntimeStatus.RUNNING;
        }
        
        return new DatedRuntimeStatus(status, date);
    }
    
    private static DatedShedtimeStatus getShedtimeStatusFromPoint(PointValueHolder pointValue) {
        DateTime date = new DateTime(pointValue.getPointDataTimeStamp());
        ShedtimeStatus status = ShedtimeStatus.RESTORED;
        
        if (pointValue.getValue() == ItronDataEventType.SHED_START.getValue()) {
            status = ShedtimeStatus.SHED;
        }
        
        return new DatedShedtimeStatus(status, date);
    }
    
    private static Map<PaoPointIdentifier, Integer> getPointIds(Set<PaoPointIdentifier> paoPointIdentifiers, Map<PaoPointIdentifier, PointValueQualityHolder> recentData) {
        return Maps.transformValues(getPointData(paoPointIdentifiers, recentData), PointValueHolder::getId);
    }
    
    private static Map<PaoPointIdentifier, PointValueQualityHolder> getPointData(Set<PaoPointIdentifier> paoPointIdentifiers, Map<PaoPointIdentifier, PointValueQualityHolder> recentData) {
        return Maps.filterValues(Maps.asMap(paoPointIdentifiers, recentData::get), Objects::nonNull);
    }
}
