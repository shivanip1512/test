package com.cannontech.maintenance.task.impl;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.maintenance.MaintenanceTaskName;
import com.cannontech.maintenance.task.MaintenanceTask;
import com.cannontech.maintenance.task.service.PointDataPruningService;

public class DuplicatePointDataPruningTask implements MaintenanceTask {

    @Autowired private PointDataPruningService dataPruningService;

    @Override
    public boolean doTask(Instant endOfTimeSlice) {
        int numDeleted = dataPruningService.deleteDuplicatePointData(endOfTimeSlice);
        if (numDeleted == 0) {
            return true;
        }
        return false;
    }

    @Override
    public MaintenanceTaskName getMaintenanceTaskName() {
        return MaintenanceTaskName.DUPLICATE_POINT_DATA_PRUNING;
    }

}
