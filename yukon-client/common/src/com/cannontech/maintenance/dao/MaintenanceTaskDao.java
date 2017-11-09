package com.cannontech.maintenance.dao;

import java.util.Map;

import com.cannontech.maintenance.MaintenanceTaskName;
import com.cannontech.maintenance.MaintenanceTasksSettings;

public interface MaintenanceTaskDao {
    /**
     * Return the settings for a particular task.
     * 
     **/
    Map<MaintenanceTasksSettings, String> getTaskSettings(MaintenanceTaskName taskName);

}
