package com.cannontech.amr.scheduledRphDuplicateDeletionExecution.tasks;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.SystemAdminEventLogService;
import com.cannontech.core.dao.RphManagementDao;
import com.cannontech.jobs.support.YukonTaskBase;

public class ScheduledRphDuplicateDeletionExecutionTask extends YukonTaskBase {
    private RphManagementDao rphManagementDao;
    private SystemAdminEventLogService systemAdminEventLogService;

    @Override
    public void start() {
        int result = rphManagementDao.deleteDuplicates();
        systemAdminEventLogService.delete(result);
    }

    @Autowired
    public void setRphManagementDao(RphManagementDao rphManagementDao) {
        this.rphManagementDao = rphManagementDao;
    }
    @Autowired
    public void setSystemAdminEventLogService(SystemAdminEventLogService systemAdminEventLogService) {
        this.systemAdminEventLogService = systemAdminEventLogService;
    }
}
