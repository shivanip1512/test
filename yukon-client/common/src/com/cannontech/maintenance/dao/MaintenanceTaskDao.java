package com.cannontech.maintenance.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.maintenance.MaintenanceTaskName;
import com.cannontech.maintenance.MaintenanceTaskSettings;
import com.cannontech.system.model.MaintenanceSetting;
import com.cannontech.system.model.MaintenanceTask;

public interface MaintenanceTaskDao {
    /**
     * Return the settings for a particular task.
     * 
     **/
    Map<MaintenanceTaskSettings, String> getTaskSettings(MaintenanceTaskName taskName);

    /**
     * Return all maintenance task if includeDisabledTask is true otherwise it will return only enable
     * maintenance task
     **/
    List<MaintenanceTaskName> getMaintenanceTaskNames(boolean includeDisabledTask);

    /**
     * Returns all maintenance tasks if includeDisabledTask is true otherwise it will return only the ones
     * that are enabled.
     **/
    List<MaintenanceTask> getMaintenanceTasks(boolean includeDisabledTask);

    /**
     * Returns a maintenance task with the given task name.
     **/
    MaintenanceTask getMaintenanceTask(MaintenanceTaskName taskName);

    /**
     * Returns a maintenance task with the given taskId.
     **/
    MaintenanceTask getMaintenanceTaskById(int taskId);

    /**
     * Updates status for the given task.
     **/
    void updateTaskStatus(MaintenanceTask task);

    /**
     * Returns all settings for the given maintenance task.
     **/
    List<MaintenanceSetting> getSettingsForMaintenanceTaskName(MaintenanceTaskName task);

    /**
     * Updates all the settings provided.
     **/
    void updateSettings(List<MaintenanceSetting> settings);

}
