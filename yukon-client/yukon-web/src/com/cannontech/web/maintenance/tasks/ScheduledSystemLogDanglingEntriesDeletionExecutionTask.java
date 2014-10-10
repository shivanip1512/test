package com.cannontech.web.maintenance.tasks;


import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.core.dao.DatabaseManagementDao;
import com.cannontech.jobs.support.YukonTaskBase;

public class ScheduledSystemLogDanglingEntriesDeletionExecutionTask extends YukonTaskBase {
	@Autowired private DatabaseManagementDao databaseManagementDao;
	@Autowired private SystemEventLogService systemEventLogService;

    @Override
    public void start() {
        Instant start = new Instant();
        int numDeleted = databaseManagementDao.deleteSystemLogDanglingEntries();
        Instant finish = new Instant();
        systemEventLogService.systemLogDeleteDanglingEntries(numDeleted, start, finish);
    }
}
