package com.cannontech.maintenance.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.cannontech.maintenance.MaintenanceSettingType;
import com.cannontech.maintenance.MaintenanceTaskType;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.model.MaintenanceSetting;
import com.cannontech.system.model.MaintenanceTask;

public interface MaintenanceTaskDao {

    /**
     * Return all maintenance tasks.
     **/
    List<MaintenanceTask> getMaintenanceTasks();

    /**
     * Returns a maintenance task with the given task type.
     **/
    MaintenanceTask getMaintenanceTask(MaintenanceTaskType taskName);

    /**
     * Updates all the settings provided.
     **/
    void updateSettings(List<MaintenanceSetting> settings);

    /**
     * Returns all global maintenance setting values and comments.
     **/
    Map<GlobalSettingType, Pair<Object, String>> getValuesAndComments();

    /**
     * Returns boolean value of the passed setting.
     */
    boolean getBooleanValue(MaintenanceSettingType setting);

    /**
     * Return all settings for a maintenance task type.
     **/
    List<MaintenanceSetting> getSettingsForTaskType(MaintenanceTaskType taskType);

    /**
     * Returns value of the passed setting.
     */
    Object getSettingValue(MaintenanceSettingType setting);

}
