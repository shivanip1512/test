package com.cannontech.amr.scheduledGroupRequestExecution.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;
import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionStatusService;
import com.google.common.collect.Maps;

public class ScheduledGroupRequestExecutionStatusServiceImpl implements
        ScheduledGroupRequestExecutionStatusService {
    private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
    private Map<Integer, ScheduledGroupRequestExecutionStatus> cache = Maps.newHashMap();
    
    @Override
    public ScheduledGroupRequestExecutionStatus getStatus(int jobId) {
        ScheduledGroupRequestExecutionStatus status = cache.get(jobId);
        if (status != null) {
            return status;
        }
        status = scheduledGroupRequestExecutionDao.getStatusByJobId(jobId);
        cache.put(jobId, status);
        return status;
    }

    @Autowired
    public void setScheduledGroupRequestExecutionDao(ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
        this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
    }
}
