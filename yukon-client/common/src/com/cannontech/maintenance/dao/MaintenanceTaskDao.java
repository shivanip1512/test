package com.cannontech.maintenance.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.cannontech.maintenance.MaintenanceTaskType;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.model.MaintenanceSetting;
import com.cannontech.system.model.MaintenanceTask;

public interface MaintenanceTaskDao {

    /**
     * Return enable maintenance task if excludeDisabled is true otherwise it will return all
     * maintenance task type
     **/
    List<MaintenanceTaskType> getMaintenanceTaskTypes(boolean excludeDisabled);

    /**
     * Return enable maintenance task if excludeDisabled is true otherwise it will return all
     * maintenance task
     **/
    List<MaintenanceTask> getMaintenanceTasks(boolean includeDisabledTask);

    /**
     * Returns a maintenance task with the given task type.
     **/
    MaintenanceTask getMaintenanceTask(MaintenanceTaskType taskName);

    /**
     * Returns a maintenance task with the given taskId.
     **/
    MaintenanceTask getMaintenanceTaskById(int taskId);

    /**
     * Updates status for the given task.
     **/
    void updateTaskStatus(MaintenanceTask task);

    /**
     * Returns all settings for the given maintenance task type.
     **/
    List<MaintenanceSetting> getSettingsForMaintenanceTaskType(MaintenanceTaskType taskType);

    /**
     * Updates all the settings provided.
     **/
    void updateSettings(List<MaintenanceSetting> settings);

    /**
     * Returns all global maintenance setting values and comments.
     **/
    Map<GlobalSettingType, Pair<Object, String>> getValuesAndComments();

}
