package com.cannontech.dr.eatonCloud.job.service.impl;

import static com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus.FAILED_WILL_RETRY;
import static com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus.UNKNOWN;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobPollService;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobReadService;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobResponseProcessor;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobSmartNotifService;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudJobStatusResponseV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;
import com.cannontech.dr.recenteventparticipation.dao.RecentEventParticipationDao;

public class EatonCloudJobPollServiceImpl extends EatonCloudJobPollServiceHelper implements EatonCloudJobPollService {

    private static final Logger log = YukonLogManager.getLogger(EatonCloudJobPollServiceImpl.class);

    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired EatonCloudJobReadService eatonCloudJobReadService;
    @Autowired private EatonCloudCommunicationServiceV1 eatonCloudCommunicationService;
    @Autowired private EatonCloudJobResponseProcessor eatonCloudJobResponseProcessor;
    @Autowired private RecentEventParticipationDao recentEventParticipationDao;
    @Autowired private EatonCloudJobSmartNotifService eatonCloudJobSmartNotifService;

    private Set<Integer> polling = ConcurrentHashMap.newKeySet();
    private Set<Integer> terminating = ConcurrentHashMap.newKeySet();

    @Override
    public void immediatePoll(EventSummary summary, List<String> jobGuids,
            Instant jobCreationTime, int currentTry) {
        poll(summary, jobGuids, jobCreationTime, currentTry, true);
    }

    @Override
    public void failWillRetryDevicesAfterLastPoll(EventSummary summary) {
        if (polling.contains(summary.getEventId())) {
            log.info(summary.getLogSummary(false) + " event termination is postponed untill POLL completes");
            terminating.add(summary.getEventId());
        } else {
            summary.failWillRetryDevices();
        }
    }

    @Override
    public void schedulePoll(EventSummary summary, int minutes, int totalDevices, List<String> jobGuids,
            Instant jobCreationTime, int currentTry) {
        if (CollectionUtils.isEmpty(jobGuids)) {
            return;
        }
        log.info(summary.getLogSummary(false) + "POLL Try:{} scheduling poll in {} minutes job guids{}.", currentTry, minutes,
                jobGuids);
        polling.add(summary.getEventId());
        executor.schedule(() -> {
            try {
                int successes = poll(summary, jobGuids, jobCreationTime, currentTry, false);
                // consider all devices that didn't succeed as failure
                if (currentTry == 1) {
                    eatonCloudJobSmartNotifService.sendSmartNotifications(summary.getProgramId(),
                            summary.getCommand().getGroupId(), totalDevices, totalDevices - successes, true,
                            summary.getLogSummary(false));
                }
            } catch (Exception e) {
                log.error("Error polling", e);
            }
            if (terminating.contains(summary.getEventId())) {
                log.info(summary.getLogSummary(false) + " event terminated POLL completed");
                summary.failWillRetryDevices();
                terminating.remove(summary.getEventId());
            }
            polling.remove(summary.getEventId());
        }, minutes, TimeUnit.MINUTES);
    }

    /**
     * event id -> multiple job guids
     * for each job guid
     * 1. Send message to Eaton Cloud to ask for device statuses
     * 2. Remove already processed (recent participation was updated) device guids
     * 3. Process success and failure by updating recent participation
     */
    private int poll(EventSummary summary, List<String> jobGuids, Instant jobCreationTime, int currentTry, boolean isImmediate) {
        List<String> successes = new ArrayList<>();
        String pollText = isImmediate ? "Immediate POLL Before " : "Scheduled POLL ";
        jobGuids.forEach(jobGuid -> {
            try {
                log.info(summary.getLogSummary(jobGuid, false) + pollText + "Try:{}", currentTry);
                EatonCloudJobStatusResponseV1 response = eatonCloudCommunicationService.getJobStatus(jobGuid);
                log.info(summary.getLogSummary(jobGuid, false) + pollText + "Try:{} successes:{} failures:{}",
                        currentTry,
                        response.getSuccesses() == null ? 0 : response.getSuccesses().size(),
                        response.getFailures() == null ? 0 : response.getFailures().size());
                Map<String, Integer> guidsToDeviceIds = getDeviceIdsForGuids(response);
                removeProcessedDeviceGuids(summary, response, guidsToDeviceIds);
                processSuccesses(summary, successes, jobGuid, response, guidsToDeviceIds, currentTry);
                processFailure(summary, jobGuid, response, guidsToDeviceIds, currentTry);
                log.info(
                        summary.getLogSummary(jobGuid, false)
                                + pollText + "Try:{} unprocessed successes:{} unprocessed failures:{}",
                        currentTry,
                        response.getSuccesses() == null ? 0 : response.getSuccesses().size(),
                        response.getFailures() == null ? 0 : response.getFailures().size());
            } catch (EatonCloudCommunicationExceptionV1 e) {
                log.error(summary.getLogSummary(jobGuid, false) + pollText + "Try:{} error polling devices job", currentTry,
                        e);
            }
        });
        if (!successes.isEmpty()) {
            eatonCloudJobReadService.setupDeviceRead(summary, jobCreationTime, currentTry);
        }
        return successes.size();
    }

    private void processFailure(EventSummary summary, String jobGuid, EatonCloudJobStatusResponseV1 response,
            Map<String, Integer> guidsToDeviceIds, int currentTry) {
        if (!CollectionUtils.isEmpty(response.getFailures())) {
            response.getFailures().forEach((deviceGuid, error) -> {
                int deviceError = parseErrorCode(error, summary.getLogSummary(jobGuid, false));
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
}
