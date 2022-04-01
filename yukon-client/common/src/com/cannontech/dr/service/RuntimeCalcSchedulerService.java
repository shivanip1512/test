package com.cannontech.dr.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigInteger;
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
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.dr.service.impl.DatedRuntimeStatus;
import com.cannontech.dr.service.impl.DatedShedtimeStatus;
import com.cannontech.dr.service.impl.DatedStatus;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class RuntimeCalcSchedulerService {
    
    public enum LogType {
        SHED_TIME,
        RUN_TIME
    }
    
    private static final Logger log = YukonLogManager.getLogger(RuntimeCalcSchedulerService.class);

    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private AttributeService attributeService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private DynamicLcrCommunicationsDao dynamicLcrCommunicationsDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PointDao pointDao;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private RuntimeCalcService runtimeCalcService;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    
    private ScheduledFuture<?> scheduledFuture;
    protected Set<BuiltInAttribute> dataLogAttributes;
    protected Set<BuiltInAttribute> relayStatusAttributes;
    protected Set<PaoType> types;


    @PostConstruct
    protected void init() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.GLOBAL_SETTING, this::databaseChangeEvent);

        scheduleCalculateDataLogs();
        log.info("Initialized " + this.getClass().getSimpleName());
    }

 
    protected abstract DatedRuntimeStatus getRuntimeStatusFromPoint(PointValueHolder pointValue);

    protected abstract DatedShedtimeStatus getShedtimeStatusFromPoint(PointValueHolder pointValue);

    protected abstract void calculateRelayDataLogs(YukonPao device, Map<PaoPointIdentifier, Integer> dataLogIdLookup,
            Map<PaoPointIdentifier, Integer> relayStatusIdLookup, Range<Instant> logRange,
            Map<Integer, List<PointValueHolder>> relayStatusData);

    private List<YukonPao> getAllDevices() {
        return types.stream()
                .flatMap(type -> paoDao.getLiteYukonPAObjectByType(type).stream())
                .collect(Collectors.toList());
    }

    /**
     * Called when any global setting is updated
     */
    private void databaseChangeEvent(DatabaseChangeEvent event) {
        if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.RUNTIME_CALCULATION_INTERVAL_HOURS)) {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }
            // RuntimeCalcInterval changed, schedule Calculate DataLogs with the updated interval.
            scheduleCalculateDataLogs();
        }
    }

    /**
     * Schedule Calculate Runtimes.
     */
    private void scheduleCalculateDataLogs() {
        // Schedule calculateDataLogs() every runtimeCalcInterval hours, with the first run 1 minute after the services init.
        scheduledFuture = 
                scheduledExecutor.scheduleAtFixedRate(
                        this::calculateDataLogs, 
                        1, 
                        getRuntimeCalcInterval() * 60,
                        TimeUnit.MINUTES);
    }

    /**
     * Get Runtime Calculation Interval
     */
    private Integer getRuntimeCalcInterval() {
        return globalSettingDao.getInteger(GlobalSettingType.RUNTIME_CALCULATION_INTERVAL_HOURS);
    }

    protected void calculateDataLogs() {
        try {
            List<YukonPao> devices = getAllDevices();
            if (devices.isEmpty()) {
                return;
            }
            log.info("{} Calculating runtime/shedtime for {} devices", this.getClass().getSimpleName(), devices.size());
            
            // Limit the range of data calculated, if there is a large gap. Default limit = 30 days.
            int historyLimitDays = configurationSource.getInteger(MasterConfigInteger.RUNTIME_CALC_RANGE_LIMIT_DAYS, 30);
            if (historyLimitDays > 0) {
                log.info("Calculation limited to past {} days.", historyLimitDays);
            }
            
            Map<PaoIdentifier, PaoMultiPointIdentifier> deviceDataLogPoints = attributeService
                    .findPaoMultiPointIdentifiersForAttributes(devices, dataLogAttributes).stream()
                    .collect(StreamUtils.mapToSelf(PaoMultiPointIdentifier::getPao));

            Map<PaoIdentifier, PaoMultiPointIdentifier> deviceRelayStatusPoints = attributeService
                    .findPaoMultiPointIdentifiersForAttributes(devices, relayStatusAttributes).stream()
                    .collect(StreamUtils.mapToSelf(PaoMultiPointIdentifier::getPao));

            devices.forEach(device -> {
                try {
                    var dataLogPoints = Optional.ofNullable(deviceDataLogPoints.get(device.getPaoIdentifier()))
                            .map(PaoMultiPointIdentifier::getPaoPointIdentifiers)
                            .orElseThrow(() -> new IllegalArgumentException("no data log points"));

                    var relayStatusPoints = Optional.ofNullable(deviceRelayStatusPoints.get(device.getPaoIdentifier()))
                            .map(PaoMultiPointIdentifier::getPaoPointIdentifiers)
                            .orElseThrow(() -> new IllegalArgumentException("no relay state points"));

                    calculateDeviceDataLogs(device, relayStatusPoints, dataLogPoints, historyLimitDays);

                } catch (Exception ex) {
                    if(device.getPaoIdentifier().getPaoType().isCloudLcr()) {
                        log.debug("{} Skipping runtime/shedtime calculations for {}", this.getClass().getSimpleName(), device, ex);
                    } else {
                        log.warn("{} Skipping runtime/shedtime calculations for {}", this.getClass().getSimpleName(), device, ex);
                    }
                }
            });

            log.info("{} Finished calculating runtime/shedtime for {} devices", this.getClass().getSimpleName(), devices.size());
        } catch (Exception e) {
            log.error("Error occurred in runtime/shedtime calculation", e);
        }
    }

    /**
     * Calculates runtime and shedtime for an individual device.
     * 
     * @param device            The device for which to calculate runtime and shedtime.
     * @param relayStatusPoints The PaoPointIdentifiers of the runtime/shedtime status points
     * @param dataLogPoints     The PaoPointIdentifiers of the runtime/shedtime data logs
     */
    private void calculateDeviceDataLogs(YukonPao device, Set<PaoPointIdentifier> relayStatusPoints,
            Set<PaoPointIdentifier> dataLogPoints, int historyLimitDays) {
        Map<PaoPointIdentifier, PointValueQualityHolder> recentData = getRecentData(device);
        
        if(log.isDebugEnabled()) {  
            Map<PaoPointIdentifier, String> identToTemplateName = dataLogPoints.stream().collect(Collectors.toMap(dl -> dl, dl -> paoDefinitionDao
                    .getPointTemplateByTypeAndOffset(device.getPaoIdentifier().getPaoType(), dl.getPointIdentifier()).getName()));
            identToTemplateName.forEach((ident, templateName) -> {
                log.debug("Device Id:{} Point Name:{} Point: {} Recent Data:{}", device.getPaoIdentifier().getPaoId(), templateName, ident, recentData.get(ident));
            });
        }

        // Get the most recent timestamp from all initialized point data on the device.
        Instant endOfRange = 
                getLatestInitializedTimestamp(recentData.values())
                    .map(RelayLogInterval.LOG_60_MINUTE::start) // round down to a 60 minute interval to allow full calculation of all interval lengths
                    .map(DateTime::toInstant)
                    .orElse(null);
        
        log.debug("End Range:{}", endOfRange);

        if (endOfRange == null) {
            log.debug("No recent point data found for " + device);
            return;
        }

        // Map of point IDs to data log point values, even if uninitialized
        Map<PaoPointIdentifier, PointValueQualityHolder> dataLogValues = getPointData(dataLogPoints, recentData);

        // Map of PaoPointIdentifiers to data log point IDs
        Map<PaoPointIdentifier, Integer> dataLogIdLookup = Maps.transformValues(dataLogValues, PointValueHolder::getId);

        // Map of PaoPointIdentifiers to relay status point values - only initialized points with real data
        Map<PaoPointIdentifier, PointValueQualityHolder> relayStatusValues = getInitializedPointData(relayStatusPoints,
                recentData);

        // Map of PaoPointIdentifiers to relay status point IDs
        Map<PaoPointIdentifier, Integer> relayStatusIdLookup = Maps.transformValues(relayStatusValues, PointValueHolder::getId);

        Set<Integer> relayStatusPointIds = Sets.newHashSet(relayStatusIdLookup.values());
        // list of Load state and Activation State per relay

        // Latest data log point timestamp
        Instant startOfRange = getLatestInitializedTimestamp(dataLogValues.values())
                .map(DateTime::toInstant)
                .orElse(null);
        
        log.debug("Start Range:{}", startOfRange);

        // Limit the range of data calculated. Default: 30 days back.
        // So if the latest initialized timestamp is older, ignore it, and limit the range to e.g. 30 days.
        startOfRange = getLimitedStartOfRange(startOfRange, historyLimitDays, DateTime.now());
        
        log.debug("Start Range after limiter:{}", startOfRange);
        
        var logRange = Range.inclusive(startOfRange, endOfRange);

        // Get all relay state information in RPH since the last data log update, and split it by point ID
        Map<Integer, List<PointValueHolder>> relayStatusData = rphDao
                .getPointData(relayStatusPointIds, logRange, false, Order.FORWARD).stream()
                .collect(Collectors.groupingBy(PointValueHolder::getId));
        
        log.debug("Relay Status Data since the last data log update :{}", relayStatusData);

        // Add in any relay information that's outside of the range
        relayStatusIdLookup
                .forEach((ppi, pointId) -> relayStatusData.computeIfAbsent(pointId, x -> List.of(relayStatusValues.get(ppi))));
        
        log.debug("Relay Status Id Lookup :{}", relayStatusIdLookup);
        log.debug("Relay Status Data since the last data log update plus missing stuff :{}", relayStatusData);

        calculateRelayDataLogs(device, dataLogIdLookup, relayStatusIdLookup, logRange, relayStatusData);
    }

    /**
     * Given a "default" start of range for runtime calculation and a limit of days to look back, determine the correct
     * start of the calculation range for the specified current time.
     * @param startOfRange The default start Instant for runtime calcualtion
     * @param historyLimitDays The maximum number of days to go back and calculate
     * @param currentTime The time of calculation.
     * @return A start of range Instant value that falls within the history limit days, if specified.
     */
    public static Instant getLimitedStartOfRange(Instant startOfRange, int historyLimitDays, DateTime currentTime) {
        if (historyLimitDays > 0) {
            Instant limitStartOfRange = 
                    currentTime.minus(Duration.standardDays(historyLimitDays))
                               .withTimeAtStartOfDay()
                               .toInstant();
            
            if (startOfRange == null || limitStartOfRange.isAfter(startOfRange)) {
                return limitStartOfRange;
            }
        }
        return startOfRange;
    }
    
    /**
     * Calculates runtime and shedtime for an individual relay.
     * 
     * @param device              The device the relay is on.
     * @param logRange            The range over which to calculate runtime and shedtime.
     * @param relayStatusData     The raw relay status data to be processed into data logs.
     * @param relayInfo           The relay data log attributes and intervals
     * @param relayStatusIdLookup A mapping of the point IDs of the device's relay status PaoPointIdentifiers
     * @param dataLogIdLookup     A mapping of the point IDs of the device's data log PaoPointIdentifiers
     */
    protected void calculateRelayDataLogs(
            YukonPao device,
            Range<Instant> logRange,
            Map<Integer, List<PointValueHolder>> relayStatusData,
            BuiltInAttribute relayStatusAttribute,
            boolean isRuntime,
            int relayNumber,
            Map<BuiltInAttribute, RelayLogInterval> intervals,
            Map<PaoPointIdentifier, Integer> relayStatusIdLookup,
            Map<PaoPointIdentifier, Integer> dataLogIdLookup) {
        
        log.debug("Device:{}", device);
        log.debug("logRange:{}", logRange);
        log.debug("relayStatusData:{}", relayStatusData);
        log.debug("relayStatusAttribute:{}", relayStatusAttribute);
        log.debug("isRuntime:{}", isRuntime);
        log.debug("relayNumber:{}", relayNumber);
        log.debug("intervals:{}", intervals);
        log.debug("relayStatusIdLookup:{}", relayStatusIdLookup);
        log.debug("dataLogIdLookup:{}", dataLogIdLookup);

        // Map of point ID to its RelayLogInterval
        Map<Integer, RelayLogInterval> dataLogIntervals = intervals.entrySet().stream()
                // Start with the attribute...
                .flatMap(e -> Optional.of(e.getKey())
                        // use it to retrieve the attribute definition (if it exists)...
                        .flatMap(attribute -> paoDefinitionDao.findAttributeLookup(device.getPaoIdentifier().getPaoType(),
                                attribute))
                        // turn that into the PaoPointIdentifier for our device type...
                        .map(attributeDefinition -> attributeDefinition.getPaoPointIdentifier(device))
                        // use the PaoPointIdentifier to get the point ID from the dataLogPointIds map...
                        .map(dataLogIdLookup::get)
                        // Then map the point ID to a RelayLogInterval.
                        .map(pointId -> Pair.of(pointId, e.getValue()))
                        .stream())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        
        log.debug("Device:{} dataLogIntervals:{}", device, dataLogIntervals);
        // Look up the relay state attribute...
        paoDefinitionDao.findAttributeLookup(device.getPaoIdentifier().getPaoType(), relayStatusAttribute)
                // get the PaoPointIdentifier...
                .map(attributeDefinition -> attributeDefinition.getPaoPointIdentifier(device))
                // use that to look up the relay state point ID...
                .map(relayStatusIdLookup::get)
                // and use the point ID to insert the runtime/shedtime
                .ifPresent(relayStatusPointId -> {
                    // Add the preceding relay state reading from RawPointHistory if available.
                    Iterable<PointValueHolder> relayStatuses = addBoundaryValues(relayStatusData.get(relayStatusPointId),
                            logRange);

                    log.debug("Relay Statuses after boundry values :{}", relayStatuses);
                    
                    if (isRuntime) {
                        // Transform the raw relay state data into runtime status
                        Iterable<? extends DatedStatus> statuses = IterableUtils.toList(relayStatuses).stream()
                                .map(status -> getRuntimeStatusFromPoint(status)).collect(Collectors.toList());
                        log.debug(
                                "Device:{} Relay Number:{} Point id:{} Runtime:{} Relay Statuses (rph boundry values):{} Range:{} DataLogIntervals:{}",
                                device, relayNumber, relayStatusPointId, isRuntime, statuses, logRange, dataLogIntervals);
                        insertRelayRuntime(device, dataLogIntervals, statuses, logRange, relayNumber);
                    } else {
                        // Transform the raw relay state data into shedtime status
                        Iterable<? extends DatedStatus> statuses = IterableUtils.toList(relayStatuses).stream()
                                .map(status -> getShedtimeStatusFromPoint(status)).collect(Collectors.toList());
                        log.debug(
                                "Device:{} Relay Number:{} Point id:{} Runtime:{} Relay Statuses (rph boundry values):{} Range:{} DataLogIntervals:{}",
                                device, relayNumber, relayStatusPointId, isRuntime, statuses, logRange, dataLogIntervals);
                        insertRelayShedtime(device, dataLogIntervals, statuses, logRange);
                    }
                });
    }

    /**
     * Gets the status point entry preceding the range, if the start of the range is specified, and extends the last status out to
     * the end of the range.
     * This is needed to calculate runtime for the portion of the interval prior to the "first" status, and the portion of the
     * interval after the status.
     * 
     * @param relayStatuses The existing raw relay statuses.
     * @param logRange      The log calculation range
     * @return The activity data with the new entry added, if retrieved.
     */
    public Iterable<PointValueHolder> addBoundaryValues(Iterable<PointValueHolder> relayStatuses, Range<Instant> logRange) {

        log.debug("Relay Statuses before boundry values :{}", relayStatuses);
        relayStatuses = addPrecedingValue(relayStatuses, logRange);

        relayStatuses = addTrailingValue(relayStatuses, logRange);

        return relayStatuses;
    }

    private Iterable<PointValueHolder> addPrecedingValue(Iterable<PointValueHolder> relayStatuses, Range<Instant> logRange) {
        PointValueHolder firstStatus = Iterables.getFirst(relayStatuses, null);
        log.debug("First Status :{}", firstStatus);
        if (firstStatus != null) {
            var firstStatusInstant = new Instant(firstStatus.getPointDataTimeStamp().getTime());
            log.debug("First Status Instant :{}", firstStatusInstant);
            // Get the entry preceding the range, if the start of the range is defined
            if (logRange.getMin() != null && logRange.getMin().isBefore(firstStatusInstant)) {
                List<PointValueHolder> previousStatus = getPrecedingArchivedValue(firstStatus);
                log.debug("Previous Status :{}", previousStatus);
                List<PointValueHolder> previousPreviousStatus = getPrecedingArchivedValue(Iterables.getFirst(previousStatus, null));
                log.debug("Previous Previous Status :{}", previousPreviousStatus);
                
                relayStatuses = Iterables.concat(previousStatus, relayStatuses);
                relayStatuses = Iterables.concat(previousPreviousStatus, relayStatuses);
            }
        }
        return relayStatuses;
    }

    public List<PointValueHolder> getPrecedingArchivedValue(PointValueHolder firstStatus) {
        int pointId = firstStatus.getId();
        Date centerDate = firstStatus.getPointDataTimeStamp();
        log.debug("Center Date :{}", centerDate);
        Range<Date> dateRange = new Range<>(null, true, centerDate, false);
        log.debug("Date Range :{}", dateRange);
        return rphDao.getLimitedPointData(pointId,
                dateRange.translate(CtiUtilities.INSTANT_FROM_DATE)
                /* Clusivity.INCLUSIVE_EXCLUSIVE */, false,
                Order.REVERSE, 1);
    }

    private Iterable<PointValueHolder> addTrailingValue(Iterable<PointValueHolder> relayStatuses, Range<Instant> logRange) {
        PointValueHolder lastStatus = Iterables.getLast(relayStatuses, null);
        if (lastStatus != null) {
            var lastStatusInstant = new Instant(lastStatus.getPointDataTimeStamp().getTime());
            if (logRange.getMax().isAfter(lastStatusInstant)) {
                // Extend the last status to the end of the range
                var trailingValue = List.of(cloneWithNewTime(lastStatus, logRange.getMax()));
                relayStatuses = Iterables.concat(relayStatuses, trailingValue);
            }
        }
        return relayStatuses;
    }

    private PointValueHolder cloneWithNewTime(PointValueHolder lastStatus, Instant instant) {
        return new SimplePointValue(lastStatus.getId(),
                new Date(instant.getMillis()),
                lastStatus.getType(),
                lastStatus.getValue());
    }

    /**
     * Inserts relay shedtime for each shedtime data log interval point that is defined on the device.
     * 
     * @param device           The device the relay is on
     * @param dataLogIntervals A map of data log point IDs to their RelayLogInterval details.
     * @param statuses         The raw shedtime status to be processed into shedtime logs.
     * @param logRange         The range over which to log shedtime.
     */
    private void insertRelayShedtime(YukonPao device, Map<Integer, RelayLogInterval> dataLogIntervals,
            Iterable<? extends DatedStatus> statuses,
            Range<Instant> logRange) {
        dataLogIntervals.forEach((relayDataLogPointId, interval) -> insertRelayDataLogs(device, statuses, logRange,
                relayDataLogPointId, interval));
    }

    /**
     * Inserts relay runtime for each runtime data log interval point that is defined on the device, and
     * updates the relay's AssetAvailability with the latest runtime.
     * 
     * @param device           The device the relay is on
     * @param dataLogIntervals A map of data log point IDs to their RelayLogInterval details.
     * @param statuses         The raw runtime status to be processed into shedtime logs.
     * @param logRange         The range over which to log runtime.
     * @param relayNumber      The relay number for the Asset Availability update.
     */
    private void insertRelayRuntime(YukonPao device, Map<Integer, RelayLogInterval> dataLogIntervals,
            Iterable<? extends DatedStatus> statuses,
            Range<Instant> logRange, int relayNumber) {
        dataLogIntervals.entrySet().stream()
                .flatMap(e -> insertRelayDataLogs(device, statuses, logRange, e.getKey(), e.getValue()).entrySet().stream()
                        // find nonzero runtimes
                        .filter(entry -> entry.getValue() > 0)
                        .map(Entry::getKey)
                        // find the latest date
                        .max(DateTime::compareTo)
                        .stream())
                .max(DateTime::compareTo)
                .map(DateTime::toInstant)
                .ifPresent(lastRuntime -> {
                    var newTimes = new AssetAvailabilityPointDataTimes(device.getPaoIdentifier().getPaoId());
                    newTimes.setRelayRuntime(relayNumber, lastRuntime);
                    log.debug("Device:{} Asset Avaiability:{}", newTimes);
                    dynamicLcrCommunicationsDao.insertData(newTimes);
                });
    }

    /**
     * Calculate relay data logs from raw relay statuses and send the logs to Dispatch.
     * 
     * @param device              The device the relay is on.
     * @param statuses            The raw runtime status to be prcessed into data logs.
     * @param logRange            The range over which to log runtime.
     * @param relayDataLogPointId The data log point ID.
     * @param interval            The interval at which to construct the data logs.
     * @return The runtime logs, in seconds per interval.
     */
    private Map<DateTime, Integer> insertRelayDataLogs(YukonPao device, Iterable<? extends DatedStatus> statuses,
            Range<Instant> logRange,
            Integer relayDataLogPointId, RelayLogInterval interval) {
        Map<DateTime, Integer> runtimeSeconds = runtimeCalcService.getIntervalRelayLogs(statuses, interval);

        log.trace("Inserting data logs for device: {}, Runtime Seconds: {}", device, runtimeSeconds);

        if (runtimeSeconds.isEmpty()) {
            log.info("Skipping data log insertion for {}. No new data is available for calculation.", device.getPaoIdentifier());
            return runtimeSeconds;
        }

        List<PointData> pointDatas = runtimeSeconds.entrySet().stream()
                .filter(entry -> logRange.intersects(entry.getKey().toInstant()))
                .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey())) // send oldest to newest
                .map(entry -> makeRelayLogPointData(relayDataLogPointId, entry.getKey().toDate(), entry.getValue()))
                .collect(Collectors.toList());
        try {
            asyncDynamicDataSource.putValues(pointDatas);
            log.trace("Inserting point data to async cache, Data: {}", pointDatas);
        } catch (DispatchNotConnectedException e) {
            log.error("Unable to insert data logs for " + device.getPaoIdentifier()
                    + " - no dispatch connection.  Will attempt to recalculate on next execution.", e);
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

    private static Optional<DateTime> getLatestInitializedTimestamp(Collection<PointValueQualityHolder> pointData) {
        return pointData.stream()
                .filter(pvqh -> pvqh.getPointQuality() != PointQuality.Uninitialized)
                .map(PointValueQualityHolder::getPointDataTimeStamp)
                .max(Date::compareTo)
                .map(DateTime::new);
    }

    private Map<PaoPointIdentifier, PointValueQualityHolder> getRecentData(YukonPao device) {
        Map<Integer, PaoPointIdentifier> ppiById = pointDao.getLitePointsByPaObjectId(device.getPaoIdentifier().getPaoId())
                .stream()
                .collect(Collectors.toMap(LitePoint::getPointID,
                        litePoint -> PaoPointIdentifier.createPaoPointIdentifier(litePoint, device)));

        return asyncDynamicDataSource.getPointDataOnce(ppiById.keySet()).stream()
                .collect(StreamUtils.mapToSelf(pvqh -> ppiById.get(pvqh.getId())));
    }

    private static Map<PaoPointIdentifier, PointValueQualityHolder> getPointData(Set<PaoPointIdentifier> paoPointIdentifiers,
            Map<PaoPointIdentifier, PointValueQualityHolder> recentData) {
        return Maps.filterValues(Maps.asMap(paoPointIdentifiers, recentData::get), Objects::nonNull);
    }

    private static Map<PaoPointIdentifier, PointValueQualityHolder> getInitializedPointData(
            Set<PaoPointIdentifier> paoPointIdentifiers, Map<PaoPointIdentifier, PointValueQualityHolder> recentData) {
        return Maps.filterValues(getPointData(paoPointIdentifiers, recentData),
                pvqh -> pvqh.getPointQuality() != PointQuality.Uninitialized);
    }
}
