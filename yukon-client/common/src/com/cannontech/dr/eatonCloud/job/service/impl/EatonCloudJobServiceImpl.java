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

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigInteger;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobPollService;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobResponseProcessor;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobService;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobSmartNotifService;
import com.cannontech.dr.eatonCloud.model.EatonCloudError;
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

public class EatonCloudJobServiceImpl extends EatonCloudJobHelperService implements EatonCloudJobService {
    @Autowired private EatonCloudJobResponseProcessor eatonCloudJobResponseProcessor;
    @Autowired private DeviceDao deviceDao;
    @Autowired private EatonCloudCommunicationServiceV1 eatonCloudCommunicationService;
    @Autowired private RecentEventParticipationDao recentEventParticipationDao;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private EatonCloudJobPollService eatonCloudJobPollService;
    @Autowired private EatonCloudJobSmartNotifService eatonCloudJobSmartNotifService;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;

    private static final Logger log = YukonLogManager.getLogger(EatonCloudJobServiceImpl.class);

    private AtomicBoolean isSendingCommands = new AtomicBoolean(false);

    // eventId
    private Map<Integer, RetrySummary> resendTries = new ConcurrentHashMap<>();

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
            if (isSendingCommands.compareAndSet(false, true)) {
                try {
                    resendControl();
                } catch (Exception e) {
                    log.error("Error Resending Control", e);
                }
                isSendingCommands.set(false);
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    private void resendControl() {
        try {
            Iterator<Entry<Integer, RetrySummary>> iter = resendTries.entrySet().iterator();
            // For each external event id in cache
            while (iter.hasNext()) {
                Entry<Integer, RetrySummary> entry = iter.next();
                Integer eventId = entry.getKey();
                EventSummary summary = entry.getValue().summary;
                Instant jobCreationTime = entry.getValue().result.getKey();
                List<String> jobGuids = entry.getValue().result.getValue();
                Instant resendTime = summary.getCurrentTryTime();
                if (resendTime.isEqualNow() || resendTime.isBeforeNow()) {
                    eatonCloudJobPollService.immediatePoll(summary, jobGuids, jobCreationTime, summary.getCurrentTry().intValue());
                    Set<Integer> devices = recentEventParticipationDao.getDeviceIdsByExternalEventIdAndStatuses(eventId,
                            List.of(FAILED_WILL_RETRY, UNKNOWN));
                    if (devices.isEmpty()) {
                        iter.remove();
                        log.info(summary.getLogSummary(true)
                                + "jobs:{} Done (No devices found with statuses of FAILED_WILL_RETRY, UNKNOWN).", jobGuids);
                        continue;
                    }
                    Pair<Instant, List<String>> result = createJobs(devices, summary);
                    if((setupRetry(summary, result) == null)){
                        iter.remove();
                        eatonCloudJobPollService.failWillRetryDevicesAfterLastPoll(summary);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    @Override
    public void createJobs(int programId, Set<Integer> devices, LMEatonCloudScheduledCycleCommand command,
            int eventId) {
        EventSummary summary = new EventSummary(eventId, programId, command, log, recentEventParticipationDao,
                deviceErrorTranslatorDao);
        Pair<Instant, List<String>> result = createJobs(devices, summary);
        setupRetry(summary, result);
    }

    // caches next try info
    private EventSummary setupRetry(EventSummary summary, Pair<Instant, List<String>> result) {
        if (result != null) {
            EventSummary nextTry = summary.setupNextTry(
                    EatonCloudJobSettingsHelper.pollInMinutes + EatonCloudJobSettingsHelper.firstRetryAfterPollMinutes);
            if (nextTry != null) {
                resendTries.put(nextTry.getEventId(), new RetrySummary(nextTry, result));
                return nextTry;
            }
        }
        eatonCloudJobPollService.failWillRetryDevicesAfterLastPoll(summary);
        return null;
    }

    private static class RetrySummary {
        private EventSummary summary;
        //job creation time, job guids
        private Pair<Instant, List<String>> result;
        public RetrySummary(EventSummary summary, Pair<Instant, List<String>> result) {
            this.summary = summary;
            this.result = result;
        }
    }

    /**
     * Starts jobs, schedules poll for results in 5 minutes
     */
    private Pair<Instant, List<String>> createJobs(Set<Integer> devices, EventSummary summary) {
        Map<Integer, String> guids = deviceDao.getGuids(devices);
        Map<String, Object> params = ShedParamHeper.getShedParams(summary.getCommand(), summary.getEventId());
        List<EatonCloudJobRequestV1> requests = getRequests(guids, devices, params);
        Instant jobCreationTime =  Instant.now();

        List<String> jobGuids = new ArrayList<>();
        requests.forEach(request -> {
            String jobGuid = startJob(guids, summary, devices, request);
            if (jobGuid != null) {
                jobGuids.add(jobGuid);
            }
        });
        if (!jobGuids.isEmpty()) {
            // schedule poll for device status in 5 minutes
            eatonCloudJobPollService.schedulePoll(summary, EatonCloudJobSettingsHelper.pollInMinutes, devices.size(), jobGuids,
                    jobCreationTime,
                    summary.getCurrentTry().get());
            return Pair.of(jobCreationTime, jobGuids);
        }
        eatonCloudJobSmartNotifService.sendSmartNotifications(summary.getProgramId(), summary.getCommand().getGroupId(),
                devices.size(), devices.size(), true, summary.getLogSummary(false));
        return null;
       
    }

    private String startJob(Map<Integer, String> guids, EventSummary summary, Set<Integer> devices,
            EatonCloudJobRequestV1 request) {
        try {
            log.info(summary.getLogSummary(true) + "SEND Creating job to send Shed Command:{} devices:{}",
                    summary.getCommand(),
                    devices.size());
            EatonCloudJobResponseV1 response = eatonCloudCommunicationService.createJob(request);
            log.info(summary.getLogSummary(response.getJobGuid(), true) + "SEND Created job to send Shed Command:{} devices:{}",
                    summary.getCommand(),
                    devices.size());
            return response.getJobGuid();
        } catch (EatonCloudCommunicationExceptionV1 e) {
            log.error(summary.getLogSummary(true) + "SEND Failed to create job to send Shed Command:{} devices:{}",
                    summary.getCommand(),
                    devices.size(), e);
            // job failed, mark all devices as failed, failed job will not retry
            devices.forEach(deviceId -> eatonCloudJobResponseProcessor.processError(summary,
                    deviceId, guids.get(deviceId), null,
                    EatonCloudError.JOB_CREATION_FAILED.getCode(), ControlEventDeviceStatus.FAILED, 1));
            return null;
        }
    }

    @Override
    public void terminateEvent(int eventId) {
        Iterator<Entry<Integer, RetrySummary>> iter = resendTries.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Integer, RetrySummary> entry = iter.next();
            if(entry.getKey() == eventId) {
                iter.remove();
                EventSummary summary = entry.getValue().summary;
                log.info(summary.getLogSummary(false) + "terminating event due to RESTORE");
                eatonCloudJobPollService.failWillRetryDevicesAfterLastPoll(summary);
            }
        }
    }
}
