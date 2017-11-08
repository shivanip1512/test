package com.cannontech.maintenance.dao;

import java.util.Map;

import com.cannontech.maintenance.MaintenanceTasks;
import com.cannontech.maintenance.MaintenanceTasksSettings;

public interface MaintenanceTaskDao {
    /**
     * Return the settings for a particular task.
     * 
     **/
    Map<MaintenanceTasksSettings, String> getTaskSettings(MaintenanceTasks taskName);

}
