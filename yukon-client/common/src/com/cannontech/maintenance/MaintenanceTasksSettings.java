package com.cannontech.maintenance;

/**
 * This enum will have settings for each maintenance task.
 */

public enum MaintenanceTasksSettings {

    NO_OF_MONTHS(MaintenanceTaskName.POINT_DATA_PRUNING);

    private MaintenanceTaskName task;

    private MaintenanceTasksSettings(MaintenanceTaskName task) {
        this.task = task;
    }

    public MaintenanceTaskName getTask() {
        return task;
    }
}
