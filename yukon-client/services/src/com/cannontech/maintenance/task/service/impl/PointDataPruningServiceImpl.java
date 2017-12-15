package com.cannontech.maintenance.task.service.impl;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.maintenance.DurationType;
import com.cannontech.maintenance.MaintenanceSettingType;
import com.cannontech.maintenance.MaintenanceTaskType;
import com.cannontech.maintenance.service.MaintenanceTaskService;
import com.cannontech.maintenance.task.dao.PointDataPruningDao;
import com.cannontech.maintenance.task.service.PointDataPruningService;

public class PointDataPruningServiceImpl implements PointDataPruningService {
    @Autowired private PointDataPruningDao pointDataPruningDao;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private MaintenanceTaskService maintenanceTaskService;

    private long minimumExecutionTime = 300000;

    @Override
    public int deletePointData(Instant processEndTime) {
        DurationType noOfMonths = (DurationType) maintenanceTaskService.getMaintenanceSettings(MaintenanceTaskType.POINT_DATA_PRUNING, MaintenanceSettingType.NO_OF_MONTHS);
        Instant deleteUpto = Instant.now().toDateTime().minusMonths(noOfMonths.getDuration()).toInstant();
        
        int numDeleted = 1;
        while (isEnoughTimeAvailable(processEndTime) && numDeleted != 0) {
            Instant start = new Instant();
            numDeleted = pointDataPruningDao.deletePointData(deleteUpto);
            Instant finish = new Instant();
            systemEventLogService.deletePointDataEntries(numDeleted, start, finish);
        }
        return numDeleted;
    }

    private boolean isEnoughTimeAvailable(Instant processEndTime) {
        return Instant.now().isBefore(processEndTime.minus(minimumExecutionTime));
    }

    @Override
    public int deleteDuplicatePointData(Instant processEndTime) {
        Instant start = new Instant();
        int numDeleted = 0;
        // TODO : Add appropriate dao call here.
        //int numDeleted = databaseManagementDao.deleteRphDuplicates();
        Instant finish = new Instant();
        systemEventLogService.rphDeleteDuplicates(numDeleted, start, finish);
        return 0;
    }

}
