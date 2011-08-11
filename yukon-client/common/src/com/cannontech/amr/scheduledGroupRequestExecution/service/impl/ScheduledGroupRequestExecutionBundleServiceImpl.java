package com.cannontech.amr.scheduledGroupRequestExecution.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupExecutionCounts;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionBundle;
import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionBundleService;
import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionStatusService;
import com.google.common.collect.Maps;

public class ScheduledGroupRequestExecutionBundleServiceImpl implements ScheduledGroupRequestExecutionBundleService {
    private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
    private ScheduledGroupRequestExecutionStatusService executionStatusService;
    private Map<Integer, ScheduledGroupRequestExecutionBundle> cache = Maps.newHashMap();
    
    public ScheduledGroupRequestExecutionBundle getScheduledGroupRequestExecution(int jobId) {
        ScheduledGroupRequestExecutionBundle executionBundle = cache.get(jobId);
        if (executionBundle != null) {
            return executionBundle;
        }
        ScheduledGroupRequestExecutionStatus status = executionStatusService.getStatus(jobId);
        ScheduledGroupExecutionCounts executionCounts = scheduledGroupRequestExecutionDao.getExecutionCountsForJobId(jobId);
        executionBundle = new ScheduledGroupRequestExecutionBundle(jobId, status, executionCounts);
        cache.put(jobId, executionBundle);
        return executionBundle;
    }

    @Autowired
    public void setScheduledGroupRequestExecutionDao(ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
        this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
    }
    @Autowired
    public void setExecutionStatusService(ScheduledGroupRequestExecutionStatusService executionStatusService) {
        this.executionStatusService = executionStatusService;
    }
}
