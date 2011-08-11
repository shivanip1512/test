package com.cannontech.amr.scheduledRphDuplicateDeletionExecution.tasks;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.core.dao.RphManagementDao;
import com.cannontech.jobs.support.YukonTaskBase;

public class ScheduledRphDuplicateDeletionExecutionTask extends YukonTaskBase {
    private RphManagementDao rphManagementDao;
    private SystemEventLogService systemEventLogService;

    @Override
    public void start() {
        DateTime start = new DateTime(getJobContext().getJob().getUserContext().getJodaTimeZone());
        int numDeleted = rphManagementDao.deleteDuplicates();
        DateTime finish = new DateTime(getJobContext().getJob().getUserContext().getJodaTimeZone());
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
