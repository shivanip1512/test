package com.cannontech.dr.eatonCloud.service.impl;

import static com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus.*;

import java.math.RoundingMode;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.util.StopWatch;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigInteger;
import com.cannontech.common.events.loggers.EatonCloudEventLogService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.smartNotification.model.EatonCloudDrEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobControlType;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandRequestV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandResponseV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.service.EatonCloudSendControlService;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudDataReadService;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;
import com.cannontech.dr.recenteventparticipation.dao.RecentEventParticipationDao;
import com.cannontech.loadcontrol.messages.LMEatonCloudScheduledCycleCommand;
import com.cannontech.loadcontrol.messages.LMEatonCloudStopCommand;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.google.common.math.IntMath;

public class EatonCloudSendControlServiceImpl implements EatonCloudSendControlService {

    private static final Logger log = YukonLogManager.getLogger(EatonCloudSendControlServiceImpl.class);

    @Autowired private IDatabaseCache dbCache;
    @Autowired private DeviceDao deviceDao;
    @Autowired private EatonCloudEventLogService eatonCloudEventLogService;
    @Autowired private EatonCloudCommunicationServiceV1 eatonCloudCommunicationService;
    @Autowired private RecentEventParticipationDao recentEventParticipationDao;
    @Autowired private SmartNotificationEventCreationService smartNotificationEventCreationService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private EatonCloudDataReadService eatonCloudDataReadService;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired private GlobalSettingDao settingDao;

    // eventId
    private Map<Integer, ResendOptions> resendTries = new ConcurrentHashMap<>();

    // <external event id, Pair<next read time, sent time>>
    private Map<Integer, Pair<Instant, Instant>> nextRead = new ConcurrentHashMap<>();

    private int failureNotificationPercent;

    private AtomicBoolean isSendingCommands = new AtomicBoolean(false);
    private AtomicBoolean isReadingDevices = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        String siteGuid = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
        if (Strings.isNullOrEmpty(siteGuid)) {
            return;
        }

        failureNotificationPercent = configurationSource.getInteger(
                MasterConfigInteger.EATON_CLOUD_NOTIFICATION_COMMAND_FAILURE_PERCENT, 25);
        try {
           /* int affectedRows = recentEventParticipationDao.failWillRetryDevices(null);
            log.info(
                    "On the start-up changed {} devices waiting for retry (FAILED_WILL_RETRY, UNKNOWN) to failed (FAILED).",
                    affectedRows);*/
        } catch (Exception e) {
            log.error(e);
        }
        schedule();
    }

    private void schedule() {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (isReadingDevices.get()) {
                return;
            }
            isReadingDevices.set(true);
            readDevices();
            isReadingDevices.set(false);
        }, 0, 1, TimeUnit.MINUTES);

        scheduledExecutor.scheduleAtFixedRate(() -> {
            if (isSendingCommands.get()) {
                return;
            }
            isSendingCommands.set(true);
            resendControl();
            isSendingCommands.set(false);
        }, 1, 1, TimeUnit.MINUTES);
    }

    private void readDevices() {
        try {
            Iterator<Entry<Integer, Pair<Instant, Instant>>> iter = nextRead.entrySet().iterator();
            // For each key in cache
            while (iter.hasNext()) {
                Entry<Integer, Pair<Instant, Instant>> entry = iter.next();
                Integer eventId = entry.getKey();
                Instant nextRead = entry.getValue().getKey();
                Instant sendTime = entry.getValue().getValue();
                if (nextRead.isEqualNow() || nextRead.isBeforeNow()) {
                    Range<Instant> range = new Range<>(sendTime, true, Instant.now(), true);
                    Set<Integer> devicesToRead = recentEventParticipationDao.getDeviceIdsByExternalEventIdAndStatuses(eventId,
                            List.of(ControlEventDeviceStatus.SUCCESS_RECEIVED));
                    if (!devicesToRead.isEmpty()) {
                        Multimap<PaoIdentifier, PointData> result = eatonCloudDataReadService.collectDataForRead(devicesToRead,
                                range, "READ AFTER SHED");
                        log.info(
                                "[external event id: {}] Reading (READ AFTER SHED) devices: {} Read succeeded for {} devices for date range:{}-{} [original command sent at {}] ",
                                eventId,
                                devicesToRead.size(),
                                result.asMap().keySet().size(),
                                range.getMin().toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"),
                                range.getMax().toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"),
                                sendTime.toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"));
                    } else {
                        log.info(
                                "[external event id: {}] Found no devices that need verification read (READ AFTER SHED). Devices with status SUCCESS_RECEIVED not found for date range:{}-{} [original command sent at {}] ",
                                eventId,
                                range.getMin().toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"),
                                range.getMax().toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"),
                                sendTime.toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"));
                    }
                    iter.remove();
                }
            }
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    private void resendControl() {
        try {
            Iterator<Entry<Integer, ResendOptions>> iter = resendTries.entrySet().iterator();
            // For each external event id in cache
            while (iter.hasNext()) {
                Entry<Integer, ResendOptions> entry = iter.next();
                Integer eventId = entry.getKey();
                ResendOptions options = entry.getValue();
                Instant resendTime = options.currentTryTime;
                if (resendTime.isEqualNow() || resendTime.isBeforeNow()) {
                    Set<Integer> devices = recentEventParticipationDao.getDeviceIdsByExternalEventIdAndStatuses(eventId,
                            List.of(FAILED_WILL_RETRY, UNKNOWN));
                    if (devices.isEmpty()) {
                        iter.remove();
                        log.info(
                                "[external event id: {} ({} of {})] Shed command will not be sent again. No devices found with statuses of FAILED_WILL_RETRY, UNKNOWN.",
                                eventId, options.currentTry, options.numberOfTimesToRetry);
                        continue;
                    }
                    sendShedCommands(devices, eventId, options);
                    if (!options.setupNextTry(options.period)) {
                        iter.remove();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    private int sendShedCommands(Set<Integer> devices, Integer eventId, ResendOptions resendOptions) {

        LMEatonCloudScheduledCycleCommand command = resendOptions.command;
        int tryNumber = resendOptions.currentTry.get();
        int totalTries = resendOptions.numberOfTimesToRetry;

        log.info("[external event id: {} ({} of {})] Sending LM Eaton Cloud Shed Command:{} devices:{}", eventId, tryNumber,
                totalTries, command, devices.size());

        Map<String, Object> params = getShedParams(command, eventId);
        Map<Integer, String> guids = deviceDao.getGuids(devices);
        AtomicInteger totalFailed = new AtomicInteger(0);
        AtomicInteger totalSucceeded = new AtomicInteger(0);
        Instant sendTime = new Instant();

        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        Stream<Entry<Integer, String>> stream = guids.entrySet().parallelStream();
        stream.forEach(entry -> {
            int deviceId = entry.getKey();
            String guid = entry.getValue();

            String deviceName = dbCache.getAllDevices().stream()
                    .filter(d -> d.getLiteID() == deviceId)
                    .findAny()
                    .map(LiteYukonPAObject::getPaoName)
                    .orElse(null);
            try {
                log.debug(
                        "[external event id: {} ({} of {})] Attempting to send shed command to device id:{} guid:{} name:{} relay:{}",
                        eventId, tryNumber, totalTries, deviceId, guid, deviceName, command.getVirtualRelayId());
                EatonCloudCommandResponseV1 response = eatonCloudCommunicationService.sendCommand(guid,
                        new EatonCloudCommandRequestV1("LCR_Control", params));
                if (response.getStatusCode() == HttpStatus.OK.value()) {
                    recentEventParticipationDao.updateDeviceControlEvent(eventId.toString(), deviceId,
                            ControlEventDeviceStatus.SUCCESS_RECEIVED, new Instant(),
                            null, tryNumber == 1 ? null : Instant.now());
                    totalSucceeded.incrementAndGet();
                    log.debug("[external event id: {} ({} of {})] Success sending shed command to device id:{} guid:{} name:{} relay:{}",
                            eventId, tryNumber, totalTries, deviceId, guid, deviceName, command.getVirtualRelayId(), params);

                    eatonCloudEventLogService.sendShed(deviceName,
                            guid,
                            eventId.toString(),
                            String.valueOf(tryNumber),
                            command.getDutyCyclePercentage(),
                            command.getDutyCyclePeriod(),
                            command.getCriticality(),
                            command.getVirtualRelayId());

                } else {
                    totalFailed.getAndIncrement();
                    log.error(
                            "[external event id: {} ({} of {})] Error: {} sending shed command device id:{} guid:{} name:{} relay:{}",
                            eventId, tryNumber, totalTries, response.getMessage(), deviceId,
                            guid, deviceName, command.getVirtualRelayId());
                    processError(eventId, deviceName, deviceId, guid, command, response.getMessage(), tryNumber);
                }
            } catch (EatonCloudCommunicationExceptionV1 e) {
                totalFailed.getAndIncrement();
                log.error(
                        "[external event id: {} ({} of {})] Error sending shed command device id:{} guid:{} name:{} relay:{}",
                        eventId, tryNumber, totalTries, deviceId,
                        guid, deviceName, command.getVirtualRelayId(), e);
                processError(eventId, deviceName, deviceId, guid, command, e.getDisplayMessage(), tryNumber);
            }
        });

        stopwatch.stop();
        if (log.isDebugEnabled()) {
            var duration = Duration.standardSeconds((long) stopwatch.getTotalTimeSeconds());
            log.debug("[external event id: {} ({} of {})] Commands timer - devices: {}, total time: {}", eventId, tryNumber, totalTries, guids.size(), duration);
        }

        int readTimeFromNowInMinutes = command.getDutyCyclePeriod() == null ? 5 : IntMath.divide(
                command.getDutyCyclePeriod() / 60,
                2, RoundingMode.CEILING);
        Instant nextReadTime = DateTime.now().plusMinutes(readTimeFromNowInMinutes).toInstant();
        nextRead.put(eventId, Pair.of(nextReadTime, sendTime));

        log.info(
                "[external event id: {} ({} of {})] Finished sending LM Eaton Cloud Shed Command:{} devices:{} relay:{} failed:{} succeeded:{} next device read in {} minutes at {}",
                eventId,
                tryNumber,
                totalTries,
                command,
                devices.size(),
                command.getVirtualRelayId(),
                totalFailed,
                totalSucceeded,
                readTimeFromNowInMinutes,
                nextReadTime.toDateTime().toString("MM-dd-yyyy HH:mm:ss"));

        return totalFailed.intValue();
    }

    private void sendSmartNotifications(int eventId, int groupId, int programId, int totalDevices, int totalFailed) {
        if (totalFailed == 0) {
            return;
        }
        boolean sendNotification = (totalFailed * 100) / totalDevices > failureNotificationPercent;
        if (sendNotification) {
            String program = dbCache.getAllLMPrograms().stream().filter(p -> p.getLiteID() == programId).findFirst().get()
                    .getPaoName();
            String group = dbCache.getAllLMGroups().stream().filter(p -> p.getLiteID() == groupId).findFirst().get()
                    .getPaoName();
            SmartNotificationEvent event = EatonCloudDrEventAssembler.assemble(group, program, totalDevices, totalFailed, EatonCloudJobControlType.SHED);

            log.info("[external event id:{}] Sending smart notification event: {}", eventId, event);
            smartNotificationEventCreationService.send(SmartNotificationEventType.EATON_CLOUD_DR, List.of(event));
        }
    }

    @Override
    public void sendInitialShedCommand(int programId, Set<Integer> devices, LMEatonCloudScheduledCycleCommand command,
            Integer eventId) {
        ResendOptions resendOptions = new ResendOptions(eventId, command);
        int totalFailed = sendShedCommands(devices, eventId, resendOptions);
        // only send smart notification on the first try and not on re-sends
        sendSmartNotifications(eventId, command.getGroupId(), programId, devices.size(), totalFailed);
        // try#2 2 minutes after the first command has been send
        resendOptions.setupNextTry(2);
    }

    private void stopCommandResend(Integer eventId) {
        Iterator<Integer> iter = resendTries.keySet().iterator();
        while (iter.hasNext()) {
            if (iter.next().intValue() == eventId.intValue()) {
                int affectedRows = recentEventParticipationDao.failWillRetryDevices(eventId);
                if (affectedRows > 0) {
                    log.info(
                            "[external event id:{}] Sent restore command. Updated {} devices that were waiting for retry from FAILED_WILL_RETRY, UNKNOWN to failed FAILED.",
                            eventId, affectedRows);
                }
                iter.remove();
            }
        }
    }

    @Override
    public void sendRestoreCommands(Set<Integer> devices, LMEatonCloudStopCommand command, Integer eventId) {
        Map<String, Object> params = getRestoreParams(command, eventId);
        Map<Integer, String> guids = deviceDao.getGuids(devices);

        log.info("[external event id:{}] Sending LM Eaton Cloud Restore Command:{} devices:{}", eventId, command, devices.size());

        // Notifying re-send service to stop re-sending commands. We do not care if restore succeeds of fails we are still going
        // to stop re-sending commands.
        stopCommandResend(eventId);

        AtomicInteger totalFailed = new AtomicInteger(0);
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        Stream<Entry<Integer, String>> stream = guids.entrySet().parallelStream();
        stream.forEach(entry -> {
            int deviceId = entry.getKey();
            String guid = entry.getValue();

            String deviceName = dbCache.getAllDevices().stream()
                    .filter(d -> d.getLiteID() == deviceId)
                    .findAny()
                    .map(d -> d.getPaoName())
                    .orElse(null);
            log.debug("[external event id:{}] Attampting to send restore command to device id:{} name:{} relay:{}", eventId,
                    deviceId,
                    deviceName, command.getVirtualRelayId());
            try {
                eatonCloudCommunicationService.sendCommand(guid, new EatonCloudCommandRequestV1("LCR_Control", params));
                eatonCloudEventLogService.sendRestore(deviceName, guid, eventId.toString(), command.getVirtualRelayId());
            } catch (EatonCloudCommunicationExceptionV1 e) {
                totalFailed.getAndIncrement();
                eatonCloudEventLogService.sendRestoreFailed(deviceName, guid, truncateErrorForEventLog(e.getDisplayMessage()),
                        eventId.toString(), command.getVirtualRelayId());
                log.error("[external event id:{}] Error sending restore command device id:{} guid:{} name:{} relay:{}", eventId, deviceId, guid,
                         deviceName, command.getVirtualRelayId(), e);
            }
        });
        stopwatch.stop();
        if (log.isDebugEnabled()) {
            var duration = Duration.standardSeconds((long) stopwatch.getTotalTimeSeconds());
            log.debug("[external event id:{}] Commands timer - devices: {}, total time: {}", eventId, guids.size(), duration);
        }
        log.info("[external event id:{}] Finished sending LM Eaton Cloud Restore Command:{} devices:{} relay:{} failed:{}", eventId, command,
                devices.size(), command.getVirtualRelayId(), totalFailed.intValue());
    }

    private final class ResendOptions {
        private AtomicInteger currentTry = new AtomicInteger(1);
        private Instant currentTryTime = new Instant();
        private int period;
        private int numberOfTimesToRetry;

        private LMEatonCloudScheduledCycleCommand command;
        private Integer eventId;

        ResendOptions(Integer eventId, LMEatonCloudScheduledCycleCommand command) {
            this.eventId = eventId;
            this.command = command;

            // If it is a 4 hour control with a 30 minute period, it would have a cycle count of 8.
            // The resend service should only attempt to send to failed devices for the first 4 cycles, or 2 hours.

            Map<String, Object> params = getShedParams(command, eventId);
            period = (Integer) params.get(CommandParam.CYCLE_PERIOD.getParamName());
            numberOfTimesToRetry = IntMath.divide((Integer) params.get(CommandParam.CYCLE_COUNT.getParamName()), 2, RoundingMode.CEILING);
            log.info("[external event id:{}] Creating retry options for the first send of the shed command, possible retries:{} command params:{} command:{}", eventId, numberOfTimesToRetry, params, command);
        }

        /*
         * try#1 first time the command was send
         * try#2 2 minutes after the first command has been send
         * try#3... "period" minutes after the previous command has been send
         */
        boolean setupNextTry(int minutes) {
            if (numberOfTimesToRetry > currentTry.get()) {
                currentTry.incrementAndGet();
                currentTryTime = DateTime.now().plusMinutes(minutes).toInstant();
                resendTries.put(eventId, this);
                log.info("[external event id:{}] Next retry time after {}", eventId, currentTryTime.toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"));
                return true;
            }
            // we ran out of retries and will mark all FAILED_WILL_RETRY or UNKNOWN devices as FAILED
            int affectedRows = recentEventParticipationDao.failWillRetryDevices(eventId);
            log.info(
                    "[external event id:{}] No more retries available, changed {} devices waiting for retry (FAILED_WILL_RETRY, UNKNOWN) to failed (FAILED).",
                    eventId, affectedRows);
            return false;
        }
        
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }

    /**
     * Returns shed parameters
     */
    private Map<String, Object> getShedParams(LMEatonCloudScheduledCycleCommand command, int eventId) {
        Map<String, Object> params = new LinkedHashMap<>();
        long startTimeSeconds = System.currentTimeMillis() / 1000;
        long stopTimeSeconds = 0;

        if (command.getControlStartDateTime() != null) {
            startTimeSeconds = command.getControlStartDateTime().getMillis() / 1000;
        }

        if (command.getControlEndDateTime() != null) {
            stopTimeSeconds = command.getControlEndDateTime().getMillis() / 1000;
        }

        double durationSeconds = stopTimeSeconds - startTimeSeconds;
        int cycleCount = 1;
        if (stopTimeSeconds != 0) {
            cycleCount = (int) Math.ceil(durationSeconds / command.getDutyCyclePeriod());
        }

        /*
         * See LCR Control Command Payloads reference:
         * https://confluence-prod.tcc.etn.com/pages/viewpage.action?pageId=137056391
         * 
         * randomization | stop flag
         * No Ramp | 0 | 0
         * Ramp In | 2 | 0
         * Ramp Out | 1 | 0
         * Ramp In/Out | 2 | 1
         */

        int randomization = 0;
        int stopFlag = 0;

        if (command.getIsRampIn()) {
            randomization = 2;
        }
        if (command.getIsRampOut() && !command.getIsRampIn()) {
            randomization = 1;
        }
        if (command.getIsRampIn() && command.getIsRampOut()) {
            stopFlag = 1;
        }

        params.put(CommandParam.VRELAY.getParamName(), command.getVirtualRelayId() - 1);
        params.put(CommandParam.CYCLE_PERCENT.getParamName(), command.getDutyCyclePercentage());
        params.put(CommandParam.CYCLE_PERIOD.getParamName(), command.getDutyCyclePeriod() / 60);
        params.put(CommandParam.CYCLE_COUNT.getParamName(), cycleCount);
        params.put(CommandParam.START_TIME.getParamName(), startTimeSeconds);
        params.put(CommandParam.EVENT_ID.getParamName(), eventId);
        params.put(CommandParam.CRITICALITY.getParamName(), command.getCriticality());
        params.put(CommandParam.RANDOMIZATION.getParamName(), randomization);
        params.put(CommandParam.CONTROL_FLAGS.getParamName(), 0);
        params.put(CommandParam.STOP_TIME.getParamName(), stopTimeSeconds);
        params.put(CommandParam.STOP_FLAGS.getParamName(), stopFlag);
        return params;
    }

    private void processError(Integer eventId, String deviceName, Integer deviceId, String guid,
            LMEatonCloudScheduledCycleCommand command, String message, int tryNumber) {

        eatonCloudEventLogService.sendShedFailed(deviceName,
                guid,
                eventId.toString(),
                String.valueOf(tryNumber), 
                command.getDutyCyclePercentage(),
                command.getDutyCyclePeriod(),
                command.getCriticality(),
                command.getVirtualRelayId(),
                truncateErrorForEventLog(message));

        recentEventParticipationDao.updateDeviceControlEvent(eventId.toString(),
                deviceId,
                ControlEventDeviceStatus.FAILED_WILL_RETRY,
                Instant.now(),
                StringUtils.isEmpty(message) ? null : message.length() > 100 ? message.substring(0, 100) : message,
                null);
    }

    private String truncateErrorForEventLog(String message) {
        return StringUtils
                .isEmpty(message) ? "See log for details" : message.length() > 2000 ? message.substring(0, 2000) : message;
    }

    private Map<String, Object> getRestoreParams(LMEatonCloudStopCommand command, int eventId) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(CommandParam.EVENT_ID.getParamName(), eventId);
        params.put(CommandParam.FLAGS.getParamName(), 0);
        return params;
    }
}
