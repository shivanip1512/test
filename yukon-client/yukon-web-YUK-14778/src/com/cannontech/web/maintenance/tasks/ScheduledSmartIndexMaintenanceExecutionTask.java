package com.cannontech.web.maintenance.tasks;


import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.core.dao.DatabaseManagementDao;
import com.cannontech.jobs.support.YukonTaskBase;

public class ScheduledSmartIndexMaintenanceExecutionTask extends YukonTaskBase {
    private static final Logger log = YukonLogManager.getLogger(ScheduledSmartIndexMaintenanceExecutionTask.class);
    
	@Autowired private DatabaseManagementDao databaseManagementDao;
	@Autowired private SystemEventLogService systemEventLogService;

    @Override
    public void start() {
        Instant start = new Instant();
        try {
            databaseManagementDao.executeSpSmartIndexMaintanence();
            Instant finish = new Instant();
            systemEventLogService.smartIndexMaintenance(start, finish);
        } catch (DataAccessException e) {
            log.error("sp_SmartIndexMaintance encountered error.", e);
        }
    }
}
