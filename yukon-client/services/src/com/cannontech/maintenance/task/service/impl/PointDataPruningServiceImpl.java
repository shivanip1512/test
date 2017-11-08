package com.cannontech.maintenance.task.service.impl;

import java.util.Map;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.maintenance.MaintenanceTasks;
import com.cannontech.maintenance.MaintenanceTasksSettings;
import com.cannontech.maintenance.dao.impl.MaintenanceTaskDaoImpl;
import com.cannontech.maintenance.task.dao.PointDataPruningDao;
import com.cannontech.maintenance.task.service.PointDataPruningService;

public class PointDataPruningServiceImpl implements PointDataPruningService {
    @Autowired private PointDataPruningDao pointDataPruningDao;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private MaintenanceTaskDaoImpl maintenanceTaskDao;

    private long bufferDuration = 5;

    @Override
    public void deletePointDataSql(Instant processEndTime) {
        int noOfMonths = Integer.parseInt(
            getMaintenanceSettings(MaintenanceTasks.POINT_DATA_PRUNING, MaintenanceTasksSettings.NO_OF_MONTHS));
        Instant start = new Instant();
        Instant deleteUpto = Instant.now().toDateTime().minusMonths(noOfMonths).toInstant();
        int numDeleted = pointDataPruningDao.deletePointData(processEndTime, deleteUpto);
        Instant finish = new Instant();
        systemEventLogService.deletePointDataEntries(numDeleted, start, finish);
    }

    @Override
    public void deletePointData(Instant processEndTime) {
        int noOfMonths = Integer.parseInt(
            getMaintenanceSettings(MaintenanceTasks.POINT_DATA_PRUNING, MaintenanceTasksSettings.NO_OF_MONTHS));
        Instant deleteUpto = Instant.now().toDateTime().minusMonths(noOfMonths).toInstant();

        while (Instant.now().toDateTime().isAfter(processEndTime.minus(bufferDuration))) {
            Instant start = new Instant();
            int numDeleted = pointDataPruningDao.deletePointData(processEndTime, deleteUpto);
            Instant finish = new Instant();
            systemEventLogService.deletePointDataEntries(numDeleted, start, finish);
        }
    }

    private String getMaintenanceSettings(MaintenanceTasks taskName, MaintenanceTasksSettings property) {
        Map<MaintenanceTasksSettings, String> settings = maintenanceTaskDao.getTaskSettings(taskName);
        return settings.get(property);
    }
}
