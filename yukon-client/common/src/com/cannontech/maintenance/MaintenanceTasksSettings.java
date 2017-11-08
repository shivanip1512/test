package com.cannontech.maintenance;

/**
 * This enum will have settings for each maintenance task.
 */

public enum MaintenanceTasksSettings {

    NO_OF_MONTHS(MaintenanceTasks.POINT_DATA_PRUNING);

    private MaintenanceTasks task;

    private MaintenanceTasksSettings(MaintenanceTasks task) {
        this.task = task;
    }

    public MaintenanceTasks getTask() {
        return task;
    }
}
