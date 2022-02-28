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
        // Return array of [numDeleted, lastChangeId]
        Integer[] returnArray = dataPruningService.deleteDuplicatePointData(endOfTimeSlice);
        if (returnArray[0] <= 1000) {
            return true;
        }
        return false;
    }

    @Override
    public MaintenanceTaskType getMaintenanceTaskType() {
        return MaintenanceTaskType.DUPLICATE_POINT_DATA_PRUNING;
    }

}
