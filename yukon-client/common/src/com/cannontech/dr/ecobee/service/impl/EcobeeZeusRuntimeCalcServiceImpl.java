package com.cannontech.dr.ecobee.service.impl;

import static com.cannontech.common.util.TimeUtil.getStartOfHour;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.AdjacentPointValues;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.dr.ecobee.service.EcobeeZeusRuntimeCalcService;
import com.cannontech.dr.service.RelayLogInterval;
import com.cannontech.dr.service.RuntimeCalcService;
import com.cannontech.dr.service.impl.DatedRuntimeStatus;
import com.cannontech.dr.service.impl.RuntimeCalcServiceHelper;
import com.cannontech.dr.service.impl.RuntimeStatus;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.ListMultimap;

public class EcobeeZeusRuntimeCalcServiceImpl implements EcobeeZeusRuntimeCalcService {

    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private PaoDao paoDao;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    private ScheduledFuture<?> scheduledFuture;
    @Autowired private RuntimeCalcServiceHelper runtimeCalcServiceHelper;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private RuntimeCalcService runtimeCalcService;
    @Autowired private PointDao pointDao;

    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusRuntimeCalcServiceImpl.class);

    @PostConstruct
    public void init() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.GLOBAL_SETTING, this::databaseChangeEvent);

        scheduleCalculateRuntimes();
        log.info("Initialized EcobeeZeusRuntimeCalcService");
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
        // Schedule calculateRuntimes() every runtimeCalcInterval hours, with the first run 1 minute after the ecobee
        // services init.
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
            if (thermostats.isEmpty()) {
                return;
            }
            log.info("Calculating runtime for Ecobee devices");
            log.debug("Found {} Ecobee devices", thermostats.size());

            Map<Integer, DateTime> lastRuntimes = runtimeCalcServiceHelper.getLastRuntimes(thermostats);

            for (YukonPao thermostat : thermostats) {

                Instant endOfCalcRange = getEndOfRuntimeCalcRange(thermostat);
                if (endOfCalcRange == null) {
                    log.debug("No recent point data found for " + thermostat);
                    continue;
                }

                int paoId = thermostat.getPaoIdentifier().getPaoId();
                final Instant startOfCalcRange;
                if (lastRuntimes.get(paoId) != null) {
                    startOfCalcRange = lastRuntimes.get(paoId).toInstant();
                } else {
                    startOfCalcRange = null;
                }

                ListMultimap<PaoIdentifier, PointValueQualityHolder> stateDataMultimap = rphDao.getAttributeData(Collections.singleton(thermostat),
                                                                                                                 BuiltInAttribute.THERMOSTAT_RELAY_STATE,
                                                                                                                 false,
                                                                                                                 Range.inclusive(startOfCalcRange,
                                                                                                                                 endOfCalcRange),
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

                // Calculate hourly runtimes
                List<DatedRuntimeStatus> statuses = new ArrayList<>();
                if (previousStatus != null) {
                    statuses.add(getRuntimeStatusFromPoint(previousStatus));
                }
                for (PointValueQualityHolder pointValue : stateData) {
                    statuses.add(getRuntimeStatusFromPoint(pointValue));
                }
                Map<DateTime, Integer> runtimeSeconds = runtimeCalcService.getHourlyRuntimeSeconds(statuses);
                // Updating last non zero runtime for thermostat
                runtimeSeconds.entrySet()
                              .stream()
                              .filter(entry -> entry.getValue() > 0)
                              .map(entry -> entry.getKey())
                              .max(DateTime::compareTo)
                              .ifPresent(lastRuntimeDate -> {
                                  runtimeCalcServiceHelper.updateAssetAvailability(thermostat.getPaoIdentifier(), lastRuntimeDate.toInstant());
                              });

                // Throw away any values prior to start of calculation range (if applicable), since that runtime is already
                // recorded. Throw away the value for the last hour of the calculation range, since it is probably a partial hour.
                // It will get calculated next time.
                Predicate<Map.Entry<DateTime, Integer>> filter = null;
                if (startOfCalcRange != null) {
                    filter = entry -> {
                        return !entry.getKey().isBefore(startOfCalcRange) && entry.getKey().isBefore(getStartOfHour(endOfCalcRange.toDateTime()));
                    };
                }

                // Insert runtime values via dispatch
                runtimeCalcServiceHelper.insertRuntimes(thermostat, runtimeSeconds, filter);
            };
            log.info("Finished calculating runtime for Ecobee devices");
        } catch (Exception e) {
            log.error("Error occurred in Ecobee runtime calculation", e);
        }
    }

    private List<YukonPao> getAllThermostats() {

        List<YukonPao> thermostats = new ArrayList<>();
        PaoType.getEcobeeTypes().forEach(type -> thermostats.addAll(paoDao.getLiteYukonPAObjectByType(type)));
        return thermostats;
    }

    private Instant getEndOfRuntimeCalcRange(YukonPao device) {
        Map<PaoPointIdentifier, PointValueQualityHolder> recentData = getRecentData(device);
        Instant endOfRange = getLatestInitializedTimestamp(recentData.values()).map(RelayLogInterval.LOG_60_MINUTE::start)
                                                                               .map(DateTime::toInstant)
                                                                               .orElse(null);
        return endOfRange;
    }

    private Map<PaoPointIdentifier, PointValueQualityHolder> getRecentData(YukonPao device) {
        Map<Integer, PaoPointIdentifier> ppiById = pointDao.getLitePointsByPaObjectId(device.getPaoIdentifier().getPaoId())
                                                           .stream()
                                                           .collect(Collectors.toMap(LitePoint::getPointID,
                                                                                     litePoint -> PaoPointIdentifier.createPaoPointIdentifier(litePoint,
                                                                                                                                              device)));

        return asyncDynamicDataSource.getPointDataOnce(ppiById.keySet()).stream().collect(StreamUtils.mapToSelf(pvqh -> ppiById.get(pvqh.getId())));
    }

    private static Optional<DateTime> getLatestInitializedTimestamp(Collection<PointValueQualityHolder> pointData) {
        return pointData.stream()
                        .filter(pvqh -> pvqh.getPointQuality() != PointQuality.Uninitialized)
                        .map(PointValueQualityHolder::getPointDataTimeStamp)
                        .max(Date::compareTo)
                        .map(DateTime::new);
    }

    private DatedRuntimeStatus getRuntimeStatusFromPoint(PointValueHolder pointValue) {
        DateTime date = new DateTime(pointValue.getPointDataTimeStamp());
        RuntimeStatus status = RuntimeStatus.STOPPED;

        // Heating (0) or Cooling (1) both map to RuntimeStatus.RUNNING
        if (pointValue.getValue() == 0 || pointValue.getValue() == 1) {
            status = RuntimeStatus.RUNNING;
        }

        return new DatedRuntimeStatus(status, date);
    }

}
