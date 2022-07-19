package com.cannontech.dr.eatonCloud.job.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobControlType;
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
    public void schedulePoll(EventRestoreSummary summary, Minutes minutes, Map<String, List<String>> jobGuids, List<String> allDevicesGuids)
    {
        if (CollectionUtils.isEmpty(jobGuids)) {
            return;
        }
        log.info("{} POLL scheduling in {} minutes job guids{}:", summary.getLogSummary(),
                EatonCloudJobSettingsHelper.pollInMinutes.getMinutes(),
                jobGuids.keySet());

        executor.schedule(() -> {
            poll(summary, jobGuids, allDevicesGuids);
        }, EatonCloudJobSettingsHelper.pollInMinutes.getMinutes(), TimeUnit.MINUTES);
    }
    
    private void poll(EventRestoreSummary summary, Map<String, List<String>> jobGuids, List<String> deviceGuids) {
        Map<String, Integer> guidsToDeviceIds = deviceDao.getDeviceIds(deviceGuids);
        List<String> successes = new ArrayList<>();

        
        //jobs failed to create
        List<String> unknowns = new ArrayList<>(deviceGuids);
        try {
            jobGuids.forEach((jobGuid, devices) -> {
                try {
                    EatonCloudJobStatusResponseV1 response = eatonCloudCommunicationService.getJobStatus(jobGuid);
                    log.info("{} POLL successes:{} failures:{}",
                            summary.getLogSummary(jobGuid), 
                            response.getSuccesses() == null ? 0 : response.getSuccesses().size(),
                            response.getFailures() == null ? 0 : response.getFailures().size());
                    List<String> jobSuccesses = processSuccesses(summary, jobGuid, response, guidsToDeviceIds);
                    List<String> jobFailures = processFailure(summary, jobGuid, response, guidsToDeviceIds);

                    successes.addAll(jobSuccesses);
  
                    unknowns.removeAll(devices);
                    
                    List<String> devicesPerJob = new ArrayList<>(devices);
                    devicesPerJob.removeAll(jobSuccesses);
                    devicesPerJob.removeAll(jobFailures);
                    
                    if(!devicesPerJob.isEmpty()) {
                        log.info("{} POLL no response recieved for {} devices. {}", summary.getLogSummary(), devicesPerJob.size(),
                                devicesPerJob);
                        processError(summary, guidsToDeviceIds, jobGuid, devicesPerJob, EatonCloudError.NO_RESPONSE_FROM_DEVICE);
                    }
                } catch (EatonCloudCommunicationExceptionV1 e) {
                    log.error(summary.getLogSummary(jobGuid) + "POLL error polling devices job", e);
                }
            });
            
            eatonCloudJobSmartNotifService.sendSmartNotifications(summary.getProgramId(),
                    summary.getCommand().getGroupId(), deviceGuids.size(), deviceGuids.size() - successes.size(),
                    EatonCloudJobControlType.RESTORE, summary.getLogSummary());
            
            if (!unknowns.isEmpty()) {
                log.info("{} POLL no response recieved for {} devices. {}", unknowns.size(), summary.getLogSummary(),
                        unknowns);
                processError(summary, guidsToDeviceIds, null, unknowns, EatonCloudError.JOB_CREATION_FAILED);
            }
        } catch (Exception e) {
            log.error("Error polling", e);
        }
    }

    private void processError(EventRestoreSummary summary, Map<String, Integer> guidsToDeviceIds, String jobGuid,
            List<String> devicesPerJob, EatonCloudError error) {
        devicesPerJob.forEach(deviceGuid -> {
            var deviceId = guidsToDeviceIds.get(deviceGuid);
            var noResponseCode = EatonCloudError.NO_RESPONSE_FROM_DEVICE.getCode();
            eatonCloudJobResponseProcessor.processError(summary, deviceId, deviceGuid, jobGuid, noResponseCode);
        });
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
