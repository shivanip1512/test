package com.cannontech.maintenance.task.service.impl;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.util.Range;
import com.cannontech.maintenance.DurationType;
import com.cannontech.maintenance.MaintenanceSettingType;
import com.cannontech.maintenance.MaintenanceTaskType;
import com.cannontech.maintenance.service.MaintenanceTaskService;
import com.cannontech.maintenance.task.dao.PointDataPruningDao;
import com.cannontech.maintenance.task.service.PointDataPruningService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class PointDataPruningServiceImpl implements PointDataPruningService {
    @Autowired private PointDataPruningDao pointDataPruningDao;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private MaintenanceTaskService maintenanceTaskService;
    @Autowired private GlobalSettingDao globalSettingDao;

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
        int totalRowsdeleted = 0;
        int noOfMonths = globalSettingDao.getInteger(GlobalSettingType.RFN_INCOMING_DATA_TIMESTAMP_LIMIT);
        Duration monthInDuration = Period.months(noOfMonths).toDurationTo(Instant.now());
        Instant fromTimestamp = Instant.now();
        Instant limit = Instant.now().minus(monthInDuration);
        Instant start = new Instant();
        while (isEnoughTimeAvailable(processEndTime) && fromTimestamp.isAfter(limit)) {
            Duration weeklyDuration = Period.days(7).toDurationTo(fromTimestamp);
            Instant toTimestamp = fromTimestamp.minus(weeklyDuration);
            Range<Instant> dateRange = Range.inclusive(toTimestamp, fromTimestamp);
            int rowsDeleted = pointDataPruningDao.deleteDuplicatePointData(dateRange);
            totalRowsdeleted = totalRowsdeleted + rowsDeleted;
            fromTimestamp = toTimestamp;
        }
        Instant finish = new Instant();
        systemEventLogService.deleteDuplicatePointDataEntries(totalRowsdeleted, start, finish);
        return totalRowsdeleted;
    }
}
