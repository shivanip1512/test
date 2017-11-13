package com.cannontech.system.model;

import com.cannontech.maintenance.MaintenanceTaskName;

public class MaintenanceTask {

    private int taskId;
    private MaintenanceTaskName taskName;
    private boolean disabled;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public MaintenanceTaskName getTaskName() {
        return taskName;
    }

    public void setTaskName(MaintenanceTaskName taskName) {
        this.taskName = taskName;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

}
