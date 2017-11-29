package com.cannontech.maintenance.task.impl;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.maintenance.MaintenanceTaskType;
import com.cannontech.maintenance.task.MaintenanceTask;
import com.cannontech.maintenance.task.service.PointDataPruningService;

public class PointDataPruningTask implements MaintenanceTask {

    @Autowired private PointDataPruningService dataPruningService;

    @Override
    public boolean doTask(Instant endOfTimeSlice) {
        // Do a small unit of work Log the work done return true if there's no more work to do
        int numDeleted = dataPruningService.deletePointDataSql(endOfTimeSlice);
        if (numDeleted == 0) {
            return true;
        }
        return false;

    }

    @Override
    public MaintenanceTaskType getMaintenanceTaskType() {
        return MaintenanceTaskType.POINT_DATA_PRUNING;
    }

}
