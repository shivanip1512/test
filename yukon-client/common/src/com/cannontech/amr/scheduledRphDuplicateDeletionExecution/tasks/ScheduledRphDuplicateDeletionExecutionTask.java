package com.cannontech.amr.scheduledRphDuplicateDeletionExecution.tasks;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.core.dao.RphManagementDao;
import com.cannontech.jobs.support.YukonTaskBase;

public class ScheduledRphDuplicateDeletionExecutionTask extends YukonTaskBase {
    private RphManagementDao rphManagementDao;
    private SystemEventLogService systemEventLogService;

    @Override
    public void start() {
        Instant start = new Instant();
        int numDeleted = rphManagementDao.deleteDuplicates();
        Instant finish = new Instant();
        systemEventLogService.rphDeleteDuplicates(numDeleted, start, finish);
    }

    @Autowired
    public void setRphManagementDao(RphManagementDao rphManagementDao) {
        this.rphManagementDao = rphManagementDao;
    }
    @Autowired
    public void setSystemEventLogService(SystemEventLogService systemEventLogService) {
        this.systemEventLogService = systemEventLogService;
    }
}
