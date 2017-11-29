package com.cannontech.maintenance.task;

import org.joda.time.Instant;

import com.cannontech.maintenance.MaintenanceTaskType;

public interface MaintenanceTask {
    /**
     * Perform task execution
     **/
    public boolean doTask(Instant processEndTime);

    /**
     * Return all enabled Maintenance Task Name.
     **/
    public MaintenanceTaskType getMaintenanceTaskType();
}
