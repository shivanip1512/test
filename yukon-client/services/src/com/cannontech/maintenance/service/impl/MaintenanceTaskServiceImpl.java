package com.cannontech.maintenance.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.maintenance.MaintenanceSettingType;
import com.cannontech.maintenance.MaintenanceTaskType;
import com.cannontech.maintenance.dao.MaintenanceTaskDao;
import com.cannontech.maintenance.service.MaintenanceTaskService;
import com.cannontech.maintenance.task.MaintenanceTask;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.model.MaintenanceSetting;

public class MaintenanceTaskServiceImpl implements MaintenanceTaskService {
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private MaintenanceTaskDao maintenanceTaskDao;
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
        // TODO get this value from global settings
        return Instant.now().plus(Duration.standardMinutes(10));
    }

    @Override
    public long getSecondsUntilRun() {
        // TODO get this value from global settings (scheduled time - current time)
        return 300;
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
