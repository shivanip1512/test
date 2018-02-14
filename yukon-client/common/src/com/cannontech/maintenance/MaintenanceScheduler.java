package com.cannontech.maintenance;

import static com.cannontech.maintenance.MaintenanceTaskType.*;
import com.google.common.collect.ImmutableSet;

public enum MaintenanceScheduler {

    POINT_DATA_CLEANUP_SCHEDULER(DUPLICATE_POINT_DATA_PRUNING, POINT_DATA_PRUNING),
    RFN_MESSAGE_SCHEDULER(DR_RECONCILIATION);

    private ImmutableSet<MaintenanceTaskType> tasks;

    private MaintenanceScheduler(MaintenanceTaskType... tasks) {
        this.tasks = ImmutableSet.copyOf(tasks);
    }

    public ImmutableSet<MaintenanceTaskType> getTasks() {
        return tasks;
    }
}