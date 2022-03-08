package com.cannontech.maintenance.task.impl;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.maintenance.MaintenanceTaskType;
import com.cannontech.maintenance.task.MaintenanceTask;
import com.cannontech.maintenance.task.service.PointDataPruningService;

public class DuplicatePointDataPruningTask implements MaintenanceTask {

    @Autowired private PointDataPruningService dataPruningService;

    @Override
    public boolean doTask(Instant endOfTimeSlice) {
        int numDeleted = dataPruningService.deleteDuplicatePointData(endOfTimeSlice);
        return numDeleted <= 1000;
    }

    @Override
    public MaintenanceTaskType getMaintenanceTaskType() {
        return MaintenanceTaskType.DUPLICATE_POINT_DATA_PRUNING;
    }

}
