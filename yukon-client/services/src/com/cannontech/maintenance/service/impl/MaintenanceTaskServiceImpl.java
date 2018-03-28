package com.cannontech.maintenance.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.maintenance.MaintenanceHelper;
import com.cannontech.maintenance.MaintenanceScheduler;
import com.cannontech.maintenance.MaintenanceSettingType;
import com.cannontech.maintenance.MaintenanceTaskType;
import com.cannontech.maintenance.dao.MaintenanceTaskDao;
import com.cannontech.maintenance.service.MaintenanceTaskService;
import com.cannontech.maintenance.task.MaintenanceTask;

public class MaintenanceTaskServiceImpl implements MaintenanceTaskService {
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

    private List<MaintenanceTaskType> getEnabledMaintenanceTaskTypes(MaintenanceScheduler scheduler) {
        Set<MaintenanceTaskType> tasks = scheduler.getTasks();
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

    @Override
    public Interval getNextAvailableRunTime(Instant nowTime, Duration minimumrunwindow) throws Exception {
            return maintenanceHelper.getNextAvailableRunTime(nowTime, minimumrunwindow);
    }
}
