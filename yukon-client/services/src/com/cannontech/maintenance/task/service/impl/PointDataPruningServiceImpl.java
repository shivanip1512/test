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
        log.info("Point data deletion started will delete data up to {}", deleteUpto);
        while (isEnoughTimeAvailable(processEndTime) && numDeleted != 0) {
            log.trace("Point data deletion started");
            numDeleted = pointDataPruningDao.deletePointData(deleteUpto);
            log.trace("Point data deletion ended by deleting {} records", numDeleted);
            // Total number of entries deleted during Point data deletion task.
            totalEntriesDeleted += numDeleted;
        }
        Instant finish = new Instant();
        log.info("Point data deletion finished. Deleted {} records.", totalEntriesDeleted);
        systemEventLogService.deletePointDataEntries(totalEntriesDeleted, start, finish);
        return totalEntriesDeleted;
    }

    private boolean isEnoughTimeAvailable(Instant processEndTime) {
        return Instant.now().isBefore(processEndTime.minus(minimumExecutionTime));
    }

    @Override
    public int deleteDuplicatePointData(Instant processEndTime) {
        int totalRowsdeleted = 0;
        boolean noLockRequired =
            configurationSource.getBoolean(MasterConfigBoolean.MAINTENANCE_DUPLICATE_POINT_DATA_NOLOCK_REQUIRED, true);
        int duplicatePointDeletionLimit =
            configurationSource.getInteger(MasterConfigInteger.MAINTENANCE_DUPLICATE_POINT_DATA_DELETE_DURATION, 60);
        int incomingDataLimit = globalSettingDao.getInteger(GlobalSettingType.RFN_INCOMING_DATA_TIMESTAMP_LIMIT);
        // Forcing noOfMonths to max 12 in case user configures above value more than 12 months.
        int incomingDataLimitMonths = NumberUtils.min(incomingDataLimit, 12);

        log.debug("Configurations for duplicate point data deletion are - ");
        log.debug("MAINTENANCE_DUPLICATE_POINT_DATA_NOLOCK_REQUIRED : {}", noLockRequired);
        log.debug("MAINTENANCE_DUPLICATE_POINT_DATA_DELETE_DURATION : {}", duplicatePointDeletionLimit);
        log.debug("RFN_INCOMING_DATA_TIMESTAMP_LIMIT (max 12 months) : {}", incomingDataLimitMonths);

        Instant txnStart = Instant.now(); // Mark start time of the transaction
        Instant toTimestamp = Instant.now(); // End of deletion interval
        Duration duplicatePointDeletionDuration = Period.days(duplicatePointDeletionLimit).toDurationTo(toTimestamp);
        Duration incomindDataLimitDuration = Period.months(incomingDataLimitMonths).toDurationTo(toTimestamp);
        Duration startLimit = duplicatePointDeletionDuration.isLongerThan(incomindDataLimitDuration) ? 
                duplicatePointDeletionDuration : incomindDataLimitDuration;
        Instant fromTimestamp = toTimestamp.minus(startLimit); // Beginning of deletion interval 

        log.info("Duplicate point data deletion started.");
        log.info("Duration for which duplicate records should be deleted = From {} To {}", fromTimestamp, toTimestamp);
        while (isEnoughTimeAvailable(processEndTime) && fromTimestamp.compareTo(toTimestamp) < 0) {
            Instant nextTimestamp = fromTimestamp.plus(Duration.standardDays(7));
            if (nextTimestamp.compareTo(toTimestamp) > 0) {
                nextTimestamp = toTimestamp;
            }
            Range<Instant> fromNextRange = Range.inclusive(fromTimestamp, nextTimestamp);
            int rowsDeleted = pointDataPruningDao.deleteDuplicatePointData(fromNextRange, noLockRequired);
            totalRowsdeleted = totalRowsdeleted + rowsDeleted;
            if (rowsDeleted == 0) {
                fromTimestamp = nextTimestamp;
            }
        }
        Instant txnFinish = Instant.now(); // Mark end time of the transaction
        Seconds secondsTaken = Seconds.secondsBetween(txnStart, txnFinish);
        log.info("Duplicate point data deletion finished. Deleted {} records in {} seconds", totalRowsdeleted, secondsTaken.getSeconds());
        systemEventLogService.rphDeleteDuplicates(totalRowsdeleted, txnStart, txnFinish);
        return totalRowsdeleted;
    }
}
