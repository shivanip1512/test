package com.cannontech.dr.eatonCloud.job.service.impl;

import static com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus.FAILED_WILL_RETRY;
import static com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus.UNKNOWN;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobPollService;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobReadService;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobResponseProcessor;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobSmartNotifService;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudJobStatusResponseV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;
import com.cannontech.dr.recenteventparticipation.dao.RecentEventParticipationDao;

public class EatonCloudJobPollServiceImpl implements EatonCloudJobPollService {
    
    private static final Logger log = YukonLogManager.getLogger(EatonCloudJobPollServiceImpl.class);
    
    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired EatonCloudJobReadService eatonCloudJobReadService;
    @Autowired private EatonCloudCommunicationServiceV1 eatonCloudCommunicationService;
    @Autowired private EatonCloudJobResponseProcessor eatonCloudJobResponseProcessor;
    @Autowired private RecentEventParticipationDao recentEventParticipationDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private EatonCloudJobSmartNotifService eatonCloudJobSmartNotifService;

    @Override
    public void immediatePoll(EventSummary summary, List<String> jobGuids,
            Instant jobCreationTime, int currentTry) {
        poll(summary, jobGuids, jobCreationTime, currentTry);
    }

    @Override
    public void schedulePoll(EventSummary summary, int minutes, int totalDevices, List<String> jobGuids,
            Instant jobCreationTime, int currentTry) {
        if (CollectionUtils.isEmpty(jobGuids)) {
            return;
        }
        executor.schedule(() -> {
            try {
                int successes = poll(summary, jobGuids, jobCreationTime, currentTry);
                // consider all devices that didn't succeed as failure
                if(currentTry == 1) {
                    eatonCloudJobSmartNotifService.sendSmartNotifications(summary, totalDevices, totalDevices - successes);
                }
            } catch (Exception e) {
                log.error("Error polling", e);
            }
        }, minutes, TimeUnit.MINUTES);
    }

    /**
     * event id -> multiple job guids
     * for each job guid
     * 1. Send message to Eaton Cloud to ask for device statuses
     * 2. Remove already processed (recent participation was updated) device guids
     * 3. Process success and failure by updating recent participation
     */
    private int poll(EventSummary summary, List<String> jobGuids, Instant jobCreationTime, int currentTry) {
        List<String> successes = new ArrayList<>();
        jobGuids.forEach(jobGuid -> {
            try {
                log.info(summary.getLogSummary(jobGuid, false) + "Try:{}", currentTry);
                EatonCloudJobStatusResponseV1 response = eatonCloudCommunicationService.getJobStatus(jobGuid);
                log.info(summary.getLogSummary(jobGuid, false) + "Try:{} successes:{} failures:{}",
                        currentTry,
                        response.getSuccesses() == null ? 0 : response.getSuccesses().size(),
                        response.getFailures() == null ? 0 : response.getFailures().size());
                Map<String, Integer> guidsToDeviceIds = getDeviceIdsForGuids(response);
                removeProcessedDeviceGuids(summary, response, guidsToDeviceIds);
                processSuccesses(summary, successes, jobGuid, response, guidsToDeviceIds, currentTry);
                processFailure(summary, jobGuid, response, guidsToDeviceIds, currentTry);
                log.info(summary.getLogSummary(jobGuid, false) + "Try:{} unprocessed successes:{} unprocessed failures:{}",
                        currentTry,
                        response.getSuccesses() == null ? 0 : response.getSuccesses().size(),
                        response.getFailures() == null ? 0 : response.getFailures().size());
            } catch (EatonCloudCommunicationExceptionV1 e) {
                log.error(summary.getLogSummary(jobGuid, false) + "Try:{} error polling devices job", currentTry, e);
            }
        }); 
        if(!successes.isEmpty()) {
            eatonCloudJobReadService.setupDeviceRead(summary, jobCreationTime, currentTry);
        }       
        return successes.size();
    }

    private void processFailure(EventSummary summary, String jobGuid, EatonCloudJobStatusResponseV1 response,
            Map<String, Integer> guidsToDeviceIds, int currentTry) {
        if (!CollectionUtils.isEmpty(response.getFailures())) {
            response.getFailures().forEach((deviceGuid, error) -> {
                int deviceError = 0;
                try {
                    deviceError = Integer.valueOf(error.getErrorNumber());
                } catch (Exception e) {
                    log.error(summary.getLogSummary(jobGuid, false) + "unable to parse error code:{}", error.getErrorNumber(), e);
                }
                eatonCloudJobResponseProcessor.processError(summary,
                        guidsToDeviceIds.get(deviceGuid), deviceGuid, jobGuid, deviceError,
                        ControlEventDeviceStatus.FAILED_WILL_RETRY, currentTry);
            });
        }
    }

    private void processSuccesses(EventSummary summary, List<String> successes, String jobGuid,
            EatonCloudJobStatusResponseV1 response, Map<String, Integer> guidsToDeviceIds, int currentTry) {
        if (!CollectionUtils.isEmpty(response.getSuccesses())) {
            successes.addAll(response.getSuccesses());
            response.getSuccesses()
                    .forEach(success -> eatonCloudJobResponseProcessor.processSuccess(summary,
                            guidsToDeviceIds.get(success), success, jobGuid, currentTry));
        }
    }

    private void removeProcessedDeviceGuids(EventSummary summary, EatonCloudJobStatusResponseV1 response,
            Map<String, Integer> guidsToDeviceIds) {
        Set<Integer> devices = recentEventParticipationDao.getDeviceIdsByExternalEventIdAndStatuses(summary.getEventId(),
                List.of(FAILED_WILL_RETRY, UNKNOWN));
        if (!CollectionUtils.isEmpty(response.getSuccesses())) {
            response.getSuccesses()
                    .removeIf(deviceGuid -> !devices.contains(guidsToDeviceIds.get(deviceGuid)));
        }
        if (!CollectionUtils.isEmpty(response.getFailures())) {
            response.getFailures().keySet()
                    .removeIf(deviceGuid -> !devices.contains(guidsToDeviceIds.get(deviceGuid)));
        }
    }

    private Map<String, Integer> getDeviceIdsForGuids(EatonCloudJobStatusResponseV1 response) {
        List<String> guids = new ArrayList<>();
        if (!CollectionUtils.isEmpty(response.getSuccesses())) {
            guids.addAll(response.getSuccesses());
        }
        if (!CollectionUtils.isEmpty(response.getFailures())) {
            guids.addAll(response.getFailures().keySet());
        }
        return deviceDao.getDeviceIds(guids);
    }
}
