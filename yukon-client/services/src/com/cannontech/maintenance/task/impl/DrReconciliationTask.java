package com.cannontech.maintenance.task.impl;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.maintenance.MaintenanceTaskType;
import com.cannontech.maintenance.task.MaintenanceTask;
import com.cannontech.maintenance.task.service.DrReconciliationService;

public class DrReconciliationTask implements MaintenanceTask {

    @Autowired DrReconciliationService drReconciliationService;

    @Override
    public boolean doTask(Instant processEndTime) {
        return drReconciliationService.doDrReconcillation(processEndTime);
    }

    @Override
    public MaintenanceTaskType getMaintenanceTaskType() {
        return MaintenanceTaskType.DR_RECONCILIATION;
    }

}
