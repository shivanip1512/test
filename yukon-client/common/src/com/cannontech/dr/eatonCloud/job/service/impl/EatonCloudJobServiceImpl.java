package com.cannontech.dr.eatonCloud.job.service.impl;

import static com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus.FAILED_WILL_RETRY;
import static com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus.UNKNOWN;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigInteger;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobPollService;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobResponseProcessor;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobService;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudJobRequestV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudJobResponseV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;
import com.cannontech.dr.recenteventparticipation.dao.RecentEventParticipationDao;
import com.cannontech.loadcontrol.messages.LMEatonCloudScheduledCycleCommand;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class EatonCloudJobServiceImpl implements EatonCloudJobService {
    @Autowired private EatonCloudJobResponseProcessor eatonCloudJobResponseProcessor;
    @Autowired private DeviceDao deviceDao;
    @Autowired private EatonCloudCommunicationServiceV1 eatonCloudCommunicationService;
    @Autowired private RecentEventParticipationDao recentEventParticipationDao;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private EatonCloudJobPollService eatonCloudJobPollService;

    private static final Logger log = YukonLogManager.getLogger(EatonCloudJobServiceImpl.class);

    private AtomicBoolean isSendingCommands = new AtomicBoolean(false);
    private int maxDevicesPerJob;
    //change to 5
    private static int pollInMinutes = 1;
    //change to 2
    private static int firstRetryAfterPollMinutes = 1;

    // eventId
    private Map<Integer, EventSummary> resendTries = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        String siteGuid = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
        if (Strings.isNullOrEmpty(siteGuid)) {
            return;
        }

        maxDevicesPerJob = configurationSource.getInteger(
                MasterConfigInteger.EATON_CLOUD_DEVICES_PER_JOB, 2500);

        schedule();
        eatonCloudJobResponseProcessor.failDevicesOnStartup();
    }

    private void schedule() {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (isSendingCommands.get()) {
                return;
            }
            isSendingCommands.set(true);
            resendControl();
            isSendingCommands.set(false);
        }, 1, 1, TimeUnit.MINUTES);
    }

    private void resendControl() {
        try {
            Iterator<Entry<Integer, EventSummary>> iter = resendTries.entrySet().iterator();
            // For each external event id in cache
            while (iter.hasNext()) {
                Entry<Integer, EventSummary> entry = iter.next();
                Integer eventId = entry.getKey();
                EventSummary summary = entry.getValue();
                Instant resendTime = summary.getCurrentTryTime();
                if (resendTime.isEqualNow() || resendTime.isBeforeNow()) {
                    Set<Integer> devices = recentEventParticipationDao.getDeviceIdsByExternalEventIdAndStatuses(eventId,
                            List.of(FAILED_WILL_RETRY, UNKNOWN));
                    if (devices.isEmpty()) {
                        iter.remove();
                        log.info(summary.getLogSummary(true)
                                + "Done (No devices found with statuses of FAILED_WILL_RETRY, UNKNOWN).");
                        continue;
                    }
                    eatonCloudJobPollService.immediatePoll(summary);
                    createJobs(devices, summary);
                    EventSummary nextTry = summary.setupNextTry(summary.getPeriod());
                    if (nextTry == null) {
                        iter.remove();
                    } else {
                        resendTries.put(nextTry.getEventId(), nextTry);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    @Override
    public void createJobs(int programId, Set<Integer> devices, LMEatonCloudScheduledCycleCommand command,
            Integer eventId) {
        EventSummary summary = new EventSummary(eventId, programId, command, log, recentEventParticipationDao);
        createJobs(devices, summary);
        if (!summary.getJobGuids().isEmpty()) {
            EventSummary nextTry = summary.setupNextTry(pollInMinutes + firstRetryAfterPollMinutes);
            resendTries.put(nextTry.getEventId(), nextTry);
        }
    }

    /**
     * Creates new job requests to send to Eaton Cloud
     */
    private List<EatonCloudJobRequestV1> getRequests(Map<Integer, String> guids, Set<Integer> devices, EventSummary summary) {
        Map<String, Object> params = ShedParamHeper.getShedParams(summary.getCommand(), summary.getEventId());
        List<List<Integer>> devicesPerJob = Lists.partition(Lists.newArrayList(devices), maxDevicesPerJob);
        return devicesPerJob.stream().map(paoIds -> {
            List<String> deviceGuids = paoIds.stream()
                    .map(paoId -> guids.get(paoId))
                    .collect(Collectors.toList());
            return new EatonCloudJobRequestV1(deviceGuids, "LCR_Control", params);
        }).collect(Collectors.toList());
    }

    /**
     * Starts jobs, schedules poll for results in 5 minutes
     */
    private void createJobs(Set<Integer> devices, EventSummary summary) {
        Map<Integer, String> guids = deviceDao.getGuids(devices);
        List<EatonCloudJobRequestV1> requests = getRequests(guids, devices, summary);

        List<String> jobGuids = new ArrayList<>();
        requests.forEach(request -> {
            String jobGuid = startJob(guids, summary, devices, request);
            if (jobGuid != null) {
                jobGuids.add(jobGuid);
            }
        });
        summary.setJobGuids(jobGuids);
        if (!jobGuids.isEmpty()) {
            // schedule poll for device status in 5 minutes
            eatonCloudJobPollService.schedulePoll(summary, pollInMinutes, devices.size());
        }
    }

    private String startJob(Map<Integer, String> guids, EventSummary summary, Set<Integer> devices,
            EatonCloudJobRequestV1 request) {
        try {
            log.info(summary.getLogSummary(true) + "Creating job to send Shed Command:{} devices:{}",
                    summary.getCommand(),
                    devices.size());
            EatonCloudJobResponseV1 response = eatonCloudCommunicationService.createJob(request);
            log.info(summary.getLogSummary(response.getJobGuid(), true) + "Created job to send Shed Command:{} devices:{}",
                    summary.getCommand(),
                    devices.size());
            return response.getJobGuid();
        } catch (EatonCloudCommunicationExceptionV1 e) {
            log.error(summary.getLogSummary(true) + "Failed to create job to send Shed Command:{} devices:{}",
                    summary.getCommand(),
                    devices.size(), e);
            // job failed, mark all devices as failed, failed job will not retry
            devices.forEach(deviceId -> eatonCloudJobResponseProcessor.processError(summary,
                    deviceId, guids.get(deviceId), "Job Creation Failed",
                    e.getDisplayMessage(), ControlEventDeviceStatus.FAILED));
            return null;
        }
    }
}
