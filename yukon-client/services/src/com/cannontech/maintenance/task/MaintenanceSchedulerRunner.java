package com.cannontech.maintenance.task;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.maintenance.MaintenanceScheduler;
import com.cannontech.maintenance.service.MaintenanceTaskService;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.Sets;

public class MaintenanceSchedulerRunner {

    private static final Logger log = YukonLogManager.getLogger(MaintenanceSchedulerRunner.class);
    private final ScheduledExecutorService scheduledExecutorService =
        Executors.newScheduledThreadPool(MaintenanceScheduler.values().length);

    @Autowired MaintenanceTaskRunner taskRunner;
    @Autowired MaintenanceTaskService maintenanceService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private GlobalSettingDao globalSettingDao;

    // Four hours in milliseconds
    private static final Duration fourHourWindow = new Duration(14400000);
    // Half an hour in milliseconds
    private static final Duration minimumRunWindow = new Duration(1800000);

    private Map<MaintenanceScheduler, Boolean> forceReschedule = new ConcurrentHashMap<>();
    private Map<MaintenanceScheduler, Boolean> rescheduleScheduler = new ConcurrentHashMap<>();
    private final Map<MaintenanceScheduler, ScheduledFuture<?>> schedulersFuture = new ConcurrentHashMap<>();

 // Update the schedule on startup and on maintenance Global Setting change
    @PostConstruct
    public void init() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(event -> {
            if ((event.getChangeCategory() == DbChangeCategory.GLOBAL_SETTING)
                && (isBusinessDaysSettingUpdated(event) ||
                    isBusinessHoursSettingUpdated(event) ||
                    isExternalMaintDaysSettingUpdated(event) ||
                    isExternalMaintHoursStartStopSettingUpdated(event))) {
                rescheduleScheduler.clear();
                forceReschedule.clear();
                rescheduleAllScheduler();
            }
        });
        rescheduleAllScheduler();
    }
    
    /**
     * @return True if the db change event reflects a change in the business days setting.
     */
    private boolean isBusinessDaysSettingUpdated(DatabaseChangeEvent event) {
        int primaryKeyId = event.getPrimaryKey();
        return globalSettingDao.getSetting(GlobalSettingType.BUSINESS_DAYS).getId() != null
                && primaryKeyId == globalSettingDao.getSetting(GlobalSettingType.BUSINESS_DAYS)
                                                   .getId()
                                                   .intValue();
    }
    
    /**
     * @return True if the db change event reflects a change in the business hours start/stop time setting.
     */
    private boolean isBusinessHoursSettingUpdated(DatabaseChangeEvent event) {
        int primaryKeyId = event.getPrimaryKey();
        return globalSettingDao.getSetting(GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME).getId() != null
                && primaryKeyId == globalSettingDao.getSetting(GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME)
                                                   .getId()
                                                   .intValue();
    }
    
    /**
     * @return True if the db change event reflects a change in the external maintenance days setting.
     */
    private boolean isExternalMaintDaysSettingUpdated(DatabaseChangeEvent event) {
        int primaryKeyId = event.getPrimaryKey();
        return globalSettingDao.getSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS).getId() != null
                && primaryKeyId == globalSettingDao.getSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS)
                                                   .getId()
                                                   .intValue();
    }
    
    /**
     * @return True if the db change event reflects a change in the external maintenance hours start/stop time setting.
     */
    private boolean isExternalMaintHoursStartStopSettingUpdated(DatabaseChangeEvent event) {
        int primaryKeyId = event.getPrimaryKey();
        return globalSettingDao.getSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME).getId() != null
                && primaryKeyId == globalSettingDao.getSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME)
                                                   .getId()
                                                   .intValue();
    }

    @PreDestroy
    public void shutdown() {
        // TODO calculate unprocessed remaining tasks and save into database. probably can consider in future
    }
    
    /*
     * Picks all schedulers and schedule them individually.
     */
    private synchronized void rescheduleAllScheduler() {
        Set<MaintenanceScheduler> schedulers = Sets.newHashSet(MaintenanceScheduler.values());
        schedulers.stream().forEach(scheduler -> {
                reschedule(scheduler, false);
        });
    }
    
    /*
     * Schedule individual scheduler.
     */
    private synchronized void reschedule(MaintenanceScheduler scheduler, boolean addDelay) {

        if (schedulersFuture.get(scheduler) != null) {
            schedulersFuture.get(scheduler).cancel(true);
        }

        // minimum wait means to delay a minimum of 4 hours
        DateTime nextStartTime = addDelay ? DateTime.now().plus(fourHourWindow) : DateTime.now();
        // This MUST return an interval. If there is
        // absolutely no interval to be found, it should
        // reschedule for end of time (not run until a dbchange comes through)
        Interval nextRunWindow = getNextRunWindow(nextStartTime.toInstant());
        if (nextRunWindow != null) {
            long millisecondsUntilRun = Math.abs(nextRunWindow.getStartMillis() - Instant.now().getMillis());
            ScheduledFuture<?> future_scheduler;
            future_scheduler = scheduledExecutorService.schedule(() -> {
                boolean endedEarly = false;
                log.info("Maintenance scheduler " + scheduler + " is starting now and will end at " + nextRunWindow.getEnd().toDate());
                // Get only those task which will run in this scheduler
                List<MaintenanceTask> tasks = maintenanceService.getEnabledMaintenanceTasks(scheduler);
                if (tasks.size() == 0) {
                    log.info("No task to run for " + scheduler);
                    endedEarly = true;
                } else {
                    log.info("Maintenance scheduler " + scheduler + " will run " + tasks.size() + " tasks.");
                    // return will be true when all task completed before time, else it will be false
                    try {
                        endedEarly = taskRunner.run(tasks, nextRunWindow.getEnd().toInstant());
                    } catch (Throwable t) {
                        endedEarly = true;
                        log.error("Error occured while executing the scheduler : " + t);
                    }
                }
                reschedule(scheduler, endedEarly);
            }, millisecondsUntilRun, TimeUnit.MILLISECONDS);
            schedulersFuture.put(scheduler, future_scheduler);
        } else {
            log.info("No run time window is available for maintenance tasks.");
        }
        
    }

    // This (or the call a level or 2 down) can be unit tested?
    private synchronized Interval getNextRunWindow(Instant nowTime) {
        Interval nextRunWindow = null;
        try {
            nextRunWindow = maintenanceService.getNextAvailableRunTime(nowTime, minimumRunWindow);
        } catch (Exception e) {
            // do nothing
        }
        return nextRunWindow;
    }

    // Stop schedules Task
    public void stop() {
        log.info("Maintenance Scheduler is stopping.");

        schedulersFuture.keySet().stream().forEach(future -> {
            if (future != null) {
                schedulersFuture.get(future).cancel(false); // Probably
            }
        });
        scheduledExecutorService.shutdown();
        log.info("Maintenance Scheduler stopped.");
        try {
            // wait one minute to termination
            scheduledExecutorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            log.error("Maintenance Scheduler exception", ex);
        } finally {
            log.info("Maintenance Scheduler finish");
        }
    }

}
