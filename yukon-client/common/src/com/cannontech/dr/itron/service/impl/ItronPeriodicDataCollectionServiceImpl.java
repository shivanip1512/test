package com.cannontech.dr.itron.service.impl;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.dr.itron.service.ItronPeriodicDataCollectionService;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ItronPeriodicDataCollectionServiceImpl implements ItronPeriodicDataCollectionService {

    private static final Logger log = YukonLogManager.getLogger(ItronPeriodicDataCollectionServiceImpl.class);

    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired GlobalSettingDao settingsDao;
    @Autowired AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired PaoDao paoDao;
    private Duration interval = Duration.ZERO;
    private ScheduledFuture<?> scheduledTask;

    @PostConstruct
    public void init() {
        scheduleDataCollection(getDataCollectionInterval());
        
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.GLOBAL_SETTING, this::databaseChangeEvent);
        
        log.info("Initialized ItronPeriodicDataCollectionService");
    }
    
    private void databaseChangeEvent(DatabaseChangeEvent event) {
        var newInterval = getDataCollectionInterval();
        if (newInterval.equals(interval)) {
            //  interval matches, nothing to do
            return;
        }
        scheduleDataCollection(newInterval);
    }
    
    private void scheduleDataCollection(Duration newInterval) {
        if (newInterval.isZero()) {
            log.warn("Data collection rate set to zero.  Pausing data collection until this is changed.");
            if (scheduledTask != null) {
                scheduledTask.cancel(false);
                scheduledTask = null;
            }
            interval = newInterval;
            return;
        }
        
        log.debug("Setting new interval to " + format(newInterval));

        var delay = Duration.ofMinutes(1);

        if (scheduledTask != null) {
            var remainingDelay = Duration.ofSeconds(scheduledTask.getDelay(TimeUnit.SECONDS));

            log.debug("Remaining delay was " + remainingDelay);

            scheduledTask.cancel(false);
            
            //  For example, 2:37 remaining on a 4 hour interval, updating to 2 hours:
            //    2:37 - 4:00 + 2:00 = 0:37
            var newDelay = remainingDelay.minus(interval).plus(newInterval);

            log.debug("Calculated new delay as " + newDelay);

            //  Wait at least one minute
            delay = ObjectUtils.max(delay, newDelay);
        }
        
        interval = newInterval;
        
        log.info("Collection set to " + format(interval) + ", starting in " + format(delay) + ".");
        
        scheduledTask = scheduledExecutor.scheduleAtFixedRate(this::collectData, delay.toSeconds(), interval.toSeconds(), TimeUnit.SECONDS);
    }
    
    private static String format(Duration d) {
        return DurationFormatUtils.formatDurationWords(d.toMillis(), true, true);
    }
    
    @Override
    public void collectData() {
        paoDao.getExistingPaoTypes().stream()
            .filter(PaoType::isItron)
            .findAny()
            .ifPresentOrElse(type -> { 
                    log.info("Itron device type found (" + type + "), beginning data collection");
                    
                    var csv = requestRawDataCsv();
                    
                    var pointData = parseCsvToPointData(csv);
                    
                    sendPointData(pointData);
                    
                    log.info("Point data sent");
                },
                () -> log.info("No Itron device types found, skipping data collection"));
    }

    private Object requestRawDataCsv() {
        log.debug("Requesting raw data CSV from Itron");
        
        return null;
    }

    private Iterable<PointData> parseCsvToPointData(Object csv) {
        log.debug("Parsing raw data CSV into point data");
        
        return Collections.emptyList();
    }

    private void sendPointData(Iterable<PointData> pointDatas) {
        log.debug("Sending Itron point data to Dispatch");
        
        asyncDynamicDataSource.putValues(pointDatas);
    }
    
    private Duration getDataCollectionInterval() {
        int hours = settingsDao.getInteger(GlobalSettingType.ITRON_HCM_DATA_COLLECTION_HOURS);
        
        if (hours > 0) {
            return Duration.ofHours(hours);
        }
        
        if (hours < 0) {
            log.warn("Invalid data collection interval: " + hours + ", setting to zero");
        }

        return Duration.ZERO;
    }
}
