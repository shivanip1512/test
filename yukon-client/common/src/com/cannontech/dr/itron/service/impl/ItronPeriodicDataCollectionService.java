package com.cannontech.dr.itron.service.impl;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.dr.itron.service.ItronDataReadService;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class ItronPeriodicDataCollectionService {

    private static final Logger log = YukonLogManager.getLogger(ItronPeriodicDataCollectionService.class);

    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private GlobalSettingDao settingsDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private ItronDataReadService itronDataReadService;
    @Autowired private PaoDao paoDao;
    @Autowired private ConfigurationSource configSource;
    
    private Duration interval = Duration.ZERO;
    private ScheduledFuture<?> scheduledTask;
    private boolean itronDevicesFound;
    public static final int maxRows = 1000;

    @PostConstruct
    public void init() {
        
        // No simulator, enabling this on a dev system will produce errors
        boolean isDevelopmentMode = configSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE, false);
        if(isDevelopmentMode) {
            log.info("Disabling Itron periodic data collection - development mode is active.");
            return;
        }
        
        scheduleDataCollection(getDataCollectionInterval());
        
        // Set up a listener for global setting changes, to process any updates to the data collection interval
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.GLOBAL_SETTING, this::databaseChangeEvent);
        
        log.info("Initialized ItronPeriodicDataCollectionService");
    }
    
    /**
     * Called when any global setting is updated
     */
    private void databaseChangeEvent(DatabaseChangeEvent event) {
        var newInterval = getDataCollectionInterval();
        if (!newInterval.equals(interval)) {
            // Interval changed, schedule data collection with the updated interval.
            scheduleDataCollection(newInterval);
        }
    }
    
    /**
     * Schedule the data collection task. If the new interval is 0, data collection stopped. Otherwise the
     * currently scheduled task will be cancelled, and a new task will be scheduled based on the new interval.
     */
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
        
        scheduledTask = scheduledExecutor.scheduleAtFixedRate(this::collectData,
            delay.toSeconds(), interval.toSeconds(), TimeUnit.SECONDS);
    }
    
    /**
     * Start the data collection, so long as there are Itron devices present in the system.
     */
    private void collectData() {
        if(!itronDevicesExist()) {
            log.debug("Skipping Itron periodic data collection - no devices present that communicate over Itron network.");
            return;
        }
        
        log.info("Starting Itron data collection");
        itronDataReadService.collectData();
        log.info("Itron data collection complete");
    }
    
    /**
     * Checks for any devices in the system that communicate over the Itron network (a.k.a. SilverSprings)
     */
    private boolean itronDevicesExist() {
        // If we already determined that there are Itron devices, don't bother checking again
        if (itronDevicesFound) {
            return true;
        }
        
        // Otherwise, check for Itron devices in the system
        itronDevicesFound = paoDao.getExistingPaoTypes()
                                  .stream()
                                  .anyMatch(PaoType::isItron);
        
        return itronDevicesFound;
    }
    
    /**
     * Get the data collection interval from the global setting and make sure it is valid.
     */
    private Duration getDataCollectionInterval() {
        int hours = settingsDao.getInteger(GlobalSettingType.ITRON_HCM_DATA_COLLECTION_HOURS);
        
        if (hours > 0) {
            return Duration.ofHours(hours);
        } else if (hours < 0) {
            log.warn("Invalid data collection interval: " + hours + ", setting to zero");
        }

        return Duration.ZERO;
    }
    
    private static String format(Duration d) {
        return DurationFormatUtils.formatDurationWords(d.toMillis(), true, true);
    }
}
