package com.cannontech.maintenance;

/**
 * This enum will have settings for each maintenance task.
 */

public enum MaintenanceTaskSettings {

    NO_OF_MONTHS(MaintenanceTaskName.POINT_DATA_PRUNING);

    private MaintenanceTaskName task;

    private MaintenanceTaskSettings(MaintenanceTaskName task) {
        this.task = task;
    }

    public MaintenanceTaskName getTask() {
        return task;
    }
}
