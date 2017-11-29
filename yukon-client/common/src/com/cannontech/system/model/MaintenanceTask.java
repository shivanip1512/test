package com.cannontech.system.model;

import com.cannontech.maintenance.MaintenanceTaskType;

public class MaintenanceTask {

    private int taskId;
    private MaintenanceTaskType taskName;
    private boolean disabled;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public MaintenanceTaskType getTaskName() {
        return taskName;
    }

    public void setTaskName(MaintenanceTaskType taskName) {
        this.taskName = taskName;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

}
