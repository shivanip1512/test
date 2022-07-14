package com.cannontech.dr.eatonCloud.job.service;

import org.joda.time.Instant;

import com.cannontech.dr.eatonCloud.job.service.impl.EventRestoreSummary;
import com.cannontech.dr.eatonCloud.job.service.impl.EventSummary;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;

public interface EatonCloudJobResponseProcessor {
    /**
     * On the start-up marks devices waiting for retry (FAILED_WILL_RETRY, UNKNOWN) as failed (FAILED)
     */
    void failDevicesOnStartup();
    /**
     * Marks devices as FAILED_WILL_RETRY, creates event log
     * @param jobCreationTime 
     */
    void processError(EventSummary summary, Integer deviceId, String guid, String jobGuid, int code,
            ControlEventDeviceStatus status, int currentTry, Instant jobCreationTime);
    /**
     * Marks devices as SUCCESS_RECEIVED, creates event log
     */
    void processSuccess(EventSummary summary, Integer deviceId, String guid, String jobGuid, int currentTry,
            Instant jobCreationTime);
   
    /**
     * Creates event log entry for successful restore
     */
    void processSuccess(EventRestoreSummary summary, Integer deviceId, String guid, String jobGuid);
    
    /**
     * Creates event log entry for failed restore
     */
    void processError(EventRestoreSummary summary, Integer deviceId, String guid, String jobGuid, int code);
}
