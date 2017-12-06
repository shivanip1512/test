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

    // Update the schedule on startup and on maintenance Global Setting change
    @PostConstruct
    public void init() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(event -> {
            int primaryKeyId = event.getPrimaryKey();
            if ((event.getChangeCategory() == DbChangeCategory.GLOBAL_SETTING) && (primaryKeyId ==
                globalSettingDao.getSetting(GlobalSettingType.BUSINESS_HOURS_DAYS).getId().intValue()
                || primaryKeyId ==
                    globalSettingDao.getSetting(GlobalSettingType.BUSINESS_HOURS_START_STOP_TIME).getId().intValue()
                || primaryKeyId == 
                    globalSettingDao.getSetting(GlobalSettingType.MAINTENANCE_DAYS).getId().intValue()
                || primaryKeyId ==
                    globalSettingDao.getSetting(GlobalSettingType.MAINTENANCE_HOURS_START_STOP_TIME).getId().intValue())) {
                reschedule();
            }
        });
        reschedule();
    }

    @PreDestroy
    public void shutdown() {
        // TODO calculate unprocessed remaining tasks and save into database. probably can consider in future
    }

    // Call this when the user updates the run windows
    public void scheduleUpdated() {
        reschedule();
    }

    // Schedule the task runner to run at the start of the next run window
    // In the future, when we want to run things in parallel, this would need to change
    private synchronized void reschedule() {
        if (future != null) {
            future.cancel(true);
        }
        long secondsUntilRun = maintenanceService.getSecondsUntilRun();
        // Schedule the runner
        future = scheduledExecutorService.schedule(() -> {
            Instant endOfRunWindow = maintenanceService.getEndOfRunWindow();

            List<MaintenanceTask> tasks = maintenanceService.getMaintenanceTasks();
            taskRunner.run(tasks, endOfRunWindow);
            // At the end of the run window, schedule this to run again at the start of the next window
            reschedule();
        }, secondsUntilRun, TimeUnit.SECONDS);
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
