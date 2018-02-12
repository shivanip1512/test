package com.cannontech.maintenance.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.maintenance.MaintenanceHelper;
import com.cannontech.maintenance.MaintenanceScheduler;
import com.cannontech.maintenance.MaintenanceSettingType;
import com.cannontech.maintenance.MaintenanceTaskType;
import com.cannontech.maintenance.dao.MaintenanceTaskDao;
import com.cannontech.maintenance.service.MaintenanceTaskService;
import com.cannontech.maintenance.task.MaintenanceTask;
import com.cannontech.system.GlobalSettingType;

public class MaintenanceTaskServiceImpl implements MaintenanceTaskService {
    private final static Logger log = YukonLogManager.getLogger(MaintenanceTaskServiceImpl.class);
    @Autowired private MaintenanceTaskDao maintenanceTaskDao;
    @Autowired private MaintenanceHelper maintenanceHelper;
    private Map<MaintenanceTaskType, MaintenanceTask> maintenanceTaskMap = new HashMap<>();

    @Override
    public List<MaintenanceTask> getEnabledMaintenanceTasks(MaintenanceScheduler scheduler) {
        List<MaintenanceTaskType> maintenanceTaskTypes = getEnabledMaintenanceTaskTypes(scheduler);
        List<MaintenanceTask> tasks = maintenanceTaskMap.entrySet().stream()
                                                                   .filter(e -> maintenanceTaskTypes.contains(e.getKey()))
                                                                   .map(e -> e.getValue())
                                                                   .collect(Collectors.toList());
        return tasks;
    }

    @Override
    public Instant getEndOfRunWindow() {
        Instant instant = null;
        DateTime nextMaintenanceRunTime = null;
        DateTime endOfRunWindow = null;
        try {
            nextMaintenanceRunTime = maintenanceHelper.getNextRunTime();
            Date refDate = new Date(nextMaintenanceRunTime.getMillis());
            DateTime businessHourStartTime = maintenanceHelper.getNextStartTime(GlobalSettingType.BUSINESS_DAYS, refDate);
            DateTime extMaintenanceHourStartTime = maintenanceHelper.getNextStartTime(GlobalSettingType.EXTERNAL_MAINTENANCE_DAYS, refDate);
            // Check if only external maintenance hour is selected
            if (businessHourStartTime == null && extMaintenanceHourStartTime != null) {
                // If true, start of coming external maintenance hour will be end of run window.
                endOfRunWindow = extMaintenanceHourStartTime;
            } else if (businessHourStartTime != null && extMaintenanceHourStartTime == null) {
                // Check if only business hour is selected
                // If true, start of coming business hour will be end of run window.
                endOfRunWindow = businessHourStartTime;
            } else if (businessHourStartTime == null && extMaintenanceHourStartTime == null) {
                endOfRunWindow = DateTime.now().plus(Duration.standardDays(1));
            } else {
                // Come here when both are selected.
                // select next businessHourStartTime or externalMaintenanceHourStartTime, whichever start first
                boolean isExtMaintenanceFirst = businessHourStartTime.isAfter(extMaintenanceHourStartTime);
                endOfRunWindow = isExtMaintenanceFirst ? extMaintenanceHourStartTime : businessHourStartTime;
            }
        } catch (Exception e) {
            log.error("Unable to find run window for maintenance task", e);
            return Instant.now();
        }
        instant = endOfRunWindow.toInstant();
        return instant;
    }

    @Override
    public long getMillisecondsUntilRun() {
        DateTime nextMaintenanceRunTime = null;
        try {
            nextMaintenanceRunTime = maintenanceHelper.getNextRunTime();
        } catch (Exception e) {
            log.error("Unable to find run window for maintenance task", e);
            // If now time window found, sleep for 24hrs and then recheck.
            nextMaintenanceRunTime = DateTime.now().plus(Duration.standardDays(1));
        }
        long currentTimeInMillisec = System.currentTimeMillis();
        long nextRunTimeInMillisec = nextMaintenanceRunTime.getMillis();
        return nextRunTimeInMillisec - currentTimeInMillisec;
    }
    
    private List<MaintenanceTaskType> getEnabledMaintenanceTaskTypes(MaintenanceScheduler scheduler) {
        Set<MaintenanceTaskType> tasks = MaintenanceTaskType.getMaintenanceTaskForScheduler(scheduler);
        List<MaintenanceTaskType> enabledTasks = new ArrayList<>();

        tasks.stream().forEach(task -> {
            MaintenanceSettingType setting = MaintenanceSettingType.getEnabledSetting(task);
            if (maintenanceTaskDao.getBooleanValue(setting)) {
                enabledTasks.add(task);
            }
        });
        return enabledTasks;
    }

    @Override
    public Object getMaintenanceSettings(MaintenanceSettingType type) {
        return maintenanceTaskDao.getSettingValue(type);
    }

    @Autowired
    public void setMaintenanceTasks(List<MaintenanceTask> listOfMaintenanceTask) {
        for (MaintenanceTask maintenanceTask : listOfMaintenanceTask) {
            maintenanceTaskMap.put(maintenanceTask.getMaintenanceTaskType(), maintenanceTask);
        }
    }

}
