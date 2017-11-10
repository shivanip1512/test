package com.cannontech.maintenance.task.service.impl;

import java.util.Map;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.maintenance.MaintenanceTaskName;
import com.cannontech.maintenance.MaintenanceTaskSettings;
import com.cannontech.maintenance.dao.impl.MaintenanceTaskDaoImpl;
import com.cannontech.maintenance.task.dao.PointDataPruningDao;
import com.cannontech.maintenance.task.service.PointDataPruningService;

public class PointDataPruningServiceImpl implements PointDataPruningService {
    @Autowired private PointDataPruningDao pointDataPruningDao;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private MaintenanceTaskDaoImpl maintenanceTaskDao;

    private long minimumExecutionTime = 5;

    @Override
    public int deletePointDataSql(Instant processEndTime) {
        int noOfMonths = Integer.parseInt(
            getMaintenanceSettings(MaintenanceTaskName.POINT_DATA_PRUNING, MaintenanceTaskSettings.NO_OF_MONTHS));
        Instant start = new Instant();
        Instant deleteUpto = Instant.now().toDateTime().minusMonths(noOfMonths).toInstant();
        int numDeleted = pointDataPruningDao.deletePointData(processEndTime.minus(minimumExecutionTime), deleteUpto);
        Instant finish = new Instant();
        systemEventLogService.deletePointDataEntries(numDeleted, start, finish);
        return numDeleted;
    }

    @Override
    public int deletePointData(Instant processEndTime) {
        int noOfMonths = Integer.parseInt(
            getMaintenanceSettings(MaintenanceTaskName.POINT_DATA_PRUNING, MaintenanceTaskSettings.NO_OF_MONTHS));
        Instant deleteUpto = Instant.now().toDateTime().minusMonths(noOfMonths).toInstant();
        
        int numDeleted = 1;
        while (isEnoughTimeAvailable(processEndTime) && numDeleted != 0) {
            Instant start = new Instant();
            numDeleted = pointDataPruningDao.deletePointData(deleteUpto);
            Instant finish = new Instant();
            systemEventLogService.deletePointDataEntries(numDeleted, start, finish);
        }
        return numDeleted;
    }

    private String getMaintenanceSettings(MaintenanceTaskName taskName, MaintenanceTaskSettings property) {
        Map<MaintenanceTaskSettings, String> settings = maintenanceTaskDao.getTaskSettings(taskName);
        return settings.get(property);
    }
    
    private boolean isEnoughTimeAvailable(Instant processEndTime) {
        return Instant.now().isBefore(processEndTime.minus(minimumExecutionTime));
    }

}
