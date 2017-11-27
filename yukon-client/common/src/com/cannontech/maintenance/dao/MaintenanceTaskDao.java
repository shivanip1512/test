package com.cannontech.maintenance.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.cannontech.maintenance.MaintenanceTaskName;
import com.cannontech.maintenance.MaintenanceTaskSettings;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.model.MaintenanceSetting;
import com.cannontech.system.model.MaintenanceTask;

public interface MaintenanceTaskDao {
    /**
     * Return the settings for a particular task.
     * 
     **/
    Map<MaintenanceTaskSettings, String> getTaskSettings(MaintenanceTaskName taskName);

    /**
     * Return enable maintenance task if excludeDisabled is true otherwise it will return all
     * maintenance task
     **/
    List<MaintenanceTaskName> getMaintenanceTaskNames(boolean excludeDisabled);

    /**
     * Return enable maintenance task if excludeDisabled is true otherwise it will return all
     * maintenance task
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

    /**
     * Returns all global maintenance setting values and comments.
     **/
    Map<GlobalSettingType, Pair<Object, String>> getValuesAndComments();

}
