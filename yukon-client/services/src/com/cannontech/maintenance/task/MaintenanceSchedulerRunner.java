package com.cannontech.maintenance.task;


import java.util.Date;
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

import org.apache.log4j.Logger;
import org.joda.time.Instant;
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
    private static final long fourHourWindow = 14400000;
    // Half an hour in milliseconds
    private static final long minimumRunWindow = 1800000;

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
                reschedule(scheduler);
        });
    }
    
    /*
     * Schedule individual scheduler.
     */
    private synchronized void reschedule(MaintenanceScheduler scheduler) {

        if (schedulersFuture.get(scheduler) != null) {
            schedulersFuture.get(scheduler).cancel(true);
        }

        long millisecondsUntilRun = getMillisecondsUntilRun(scheduler);
        ScheduledFuture<?> future_scheduler;
        
        if (millisecondsUntilRun > minimumRunWindow && !isForceReschedule(scheduler)) {
            log.info("Maintenance scheduler " + scheduler + " will start at "
                + new Date(Instant.now().getMillis() + millisecondsUntilRun));
        }
        
        future_scheduler = scheduledExecutorService.schedule(() -> {
            if (isForceReschedule(scheduler)) {
                forceReschedule.put(scheduler, false);
                rescheduleScheduler.put(scheduler, false);
            } else {
                Instant endOfRunWindow = maintenanceService.getEndOfRunWindow();

                if (!isEnoughTimeAvailable(endOfRunWindow)) {
                    log.info("Not enough time to run "+scheduler);
                    rescheduleScheduler.put(scheduler, true);
                } else {

                    log.info("Maintenance scheduler " + scheduler + " is starting now and will end at "
                        + endOfRunWindow.toDate());
                    // Get only those task which will run in this scheduler
                    List<MaintenanceTask> tasks = maintenanceService.getEnabledMaintenanceTasks(scheduler);
                    if (tasks.size() == 0) {
                        log.info("No task to run for " + scheduler);
                        rescheduleScheduler.put(scheduler, true);
                    } else {
                        log.info("Maintenance scheduler " + scheduler + " will run " + tasks.size() + " tasks.");
                        // rescheduleScheduler will be true when all task completed before time, else it will be false
                        boolean taskCompleted = taskRunner.run(tasks, endOfRunWindow);
                        rescheduleScheduler.put(scheduler, taskCompleted);
                    }
                }
            }
            reschedule(scheduler);
        }, millisecondsUntilRun, TimeUnit.MILLISECONDS);
        schedulersFuture.put(scheduler, future_scheduler);
    }

    private boolean isForceReschedule(MaintenanceScheduler scheduler) {
        return forceReschedule.getOrDefault(scheduler, false);
    }

    private boolean shouldRescheduleScheduler(MaintenanceScheduler scheduler) {
        return rescheduleScheduler.getOrDefault(scheduler, false);
    }

    /*
     * Checks if enough time is available to run any scheduler.
     */
    private boolean isEnoughTimeAvailable(Instant endOfRunWindow) {
        if (endOfRunWindow.getMillis() - Instant.now().getMillis() <= minimumRunWindow) {
            return false;
        }
        return true;
    }

    private synchronized long getMillisecondsUntilRun(MaintenanceScheduler scheduler) {
        long millisecondsUntilRun = 0;
        // Different rules of rescheduling when all tasks are completed or no task to run or no time window to run.
        if (shouldRescheduleScheduler(scheduler)) {
            millisecondsUntilRun = getRescheduledSecondsUntillNextRun(scheduler);
        } else {
            millisecondsUntilRun = maintenanceService.getMillisecondsUntilRun();
        }
        if (millisecondsUntilRun < 0) {
            millisecondsUntilRun = 0;
        }
        return millisecondsUntilRun;
    }

    /*
     * When all the task have completed before completion time or or no tasks to run or no time to run
     * scheduler, then rule is:
     * If the difference in the completion time (endOfRunWindow) and four hour window is more than minimum run
     * window then next run time will
     * be four hours from now otherwise it will be the what ever the next run time is.
     * This is required so that we do not keep on running the scheduler when there is nothing much to process
     * or not much time (< 1hr) to process.
     */
    private long getRescheduledSecondsUntillNextRun(MaintenanceScheduler scheduler) {
        long millisecondsUntilRun = 0;
        Instant endOfRunWindow = maintenanceService.getEndOfRunWindow();
        if ((endOfRunWindow.getMillis() - Instant.now().getMillis() - fourHourWindow) >= (minimumRunWindow)) {
            millisecondsUntilRun = maintenanceService.getMillisecondsUntilRun();
            if (millisecondsUntilRun == 0 || millisecondsUntilRun > fourHourWindow) {
                millisecondsUntilRun = fourHourWindow;
            }
        } else {
            millisecondsUntilRun = (endOfRunWindow.getMillis() - Instant.now().getMillis());
            forceReschedule.put(scheduler, true);
        }
        return millisecondsUntilRun;
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
