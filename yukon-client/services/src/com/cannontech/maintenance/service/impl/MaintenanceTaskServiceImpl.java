package com.cannontech.maintenance.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.maintenance.MaintenanceHelper;
import com.cannontech.maintenance.MaintenanceSettingType;
import com.cannontech.maintenance.MaintenanceTaskType;
import com.cannontech.maintenance.dao.MaintenanceTaskDao;
import com.cannontech.maintenance.service.MaintenanceTaskService;
import com.cannontech.maintenance.task.MaintenanceTask;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.model.MaintenanceSetting;

public class MaintenanceTaskServiceImpl implements MaintenanceTaskService {
    private final static Logger log = YukonLogManager.getLogger(MaintenanceTaskServiceImpl.class);
    @Autowired private MaintenanceTaskDao maintenanceTaskDao;
    @Autowired private MaintenanceHelper maintenanceHelper;
    private Map<MaintenanceTaskType, MaintenanceTask> maintenanceTaskMap = new HashMap<>();

    @Override
    public List<MaintenanceTask> getMaintenanceTasks() {
        List<MaintenanceTaskType> maintenanceTaskTypes = maintenanceTaskDao.getMaintenanceTaskTypes(true);
        List<MaintenanceTask> tasks = maintenanceTaskMap.entrySet().stream()
                                                                   .filter(e -> maintenanceTaskTypes.contains(e.getKey()))
                                                                   .map(e -> e.getValue())
                                                                   .collect(Collectors.toList());
        return tasks;
    }

    @Override
    public Instant getEndOfRunWindow() {
        Instant instant = null;
        try {
            DateTime businessHourStartTime = maintenanceHelper.getNextStartTime(GlobalSettingType.BUSINESS_HOURS_DAYS);
            DateTime extMaintenanceHourStartTime = maintenanceHelper.getNextStopTime(GlobalSettingType.MAINTENANCE_DAYS);
            // select next businessHourStartTime or externalMaintenanceHourStartTime, whichever start first
            boolean isExtMaintenanceFirst = businessHourStartTime.isAfter(extMaintenanceHourStartTime);
            DateTime endOfRunWindow = isExtMaintenanceFirst ? extMaintenanceHourStartTime : businessHourStartTime;
            instant = endOfRunWindow.toInstant();
        } catch (Exception e) {
            log.error("Parsing exception for end time", e);
        }
        return instant;
    }

    @Override
    public long getSecondsUntilRun() {
        DateTime nextMaintenanceRunTime = null;
        try {
            nextMaintenanceRunTime = maintenanceHelper.getNextScheduledRunTime();
        } catch (Exception e) {
            log.error("Parsing exception for next run time", e);
        }
        long currentTimeInSec = System.currentTimeMillis() / 1000;
        long nextRunTimeInSec = nextMaintenanceRunTime.getMillis() / 1000;
        return nextRunTimeInSec - currentTimeInSec;
    }

    @Override
    public Object getMaintenanceSettings(MaintenanceTaskType taskName, MaintenanceSettingType type) {
        List<MaintenanceSetting> allSettings = maintenanceTaskDao.getSettingsForMaintenanceTaskType(taskName);
        MaintenanceSetting settings = allSettings.stream().filter(setting -> setting.getAttribute() == type).findAny().get();
        return settings.getAttributeValue();
    }

    @Autowired
    public void setMaintenanceTasks(List<MaintenanceTask> listOfMaintenanceTask) {
        for (MaintenanceTask maintenanceTask : listOfMaintenanceTask) {
            maintenanceTaskMap.put(maintenanceTask.getMaintenanceTaskType(), maintenanceTask);
        }
    }

}
