package com.cannontech.maintenance;

import static com.cannontech.maintenance.MaintenanceSettingType.*;

import com.google.common.collect.ImmutableSet;

public enum MaintenanceTaskType {

    DUPLICATE_POINT_DATA_PRUNING(DUPLICATE_POINT_DATA_PRUNING_ENABLED),
    POINT_DATA_PRUNING(POINT_DATA_PRUNING_ENABLED, POINT_DATA_PRUNING_NO_OF_MONTHS),
    DR_RECONCILIATION(DR_RECONCILIATION_ENABLED);

    private ImmutableSet<MaintenanceSettingType> settings;

    private MaintenanceTaskType(MaintenanceSettingType... settings) {
        this.settings = ImmutableSet.copyOf(settings);
    }

    public ImmutableSet<MaintenanceSettingType> getSettings() {
        return settings;
    }
}