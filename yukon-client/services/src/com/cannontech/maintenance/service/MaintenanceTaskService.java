package com.cannontech.maintenance.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.maintenance.MaintenanceSettingType;
import com.cannontech.maintenance.MaintenanceTaskType;
import com.cannontech.maintenance.task.MaintenanceTask;

public interface MaintenanceTaskService {

    /**
     * Return all enabled maintenance tasks.
     **/
    List<MaintenanceTask> getMaintenanceTasks();

    /**
     * Return end time for maintenance tasks
     **/
    Instant getEndOfRunWindow();

    /**
     * Return start time of maintenance tasks in seconds
     **/
    long getSecondsUntilRun();

    /**
     * Return value for given maintenance task and setting type.
     **/
    Object getMaintenanceSettings(MaintenanceTaskType taskName, MaintenanceSettingType type);

}
