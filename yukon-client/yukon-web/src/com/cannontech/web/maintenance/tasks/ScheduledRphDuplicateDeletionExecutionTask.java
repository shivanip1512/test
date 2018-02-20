package com.cannontech.web.maintenance.tasks;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.core.dao.DatabaseManagementDao;
import com.cannontech.jobs.support.YukonTaskBase;

public class ScheduledRphDuplicateDeletionExecutionTask extends YukonTaskBase {
    @Autowired private DatabaseManagementDao databaseManagementDao;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private ConfigurationSource configurationSource;

    @Override
    public void start() {
        // TODO This is done just to inactive Duplicate Deletion Task (YUK-17919).
        boolean devMode = configurationSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE);
        if (devMode) {
            Instant start = new Instant();
            int numDeleted = databaseManagementDao.deleteRphDuplicates();
            Instant finish = new Instant();
            systemEventLogService.rphDeleteDuplicates(numDeleted, start, finish);
        }
    }
}
