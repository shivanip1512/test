package com.cannontech.system.model;

import com.cannontech.maintenance.MaintenanceTaskType;

public class MaintenanceTask {

    private MaintenanceTaskType taskType;
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public MaintenanceTaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(MaintenanceTaskType taskType) {
        this.taskType = taskType;
    }

}
