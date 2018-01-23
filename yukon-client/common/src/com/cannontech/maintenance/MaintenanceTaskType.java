package com.cannontech.maintenance;

import java.util.Set;

import com.cannontech.database.YNBoolean;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * Maintenance task which will run as single unit.
 */
public enum MaintenanceTaskType {
    POINT_DATA_PRUNING(YNBoolean.YES, MaintenanceSettingType.NO_OF_MONTHS), 
    DUPLICATE_POINT_DATA_PRUNING(YNBoolean.YES),
    DR_RECONCILIATION(YNBoolean.NO);

    private static final ImmutableMap<MaintenanceTaskType, Set<MaintenanceSettingType>> maintenanceTaskSettingMapping;
    private Set<MaintenanceSettingType> maintenanceSettingTypes;
    private YNBoolean isDisabledByDefault;

    private MaintenanceTaskType(YNBoolean isDisabledByDefault, MaintenanceSettingType... maintenanceSettingTypes) {
        this.maintenanceSettingTypes = Sets.newHashSet(maintenanceSettingTypes);
        this.isDisabledByDefault = isDisabledByDefault;
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
    
    public YNBoolean isDisabled() {
        return isDisabledByDefault;
    }
    
}
