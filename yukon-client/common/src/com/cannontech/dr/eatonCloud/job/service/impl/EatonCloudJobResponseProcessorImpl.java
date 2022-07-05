package com.cannontech.dr.eatonCloud.job.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.EatonCloudEventLogService;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobResponseProcessor;
import com.cannontech.dr.eatonCloud.model.EatonCloudError;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;
import com.cannontech.dr.recenteventparticipation.dao.RecentEventParticipationDao;
import com.cannontech.loadcontrol.messages.LMEatonCloudScheduledCycleCommand;
import com.cannontech.loadcontrol.messages.LMEatonCloudStopCommand;
import com.cannontech.yukon.IDatabaseCache;

public class EatonCloudJobResponseProcessorImpl implements EatonCloudJobResponseProcessor {

    @Autowired private EatonCloudEventLogService eatonCloudEventLogService;
    @Autowired private RecentEventParticipationDao recentEventParticipationDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
   
    private static final Logger log = YukonLogManager.getLogger(EatonCloudJobResponseProcessorImpl.class);
    
    @Override
    public void failDevicesOnStartup() {
        try {
            DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(EatonCloudError.NO_RESPONSE_DUE_TO_RESTART.getDeviceError());
            int affectedRows = recentEventParticipationDao.failWillRetryDevices(null, errorDescription.getDescription());
            log.info(
                    "On the start-up changed {} devices waiting for retry (FAILED_WILL_RETRY, UNKNOWN) to failed (FAILED):{}.",
                    affectedRows, errorDescription.getDescription());
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    public void processError(EventSummary summary, Integer deviceId, String guid, String jobGuid, int code,
            ControlEventDeviceStatus status, int currentTry) {
        String deviceName = dbCache.getAllPaosMap().get(deviceId).getPaoName();
        LMEatonCloudScheduledCycleCommand command = summary.getCommand();
        DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(EatonCloudError.getErrorByCode(code));
        String message = errorDescription.getDescription();        
        recentEventParticipationDao.updateDeviceControlEvent(String.valueOf(summary.getEventId()),
                deviceId,
                status,
                Instant.now(),
                StringUtils.isEmpty(message) ? null : message.length() > 100 ? message.substring(0, 100) : message,
                currentTry == 1 ? null : Instant.now());
        
        String jobInfo = getJobInfo(summary.getEventId(), jobGuid);
        eatonCloudEventLogService.sendShedJobFailed(deviceName,
                guid,
                jobInfo,
                String.valueOf(currentTry), 
                command.getDutyCyclePercentage(),
                command.getDutyCyclePeriod(),
                command.getCriticality(),
                command.getVirtualRelayId(),
                truncateErrorForEventLog("("+code+")" + message));
 
        log.debug(summary.getLogSummary(false) + "Try:{} Failed sending shed to device id:{} guid:{} name:{} error:{}",
                currentTry, deviceId, guid, deviceName, message);
    }
    
    @Override
    public void processSuccess(EventSummary summary, Integer deviceId, String guid, String jobGuid, int currentTry) {
        String deviceName = dbCache.getAllPaosMap().get(deviceId).getPaoName();
        LMEatonCloudScheduledCycleCommand command = summary.getCommand();
        recentEventParticipationDao.updateDeviceControlEvent(String.valueOf(summary.getEventId()), deviceId,
                ControlEventDeviceStatus.SUCCESS_RECEIVED, new Instant(),
                null, currentTry == 1 ? null : Instant.now());

        log.debug(summary.getLogSummary(jobGuid, false) + "Try:{} Success sending shed to device id:{} guid:{} name:{}",
                currentTry, deviceId, guid, deviceName);

        eatonCloudEventLogService.sendShedJob(deviceName,
                guid,
                summary.getEventId()+"/"+jobGuid,
                String.valueOf(currentTry),
                command.getDutyCyclePercentage(),
                command.getDutyCyclePeriod(),
                command.getCriticality(),
                command.getVirtualRelayId());
    }
    
    @Override
    public void processError(EventRestoreSummary summary, Integer deviceId, String guid, String jobGuid, int code) {
        String deviceName = dbCache.getAllPaosMap().get(deviceId).getPaoName();
        LMEatonCloudStopCommand command = summary.getCommand();
        DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(EatonCloudError.getErrorByCode(code));
        String message = errorDescription.getDescription();        
        
        String jobInfo = getJobInfo(summary.getEventId(), jobGuid);
        eatonCloudEventLogService.sendRestoreJobFailed(deviceName,
                guid,
                jobInfo,
                truncateErrorForEventLog("("+code+")" + message),
                command.getVirtualRelayId());
 
        log.debug(summary.getLogSummary(jobGuid) + "Failed sending restore to device id:{} guid:{} name:{} error:{}",
                 deviceId, guid, deviceName, message);
    }

    private String getJobInfo(int eventId, String jobGuid) {
        String jobInfo = String.valueOf(eventId);
        if (jobGuid != null) {
            jobInfo = jobInfo + "/" + jobGuid;
        }
        return jobInfo;
    }
    
    @Override
    public void processSuccess(EventRestoreSummary  summary, Integer deviceId, String guid, String jobGuid) {
        String deviceName = dbCache.getAllPaosMap().get(deviceId).getPaoName();
        LMEatonCloudStopCommand command = summary.getCommand();

        log.debug(summary.getLogSummary(jobGuid) + "Try:{} Success sending restore to device id:{} guid:{} name:{}",
                  deviceId, guid, deviceName);

        eatonCloudEventLogService.sendRestoreJob(deviceName,
                guid,
                summary.getEventId()+"/"+jobGuid,
                command.getVirtualRelayId());
    }

    private String truncateErrorForEventLog(String message) {
        return StringUtils
                .isEmpty(message) ? "See log for details" : message.length() > 2000 ? message.substring(0, 2000) : message;
    }
}
