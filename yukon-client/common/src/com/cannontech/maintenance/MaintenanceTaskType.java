package com.cannontech.maintenance;

public enum MaintenanceTaskType {

    DUPLICATE_POINT_DATA_PRUNING(MaintenanceScheduler.POINT_DATA_CLEANUP_SCHEDULER),
    POINT_DATA_PRUNING(MaintenanceScheduler.POINT_DATA_CLEANUP_SCHEDULER),
    DR_RECONCILIATION(MaintenanceScheduler.RFN_MESSAGE_SCHEDULER);

    private MaintenanceScheduler scheduler;

    private MaintenanceTaskType(MaintenanceScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public MaintenanceScheduler getScheduler() {
        return scheduler;
    }
}