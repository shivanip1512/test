package com.cannontech.dr.eatonCloud.job.service;

import com.cannontech.dr.eatonCloud.job.service.impl.EventSummary;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;

public interface EatonCloudJobResponseProcessor {
    /**
     * On the start-up marks devices waiting for retry (FAILED_WILL_RETRY, UNKNOWN) as failed (FAILED)
     */
    void failDevicesOnStartup();
    /**
     * Marks devices as FAILED_WILL_RETRY, creates event log
     */
    void processError(EventSummary summary, Integer deviceId, String guid, String jobGuid, String message,
            ControlEventDeviceStatus status);
    /**
     * Marks devices as SUCCESS_RECEIVED, creates event log
     */
    void processSuccess(EventSummary summary, Integer deviceId, String guid, String jobGuid);
}
