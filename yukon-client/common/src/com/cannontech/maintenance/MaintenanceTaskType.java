package com.cannontech.maintenance;

import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * Maintenance task which will run as single unit.
 */
public enum MaintenanceTaskType {
    POINT_DATA_PRUNING(MaintenanceSettingType.NO_OF_MONTHS), 
    DUPLICATE_POINT_DATA_PRUNING();

    private static final ImmutableMap<MaintenanceTaskType, Set<MaintenanceSettingType>> maintenanceTaskSettingMapping;
    private Set<MaintenanceSettingType> maintenanceSettingTypes;

    private MaintenanceTaskType(MaintenanceSettingType... maintenanceSettingTypes) {
        this.maintenanceSettingTypes = Sets.newHashSet(maintenanceSettingTypes);
    }

    static {
        final ImmutableMap.Builder <MaintenanceTaskType, Set<MaintenanceSettingType>> taskTypebuilder = ImmutableMap.builder();
        for (MaintenanceTaskType taskType : values()) {
            taskTypebuilder.put(taskType, taskType.maintenanceSettingTypes);
        }
        maintenanceTaskSettingMapping = taskTypebuilder.build();

    }

    public static ImmutableMap<MaintenanceTaskType, Set<MaintenanceSettingType>> getMaintenanceTaskSettingMapping() {
        return maintenanceTaskSettingMapping;
    }

    public static Set<MaintenanceSettingType> getSettingsForTask(MaintenanceTaskType taskType) {
        return maintenanceTaskSettingMapping.get(taskType);
    }
}
