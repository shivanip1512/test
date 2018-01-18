package com.cannontech.maintenance.task;

import java.util.List;
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
import com.cannontech.maintenance.service.MaintenanceTaskService;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class MaintenanceScheduler {
    
    private static final Logger log = YukonLogManager.getLogger(MaintenanceScheduler.class);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Autowired MaintenanceTaskRunner taskRunner;
    @Autowired MaintenanceTaskService maintenanceService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    private ScheduledFuture<?> future;
    // Four hours in seconds
    private static final long fourHourWindow = 14400;
    // One hour in milliseconds
    private static final long minimumRunWindow = 3600000;
    private boolean rescheduleScheduler = false;

    // Update the schedule on startup and on maintenance Global Setting change
    @PostConstruct
    public void init() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(event -> {
            int primaryKeyId = event.getPrimaryKey();
            if ((event.getChangeCategory() == DbChangeCategory.GLOBAL_SETTING)
                && ((globalSettingDao.getSetting(GlobalSettingType.BUSINESS_DAYS).getId() != null
                     && primaryKeyId == globalSettingDao.getSetting(GlobalSettingType.BUSINESS_DAYS).getId().intValue())
                    || (globalSettingDao.getSetting(GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME).getId() != null
                        && primaryKeyId == globalSettingDao.getSetting(GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME).getId().intValue())
                    || (globalSettingDao.getSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS).getId() != null
                        && primaryKeyId == globalSettingDao.getSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS).getId().intValue())
                    || (globalSettingDao.getSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME).getId() != null 
                        && primaryKeyId == globalSettingDao.getSetting(GlobalSettingType.EXTERNAL_MAINTENANCE_HOURS_START_STOP_TIME).getId().intValue()))) {
                rescheduleScheduler = false;
                reschedule();
            }
        });
        reschedule();
    }

    @PreDestroy
    public void shutdown() {
        // TODO calculate unprocessed remaining tasks and save into database. probably can consider in future
    }

    // Schedule the task runner to run at the start of the next run window
    // In the future, when we want to run things in parallel, this would need to change
    private synchronized void reschedule() {
        if (future != null) {
            future.cancel(true);
        }
        long secondsUntilRun = getSecondsUntilNextRun();
        // Schedule the runner
        future = scheduledExecutorService.schedule(() -> {
            Instant endOfRunWindow = maintenanceService.getEndOfRunWindow();
            if (endOfRunWindow.getMillis() - Instant.now().getMillis() <= minimumRunWindow) {
                log.info("Not enough time to run maintenance tasks. Rescheduling");
                rescheduleScheduler = true;
            } else {
                log.info("Maintenance task is starting now and will end at " + endOfRunWindow.toDate());
                List<MaintenanceTask> tasks = maintenanceService.getMaintenanceTasks();
                if (tasks.size() == 0) {
                    rescheduleScheduler = true;
                } else {
                    // rescheduleScheduler will be true when all task completed before time, else it will be false
                    rescheduleScheduler = taskRunner.run(tasks, endOfRunWindow);
                }
            }
            // At the end of the run window, schedule this to run again at the start of the next window
            reschedule();
        }, secondsUntilRun, TimeUnit.SECONDS);
    }
    
    /**
     * This method gets the next run time. There are 2 cases here.
     * Case 1: When all task have not completed. This will return next run based on the business hour and maintenance hour settings
     * Case 2: When all the task have completed before completion time or or no tasks to run or no time to run scheduler, then rule is:
     *  If the difference in the completion time (endOfRunWindow) and four hour window is more than minimum run window then next run time will
     *  be four hours from now otherwise it will be the what ever the next run time is. 
     *  case 2 is required so that we do not keep on running the scheduler when there is nothing much to process or not much time (< 1hr) to process.
     */
    private long getSecondsUntilNextRun() {
        long secondsUntilRun = 0;

        // Different rules of rescheduling when all tasks are completed or no task to run or no time window to run.
        if (rescheduleScheduler) {
            Instant endOfRunWindow = maintenanceService.getEndOfRunWindow();
            if ((endOfRunWindow.getMillis() - Instant.now().getMillis() - (fourHourWindow *1000)) >= (minimumRunWindow)) {
                secondsUntilRun = maintenanceService.getSecondsUntilRun();
                if (secondsUntilRun == 0 || secondsUntilRun > fourHourWindow) {
                    secondsUntilRun = fourHourWindow;
                }
            } else {
                secondsUntilRun = (endOfRunWindow.getMillis() - Instant.now().getMillis())/1000;
            }
            log.info("All maintenance task are completed before end of run window. Rescheduling task runner.");
        } else {
            secondsUntilRun = maintenanceService.getSecondsUntilRun();
        }
        log.info("Maintenance task will start after " + secondsUntilRun + " seconds");
        return secondsUntilRun;
    }
    
    // Stop schedules Task
    public void stop() {
        log.info("Maintenance Scheduler is stopping.");
        if (future != null) {
            future.cancel(false); // Probably 
        }
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
