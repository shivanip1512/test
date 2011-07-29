package com.cannontech.amr.scheduledGroupRequestExecution.service;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionBundle;

public interface ScheduledGroupRequestExecutionBundleService {
    public ScheduledGroupRequestExecutionBundle getScheduledGroupRequestExecution(Integer jobId);
}
