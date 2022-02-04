package com.cannontech.maintenance.task.service.impl;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MasterConfigInteger;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.util.Range;
import com.cannontech.maintenance.DurationType;
import com.cannontech.maintenance.MaintenanceSettingType;
import com.cannontech.maintenance.service.MaintenanceTaskService;
import com.cannontech.maintenance.task.dao.PointDataPruningDao;
import com.cannontech.maintenance.task.service.PointDataPruningService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class PointDataPruningServiceImpl implements PointDataPruningService {

    private final static Logger log = YukonLogManager.getLogger(PointDataPruningServiceImpl.class);

    @Autowired private PointDataPruningDao pointDataPruningDao;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private MaintenanceTaskService maintenanceTaskService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ConfigurationSource configurationSource;

    private long minimumExecutionTime = 300000; // Time in ms i.e. 5 minute

    @Override
    public int deletePointData(Instant processEndTime) {
        DurationType noOfMonths = (DurationType) maintenanceTaskService.getMaintenanceSettings(
            MaintenanceSettingType.POINT_DATA_PRUNING_NO_OF_MONTHS);
        Instant deleteUpto = Instant.now().toDateTime().minusMonths(noOfMonths.getDuration()).toInstant();
        
        int numDeleted = 1;
        int totalEntriesDeleted = 0;
        Instant start = new Instant();
        log.info("Point data deletion started will delete data up to " + deleteUpto);
        while (isEnoughTimeAvailable(processEndTime) && numDeleted != 0) {
            log.trace("Point data deletion started");
            numDeleted = pointDataPruningDao.deletePointData(deleteUpto);
            log.trace("Point data deletion ended by deleting " + numDeleted + " records");
            // Total number of entries deleted during Point data deletion task.
            totalEntriesDeleted += numDeleted;
        }
        Instant finish = new Instant();
        log.info("Point data deletion finished. Deleted " + totalEntriesDeleted + " records.");
        systemEventLogService.deletePointDataEntries(totalEntriesDeleted, start, finish);
        return totalEntriesDeleted;
    }

    private boolean isEnoughTimeAvailable(Instant processEndTime) {
        return Instant.now().isBefore(processEndTime.minus(minimumExecutionTime));
    }

    @Override
    public Integer[] deleteDuplicatePointData(Instant processEndTime) {
        int totalRowsdeleted = 0;
        boolean noLockRequired =
            configurationSource.getBoolean(MasterConfigBoolean.MAINTENANCE_DUPLICATE_POINT_DATA_NOLOCK_REQUIRED, true);
        int lastChangeId = 0;
        Integer[] returnArray = new Integer[] {totalRowsdeleted, lastChangeId};
        Integer daysInDuration =
            configurationSource.getInteger(MasterConfigInteger.MAINTENANCE_DUPLICATE_POINT_DATA_DELETE_DURATION, 60);
        int noOfMonthsConfigured = globalSettingDao.getInteger(GlobalSettingType.RFN_INCOMING_DATA_TIMESTAMP_LIMIT);
        // Forcing noOfMonths to max 12 in case user configures above value more than 12 months.
        int noOfMonths = NumberUtils.min(noOfMonthsConfigured, 12);
        log.debug("Configurations for duplicate point data deletion are - ");
        log.debug("MAINTENANCE_DUPLICATE_POINT_DATA_DELETE_DURATION : " + daysInDuration);
        log.debug("MAINTENANCE_DUPLICATE_POINT_DATA_NOLOCK_REQUIRED : " + noLockRequired);
        log.debug("No Of Months for which duplicate data is to be deleted : " + noOfMonths);
        Instant start = Instant.now();
        Duration monthInDuration = Period.months(noOfMonths).toDurationTo(start);
        Instant fromTimestamp = Instant.now();
        Instant limit = start.minus(monthInDuration);
        log.debug(
            "Overall duration for which duplicate records should be deleted = From " + limit + " To " + fromTimestamp);
        log.info("Duplicate point data deletion started.");
        while (isEnoughTimeAvailable(processEndTime) && fromTimestamp.isAfter(limit)) {
            Duration deletionDuration = Period.days(daysInDuration).toDurationTo(fromTimestamp);
            Instant toTimestamp = fromTimestamp.minus(deletionDuration);
            Range<Instant> dateRange = Range.inclusive(toTimestamp, fromTimestamp);
            // returns array of [rowsDeleted, lastChangeId]
            returnArray = pointDataPruningDao.deleteDuplicatePointData(dateRange, noLockRequired, lastChangeId);
            totalRowsdeleted = totalRowsdeleted + returnArray[0];
            if (returnArray[0] == 0) {
                fromTimestamp = toTimestamp;
            }
            if (returnArray[1] != null) {
                lastChangeId = returnArray[1];
            }
        }
        Instant finish = new Instant();
        Seconds secondsTaken = Seconds.secondsBetween(start, finish);
        log.info("Duplicate point data deletion finished. Deleted " + totalRowsdeleted + " records in "
            + secondsTaken.getSeconds() + " seconds");
        systemEventLogService.rphDeleteDuplicates(totalRowsdeleted, start, finish);
        return returnArray;
    }
}
