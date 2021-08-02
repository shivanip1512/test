package com.cannontech.dr.honeywellWifi;

import static com.cannontech.common.util.TimeUtil.getLeastRecent;
import static com.cannontech.common.util.TimeUtil.getStartOfHour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

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
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.AdjacentPointValues;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.dr.honeywellWifi.azure.event.EquipmentStatus;
import com.cannontech.dr.service.RuntimeCalcService;
import com.cannontech.dr.service.impl.DatedRuntimeStatus;
import com.cannontech.dr.service.impl.RuntimeCalcServiceHelper;
import com.cannontech.dr.service.impl.RuntimeStatus;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.ListMultimap;

public class HoneywellWifiRuntimeCalcServiceImpl implements HoneywellWifiRuntimeCalcService {
    private static final Logger log = YukonLogManager.getLogger(HoneywellWifiRuntimeCalcServiceImpl.class);
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private HoneywellWifiDataListener dataListener;
    @Autowired private PaoDao paoDao;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private RuntimeCalcService runtimeCalcService;
    @Autowired private RuntimeCalcServiceHelper runtimeCalcServiceHelper;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private GlobalSettingDao globalSettingDao;
    private ScheduledFuture<?> scheduledFuture;

    @PostConstruct
    public void init() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.GLOBAL_SETTING, this::databaseChangeEvent);

        scheduleCalculateRuntimes();
        log.info("Initialized HoneywellWifiRuntimeCalcService");
    }

    /**
     * Called when any global setting is updated
     */
    private void databaseChangeEvent(DatabaseChangeEvent event) {

        if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.RUNTIME_CALCULATION_INTERVAL_HOURS)) {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }
            // RuntimeCalcInterval changed, schedule Calculate Runtimes with the updated interval.
            scheduleCalculateRuntimes();
        }

    }

    /**
     * Schedule Calculate Runtimes.
     */
    private void scheduleCalculateRuntimes() {
        // Schedule calculateRuntimes() every runtimeCalcInterval hours, with the first run 1 minute after the honeywell services init.
        scheduledFuture = scheduledExecutor.scheduleAtFixedRate(this::calculateRuntimes, 1, getRuntimeCalcInterval() * 60, TimeUnit.MINUTES);
    }

    /**
     * Get Runtime Calculation Interval
     */
    private Integer getRuntimeCalcInterval() {
        return globalSettingDao.getInteger(GlobalSettingType.RUNTIME_CALCULATION_INTERVAL_HOURS);
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
            
            Map<Integer, DateTime> lastRuntimes = runtimeCalcServiceHelper.getLastRuntimes(thermostats);
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
                                             runtimeCalcServiceHelper.updateAssetAvailability(thermostat.getPaoIdentifier(), lastRuntimeDate.toInstant());
                                         });
    
                // Throw away any values prior to start of calculation range (if applicable), since that runtime is already
                // recorded.
                // Throw away the value for the last hour of the calculation range, since it is probably a partial hour. It
                // will get calculated next time.
                Predicate<Map.Entry<DateTime, Integer>> filter = null;
                if (startOfCalcRange != null) {
                    filter = entry -> {
                        return !entry.getKey().isBefore(startOfCalcRange)
                               && entry.getKey().isBefore(getStartOfHour(endOfCalcRange.toDateTime()));
                    };
                }
                
                //Insert runtime values via dispatch
                runtimeCalcServiceHelper.insertRuntimes(thermostat, runtimeSeconds, filter);
                
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

}
