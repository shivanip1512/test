package com.cannontech.maintenance.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.maintenance.MaintenanceTaskName;
import com.cannontech.maintenance.MaintenanceTasksSettings;

public interface MaintenanceTaskDao {
    /**
     * Return the settings for a particular task.
     * 
     **/
    Map<MaintenanceTasksSettings, String> getTaskSettings(MaintenanceTaskName taskName);

    /**
     * Return all maintenance task if includeDisabledTask is true otherwise it will return only enable
     * maintenance task
     **/
    List<MaintenanceTaskName> getMaintenanceTaskNames(boolean includeDisabledTask);

}
