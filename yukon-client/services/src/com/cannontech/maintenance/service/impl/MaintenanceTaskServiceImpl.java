package com.cannontech.maintenance.service.impl;

import java.util.Date;
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
                // TODO This case need to handle under YUK-17664. 
            } else {
                // Come here when both are selected.
                // select next businessHourStartTime or externalMaintenanceHourStartTime, whichever start first
                boolean isExtMaintenanceFirst = businessHourStartTime.isAfter(extMaintenanceHourStartTime);
                endOfRunWindow = isExtMaintenanceFirst ? extMaintenanceHourStartTime : businessHourStartTime;
                instant = endOfRunWindow.toInstant();
            }
        } catch (Exception e) {
            log.error("Unable to find run window for maintenance task", e);
        }
        log.info("Maintenance task will end at " + instant);
        return instant;
    }

    @Override
    public long getSecondsUntilRun() {
        DateTime nextMaintenanceRunTime = null;
        try {
            nextMaintenanceRunTime = maintenanceHelper.getNextRunTime();
        } catch (Exception e) {
            log.error("Unable to find run window for maintenance task", e);
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
