package com.cannontech.dr.eatonCloud.job.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigInteger;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobResponseProcessor;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobRestorePollService;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobRestoreService;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobSmartNotifService;
import com.cannontech.dr.eatonCloud.model.EatonCloudError;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudJobRequestV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudJobResponseV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;
import com.cannontech.loadcontrol.messages.LMEatonCloudStopCommand;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.base.Strings;

public class EatonCloudJobRestoreServiceImpl extends EatonCloudJobHelperService implements EatonCloudJobRestoreService {
    @Autowired private EatonCloudJobResponseProcessor eatonCloudJobResponseProcessor;
    @Autowired private DeviceDao deviceDao;
    @Autowired private EatonCloudCommunicationServiceV1 eatonCloudCommunicationService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private EatonCloudJobRestorePollService eatonCloudJobRestorePollService;
    @Autowired private EatonCloudJobSmartNotifService eatonCloudJobSmartNotifService;

    private static final Logger log = YukonLogManager.getLogger(EatonCloudJobRestoreServiceImpl.class);

    @PostConstruct
    public void init() {
        String siteGuid = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
        if (Strings.isNullOrEmpty(siteGuid)) {
            return;
        }

        maxDevicesPerJob = configurationSource.getInteger(
                MasterConfigInteger.EATON_CLOUD_DEVICES_PER_JOB, 2500);
    }
    
    @Override
    public void createJobs(int programId, Set<Integer> devices, LMEatonCloudStopCommand command,
            Integer eventId) {
        EventRestoreSummary summary = new EventRestoreSummary(eventId, programId, command, log);
        createJobs(devices, summary);
    }

    /**
     * Starts jobs, schedules poll for results in 5 minutes
     */
    private void createJobs(Set<Integer> devices, EventRestoreSummary summary) {
        Map<Integer, String> guids = deviceDao.getGuids(devices);
        Map<String, Object> params = ShedParamHeper.getRestoreParams(summary.getCommand(), summary.getEventId());
        List<EatonCloudJobRequestV1> requests = getRequests(guids, devices, params);
        List<String> jobGuids = new ArrayList<>();
        List<String> devicesGuids = new ArrayList<>();
        requests.forEach(request -> {
            devicesGuids.addAll(request.getDeviceGuids());
            String jobGuid = startJob(guids, summary, devices, request);
            if (jobGuid != null) {
                jobGuids.add(jobGuid);
            }
        });
        if (!jobGuids.isEmpty()) {
            eatonCloudJobRestorePollService.schedulePoll(summary, pollInMinutes, jobGuids, devicesGuids);
        } else {
            eatonCloudJobSmartNotifService.sendSmartNotifications(summary.getProgramId(), summary.getCommand().getGroupId(),
                    devices.size(), devices.size(), summary.getLogSummary());
        }
    }
    
    private String startJob(Map<Integer, String> guids, EventRestoreSummary summary, Set<Integer> devices,
            EatonCloudJobRequestV1 request) {
        try {
            log.info(summary.getLogSummary() + "SEND Creating job to send Shed Command:{} devices:{}",
                    summary.getCommand(),
                    devices.size());
            EatonCloudJobResponseV1 response = eatonCloudCommunicationService.createJob(request);
            log.info(summary.getLogSummary() + "SEND Created job to send Shed Command:{} devices:{}",
                    summary.getCommand(),
                    devices.size());
            return response.getJobGuid();
        } catch (EatonCloudCommunicationExceptionV1 e) {
            log.error(summary.getLogSummary() + "SEND Failed to create job to send Shed Command:{} devices:{}",
                    summary.getCommand(),
                    devices.size(), e);
            // job failed, mark all devices as failed, failed job will not retry
            devices.forEach(deviceId -> eatonCloudJobResponseProcessor.processError(summary,
                    deviceId, guids.get(deviceId), null,
                    EatonCloudError.JOB_CREATION_FAILED.getCode()));
            return null;
        }
    }
}
