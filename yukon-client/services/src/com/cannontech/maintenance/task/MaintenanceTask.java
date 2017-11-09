package com.cannontech.maintenance.task;

import org.joda.time.Instant;

import com.cannontech.maintenance.MaintenanceTaskName;

public interface MaintenanceTask {
    /**
     * Perform task execution
     **/
    public boolean doTask(Instant processEndTime);

    /**
     * Return all enabled Maintenance Task Name.
     **/
    public MaintenanceTaskName getMaintenanceTaskName();
}
