package com.cannontech.dr.eatonCloud.job.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobReadService;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobResponseProcessor;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobRestorePollService;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobSmartNotifService;
import com.cannontech.dr.eatonCloud.model.EatonCloudError;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudJobStatusResponseV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;

public class EatonCloudJobRestorePollServiceImpl extends EatonCloudJobPollServiceHelper implements EatonCloudJobRestorePollService {

    private static final Logger log = YukonLogManager.getLogger(EatonCloudJobRestorePollServiceImpl.class);

    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired EatonCloudJobReadService eatonCloudJobReadService;
    @Autowired private EatonCloudCommunicationServiceV1 eatonCloudCommunicationService;
    @Autowired private EatonCloudJobResponseProcessor eatonCloudJobResponseProcessor;
    @Autowired private EatonCloudJobSmartNotifService eatonCloudJobSmartNotifService;
    @Autowired private DeviceDao deviceDao;
    
    @Override
    public void schedulePoll(EventRestoreSummary summary, int pollInMinutes, List<String> jobGuids, List<String> deviceGuids) {
        if (CollectionUtils.isEmpty(jobGuids)) {
            return;
        }
        log.info(summary.getLogSummary() + "POLL scheduling in {} minutes job guids{}:", pollInMinutes,
                jobGuids);

        executor.schedule(() -> {
            poll(summary, jobGuids, deviceGuids);
        }, pollInMinutes, TimeUnit.MINUTES);
    }
    
    private void poll(EventRestoreSummary summary, List<String> jobGuids, List<String> deviceGuids) {
        Map<String, Integer> guidsToDeviceIds = deviceDao.getDeviceIds(deviceGuids);
        List<String> successes = new ArrayList<>();
        List<String> failures = new ArrayList<>();
        try {
            jobGuids.forEach(jobGuid -> {
                try {
                    log.info(summary.getLogSummary(jobGuid) + "POLL");
                    EatonCloudJobStatusResponseV1 response = eatonCloudCommunicationService.getJobStatus(jobGuid);
                    log.info(summary.getLogSummary(jobGuid) + "POLL successes:{} failures:{}",
                            response.getSuccesses() == null ? 0 : response.getSuccesses().size(),
                            response.getFailures() == null ? 0 : response.getFailures().size());
                    successes.addAll(processSuccesses(summary, jobGuid, response, guidsToDeviceIds));
                    failures.addAll(processFailure(summary, jobGuid, response, guidsToDeviceIds));
                } catch (EatonCloudCommunicationExceptionV1 e) {
                    log.error(summary.getLogSummary(jobGuid) + "POLL error polling devices job", e);
                }
            });
            
            eatonCloudJobSmartNotifService.sendSmartNotifications(summary.getProgramId(),
                    summary.getCommand().getGroupId(), deviceGuids.size(), deviceGuids.size() - successes.size(),
                    false, summary.getLogSummary());
            
            deviceGuids.removeIf(guid -> successes.contains(guid) || failures.contains(guid));
            if(!deviceGuids.isEmpty()) {
                log.info(summary.getLogSummary() + "POLL no response recieved for {} devices. {}", deviceGuids.size(),
                        deviceGuids);
                deviceGuids
                        .forEach(guid -> eatonCloudJobResponseProcessor.processError(summary,
                                guidsToDeviceIds.get(guid), guid, null,
                                EatonCloudError.NO_RESPONSE_FROM_DEVICE.getCode())); 
            }
        } catch (Exception e) {
            log.error("Error polling", e);
        }
    }

    private List<String> processFailure(EventRestoreSummary summary, String jobGuid, EatonCloudJobStatusResponseV1 response,
            Map<String, Integer> guidsToDeviceIds) {
        List<String> failures = new ArrayList<>();
        if (!CollectionUtils.isEmpty(response.getFailures())) {
            response.getFailures().forEach((deviceGuid, error) -> {
                int deviceError = parseErrorCode(error, summary.getLogSummary(jobGuid));
                failures.add(deviceGuid);
                eatonCloudJobResponseProcessor.processError(summary,
                        guidsToDeviceIds.get(deviceGuid), deviceGuid, jobGuid, deviceError);
            });
        }
        return failures ;
    }

    private List<String> processSuccesses(EventRestoreSummary summary, String jobGuid,
            EatonCloudJobStatusResponseV1 response, Map<String, Integer> guidsToDeviceIds) {
        if (!CollectionUtils.isEmpty(response.getSuccesses())) {
            response.getSuccesses()
                    .forEach(success -> eatonCloudJobResponseProcessor.processSuccess(summary,
                            guidsToDeviceIds.get(success), success, jobGuid));
            return response.getSuccesses();
        }
        return new ArrayList<>();
    }
}
