package com.cannontech.maintenance.service;

import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;

import com.cannontech.maintenance.MaintenanceScheduler;
import com.cannontech.maintenance.MaintenanceSettingType;
import com.cannontech.maintenance.task.MaintenanceTask;

public interface MaintenanceTaskService {

    /**
     * Return all enabled maintenance tasks for a scheduler.
     **/
    List<MaintenanceTask> getEnabledMaintenanceTasks(MaintenanceScheduler scheduler);

    /**
     * Return value for given maintenance task and setting type.
     **/
    Object getMaintenanceSettings(MaintenanceSettingType type);

    /**
     * Return next available interval with at least some duration available and starting after a given time
     **/
     Interval getNextAvailableRunTime(Instant nowTime, Duration minimumrunwindow) throws Exception;
    
}
