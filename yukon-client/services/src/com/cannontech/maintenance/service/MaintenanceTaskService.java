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
    List<MaintenanceTask> getEnabledMaintenanceTasks();

    /**
     * Return end time for maintenance tasks
     **/
    Instant getEndOfRunWindow();

    /**
     * Return start time of maintenance tasks in milliseconds
     **/
    long getMillisecondsUntilRun();

    /**
     * Return value for given maintenance task and setting type.
     **/
    Object getMaintenanceSettings(MaintenanceSettingType type);
    
    /**
     * Returns enabled maintenance tasks types.
     */
    List<MaintenanceTaskType> getEnabledMaintenanceTaskTypes();
}
