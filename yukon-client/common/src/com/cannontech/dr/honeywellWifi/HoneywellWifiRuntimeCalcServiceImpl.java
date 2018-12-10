package com.cannontech.dr.honeywellWifi;

import static com.cannontech.common.util.TimeUtil.getLeastRecent;
import static com.cannontech.common.util.TimeUtil.getStartOfHour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.AdjacentPointValues;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.dr.assetavailability.AssetAvailabilityPointDataTimes;
import com.cannontech.dr.assetavailability.dao.DynamicLcrCommunicationsDao;
import com.cannontech.dr.honeywellWifi.azure.event.EquipmentStatus;
import com.cannontech.dr.service.RuntimeCalcService;
import com.cannontech.dr.service.impl.DatedRuntimeStatus;
import com.cannontech.dr.service.impl.RuntimeStatus;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.ListMultimap;

public class HoneywellWifiRuntimeCalcServiceImpl implements HoneywellWifiRuntimeCalcService {
    private static final Logger log = YukonLogManager.getLogger(HoneywellWifiRuntimeCalcServiceImpl.class);
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private HoneywellWifiDataListener dataListener;
    @Autowired private PaoDao paoDao;
    @Autowired private PointDao pointDao;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private RuntimeCalcService runtimeCalcService;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private DynamicLcrCommunicationsDao dynamicLcrCommunicationsDao;
    private static final int runtimePointOffset = 5;
    
    @PostConstruct
    public void init() {
        //Schedule calculateRuntimes() every 6 hours, with the first run 1 minute after the honeywell services init.
        scheduledExecutor.scheduleAtFixedRate(this::calculateRuntimes, 1, 6*60, TimeUnit.MINUTES);
        log.info("Initialized HoneywellWifiRuntimeCalcService");
    }
    
    @Override
    public void calculateRuntimes() {
        try {
            List<YukonPao> thermostats = getAllThermostats();
            if (thermostats.size() == 0) {
                return;
            }
            log.info("Calculating runtimes for Honeywell wifi thermostats.");
            log.debug("Found " + thermostats.size() + " Honeywell wifi thermostats.");
            
            Map<Integer, DateTime> lastRuntimes = getLastRuntimes(thermostats);
            Instant endOfCalcRange = getEndOfRuntimeCalcRange().toInstant();
            
            for (YukonPao thermostat : thermostats) {
                // Get all the state values within the range where we want to calculate runtime
                int paoId = thermostat.getPaoIdentifier().getPaoId();
                final Instant startOfCalcRange;
                if (lastRuntimes.get(paoId) != null) {
                    startOfCalcRange = lastRuntimes.get(paoId).toInstant();
                } else {
                    startOfCalcRange = null;
                }
                
                ListMultimap<PaoIdentifier, PointValueQualityHolder> stateDataMultimap = 
                        rphDao.getAttributeData(Collections.singleton(thermostat), 
                                                BuiltInAttribute.THERMOSTAT_RELAY_STATE, 
                                                false, 
                                                Range.inclusive(startOfCalcRange, endOfCalcRange), 
                                                Order.FORWARD, 
                                                null);
                List<PointValueQualityHolder> stateData = stateDataMultimap.get(thermostat.getPaoIdentifier());
                
                // Get the status point entry directly previous to the first (if applicable).
                // This is needed to calculate runtime for the portion of the hour prior to the "first" status
                PointValueHolder previousStatus = null;
                if (startOfCalcRange != null && stateData.size() > 0) {
                    AdjacentPointValues adjacents = rphDao.getAdjacentPointValues(stateData.get(0));
                    previousStatus = adjacents.getPreceding();
                }
                
                //Calculate hourly runtimes
                List<DatedRuntimeStatus> statuses = new ArrayList<>();
                if (previousStatus != null) {
                    statuses.add(getRuntimeStatusFromPoint(previousStatus));
                }
                for (PointValueQualityHolder pointValue : stateData) {
                    statuses.add(getRuntimeStatusFromPoint(pointValue));
                }
                Map<DateTime, Integer> runtimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
                //Updating last non zero runtime for thermostat
                runtimeSeconds.entrySet().stream()
                                         .filter(entry -> entry.getValue() > 0)
                                         .map(entry -> entry.getKey())
                                         .max(DateTime::compareTo)
                                         .ifPresent(lastRuntimeDate -> {
                                             updateAssetAvailability(thermostat.getPaoIdentifier(), lastRuntimeDate.toInstant());
                                         });
    
                // Throw away any values prior to start of calculation range (if applicable), since that runtime is already
                // recorded.
                // Throw away the value for the last hour of the calculation range, since it is probably a partial hour. It
                // will get calculated next time.
                Predicate<Map.Entry<DateTime, Integer>> filter = null;
                if (startOfCalcRange != null) {
                    filter = entry -> {
                        return entry.getKey().isAfter(startOfCalcRange)
                               || entry.getKey().equals(startOfCalcRange)
                               || entry.getKey().isAfter(getStartOfHour(endOfCalcRange.toDateTime()));
                    };
                }
                
                //Insert runtime values via dispatch
                insertRuntimes(thermostat, runtimeSeconds, filter);
                
                log.info("Finished calculating runtimes for Honeywell wifi thermostats.");
            }
        } catch (Exception e) {
            log.error("Error occurred in Honeywell runtime calculation", e);
        }
    }
    
    private List<YukonPao> getAllThermostats() {
        
        List<YukonPao> thermostats = new ArrayList<>();
        PaoType.getHoneywellTypes().forEach(
            type -> thermostats.addAll(paoDao.getLiteYukonPAObjectByType(type))
        );
        return thermostats;
    }
    
    private Map<Integer, DateTime> getLastRuntimes(List<YukonPao> thermostats) {
        
        Map<PaoIdentifier, PointValueQualityHolder> runtimeValues = 
                rphDao.getSingleAttributeData(thermostats, BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG, false, null);
        
        return runtimeValues.entrySet()
                            .stream()
                            .collect(Collectors.toMap(entry -> entry.getKey().getPaoId(), 
                                                      entry -> new DateTime(entry.getValue().getPointDataTimeStamp())));
    }
    
    private DateTime getEndOfRuntimeCalcRange() {
        // Default to start of previous hour
        DateTime endOfCalcRange = getStartOfHour(DateTime.now().minus(Duration.standardHours(1)));
        
        // Only allow updates up until the date-time where we have received messages, or have cleared the queue completely.
        DateTime lastEmptyQueueTime = dataListener.getLastEmptyQueueTime();
        DateTime lastProcessedMessageTime = dataListener.getLastProcessedMessageTime();

        // Cannot determine a safe end of runtime calculation range unless we know the most recent time messages were
        // processed.
        if (lastEmptyQueueTime == null && lastProcessedMessageTime == null) {
            throw new IllegalStateException("Message processing has not started yet.");
        }
        
        // Only calculate up to the last parsed message time, or the last time the queue was completely empty
        // And, at most, process up to the start of the last hour.
        if (lastEmptyQueueTime == null && lastProcessedMessageTime != null) {
            return getLeastRecent(endOfCalcRange, lastProcessedMessageTime);
        } else if (lastEmptyQueueTime != null && lastProcessedMessageTime == null) {
            return getLeastRecent(endOfCalcRange, lastEmptyQueueTime);
        } else {
            DateTime lastProcessingTime = TimeUtil.getMostRecent(lastEmptyQueueTime, lastProcessedMessageTime);
            return getLeastRecent(endOfCalcRange, lastProcessingTime);
        }
    }
    
    private boolean insertRuntimes(YukonPao pao, Map<DateTime, Integer> hourlyRuntimeSeconds, 
                                  Predicate<Map.Entry<DateTime, Integer>> filter) {
        
        log.trace("Inserting runtimes for Honeywell wifi thermostat: " + pao);
        
        if (hourlyRuntimeSeconds.size() == 0) {
            log.info("Skipping runtime insertion for " + pao.getPaoIdentifier() + 
                     ". Not enough new data is available for calculation.");
            return false;
        }
        
        LitePoint runtimePoint;
        try {
            runtimePoint = pointDao.getLitePointIdByDeviceId_Offset_PointType(pao.getPaoIdentifier().getPaoId(), 
                                                                              runtimePointOffset, 
                                                                              PointType.Analog.getPointTypeId());
        } catch (NotFoundException e) {
            log.error("Unable to insert runtime for " + pao.getPaoIdentifier() + " - no runtime point at analog offset " 
                      + runtimePointOffset + ".");
            return false;
        }
        
        List<PointData> pointDatas = hourlyRuntimeSeconds.entrySet()
                                                         .stream()
                                                         .filter(nullSafeFilter(filter))
                                                         .map(entry -> {
                                                             Date runtimeDate = entry.getKey().toDate();
                                                             int seconds = entry.getValue();
                                                             Double runtimeMinutes = seconds / 60.0;
                                                             
                                                             PointData pointData = new PointData();
                                                             pointData.setId(runtimePoint.getLiteID());
                                                             pointData.setType(PointType.Analog.getPointTypeId());
                                                             pointData.setMillis(0);
                                                             pointData.setPointQuality(PointQuality.Normal);
                                                             pointData.setTime(runtimeDate);
                                                             pointData.setValue(runtimeMinutes);
                                                             pointData.setTagsPointMustArchive(true);
                                                             return pointData;
                                                         })
                                                         .collect(Collectors.toList());
        try {
            asyncDynamicDataSource.putValues(pointDatas);
        } catch (DispatchNotConnectedException e) {
            log.error("Unable to insert runtime for " + pao.getPaoIdentifier() + " - no dispatch connection.");
            return false;
        }
        return true;
    }
    
    private DatedRuntimeStatus getRuntimeStatusFromPoint(PointValueHolder pointValue) {
        DateTime date = new DateTime(pointValue.getPointDataTimeStamp());
        RuntimeStatus status = RuntimeStatus.STOPPED;
        
        // Heating (0) or Cooling (1) both map to RuntimeStatus.RUNNING
        if (pointValue.getValue() == EquipmentStatus.HEATING.getStateValue() 
            || pointValue.getValue() == EquipmentStatus.COOLING.getStateValue()) {
            status = RuntimeStatus.RUNNING;
        }
        
        return new DatedRuntimeStatus(status, date);
    }
    
    /**
     * If the specified predicate is null, replace it with a filter that accepts all input.
     */
    private Predicate<Map.Entry<DateTime, Integer>> nullSafeFilter(Predicate<Map.Entry<DateTime, Integer>> filter) {
        if (filter == null) {
            filter = input -> true;
        }
        return filter;
    }
    
    private void updateAssetAvailability(PaoIdentifier paoIdentifier, Instant lastRuntime) {
        AssetAvailabilityPointDataTimes newTimes = new AssetAvailabilityPointDataTimes(paoIdentifier.getPaoId());
        newTimes.setRelayRuntime(1, lastRuntime);
        dynamicLcrCommunicationsDao.insertData(newTimes);
    }
}
