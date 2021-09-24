package com.cannontech.dr.eatonCloud;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
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
import com.cannontech.common.smartNotification.model.EatonCloudDrEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.eatonCloud.model.EatonCloudException;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandRequestV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandResponseV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudDataReadService;
import com.cannontech.dr.recenteventparticipation.ControlEventDeviceStatus;
import com.cannontech.dr.recenteventparticipation.dao.RecentEventParticipationDao;
import com.cannontech.dr.recenteventparticipation.service.RecentEventParticipationService;
import com.cannontech.dr.service.ControlHistoryService;
import com.cannontech.dr.service.ControlType;
import com.cannontech.loadcontrol.messages.LMEatonCloudScheduledCycleCommand;
import com.cannontech.loadcontrol.messages.LMEatonCloudStopCommand;
import com.cannontech.stars.dr.account.dao.ApplianceAndProgramDao;
import com.cannontech.stars.dr.account.model.ProgramLoadGroup;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.math.IntMath;

public class EatonCloudMessageListener {
    private static final Logger log = YukonLogManager.getLogger(EatonCloudMessageListener.class);
    
    @Autowired private ControlHistoryService controlHistoryService;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DeviceDao deviceDao;
    @Autowired private EatonCloudEventLogService eatonCloudEventLogService;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private OptOutEventDao optOutEventDao;
    @Autowired private EatonCloudCommunicationServiceV1 eatonCloudCommunicationService;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private RecentEventParticipationService recentEventParticipationService;
    @Autowired private ApplianceAndProgramDao applianceAndProgramDao;
    @Autowired private RecentEventParticipationDao recentEventParticipationDao;
    @Autowired private SmartNotificationEventCreationService smartNotificationEventCreationService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private EatonCloudDataReadService eatonCloudDataReadService;
    private Executor executor = Executors.newCachedThreadPool();
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    
    // <event id, Pair<next read time, sent time>>
    private Map<Integer, Pair<Instant, Instant>> nextRead = new ConcurrentHashMap<Integer, Pair<Instant, Instant>>();
    
    private int failureNotificationPercent;
    
    @PostConstruct
    public void init() {
        failureNotificationPercent = configurationSource.getInteger(
            MasterConfigInteger.EATON_CLOUD_NOTIFICATION_COMMAND_FAILURE_PERCENT, 25);
        schedule();
    }
                     
    private void schedule() {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            readDevices();
        }, 0, 1, TimeUnit.MINUTES);
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
                    Range<Instant> range = new Range<Instant>(sendTime, true, Instant.now(), true);
                    Set<Integer> devicesToRead = recentEventParticipationDao.getDeviceIdsByExternalEventIdAndStatuses(eventId,
                            List.of(ControlEventDeviceStatus.SUCCESS_RECEIVED));
                    executor.execute(() -> {
                        if (!devicesToRead.isEmpty()) {
                            log.info("Reading devices: {} event id: {} for date range:{}-{} [original command sent at {}] ", 
                                    devicesToRead.size(),
                                    eventId,
                                    range.getMin().toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"),
                                    range.getMax().toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"),
                                    sendTime.toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"));
                            eatonCloudDataReadService.collectDataForRead(devicesToRead, range);
                        } else {
                            log.info("Can't find find devices to read. Devices with status SUCCESS_RECEIVED not found for event id: {} for date range:{}-{} [original command sent at {}] ", 
                                    eventId,
                                    range.getMin().toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"),
                                    range.getMax().toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"),
                                    sendTime.toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"));
                        }
                    });
                    iter.remove();
                }
            }
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    public enum CommandParam {
        FLAGS("flags"),
        VRELAY("vrelay"),
        CYCLE_PERCENT("cycle percent"),
        CYCLE_PERIOD("cycle period"),
        CYCLE_COUNT("cycle count"),
        START_TIME("start time"),
        CONTROL_FLAGS("control flags"),
        STOP_TIME("stop time"),
        STOP_FLAGS("stop flags"), 
        CRITICALITY("criticality"),
        EVENT_ID("event id"),
        RANDOMIZATION("randomization");
        
        private String paramName;

        private CommandParam(String paramName) {
            this.paramName = paramName;
        }

        public String getParamName() {
            return paramName;
        } 
    }
    
    public void handleCyclingControlMessage(LMEatonCloudScheduledCycleCommand command) {
        Duration controlDuration = new Duration(command.getControlStartDateTime(), command.getControlEndDateTime());
        int controlDurationSeconds = controlDuration.toStandardSeconds().getSeconds();

        Set<Integer> devices = findInventoryAndRemoveOptOut(command.getGroupId());
        if(devices.isEmpty()) {
            log.info("No devices found for groupId:{}", command.getGroupId());
            return;  
        }   

        Integer eventId = nextValueHelper.getNextValue("EatonCloudEventIdIncrementor");

        List<ProgramLoadGroup> programsByLMGroupId = applianceAndProgramDao.getProgramsByLMGroupId(command.getGroupId());
        int programId = programsByLMGroupId.get(0).getPaobjectId();
        
        recentEventParticipationService.createDeviceControlEvent(programId, 
                String.valueOf(eventId), 
                command.getGroupId(),
                command.getControlStartDateTime(), 
                command.getControlEndDateTime());
        
        sendShedCommands(programId, devices, command, eventId);
        
        controlHistoryService.sendControlHistoryShedMessage(command.getGroupId(),
                command.getControlStartDateTime(),
                ControlType.EATON_CLOUD,
                null,
                controlDurationSeconds,
                command.getDutyCyclePeriod());
    }

    private void sendShedCommands(int programId, Set<Integer> devices, LMEatonCloudScheduledCycleCommand command, Integer eventId) {
        
        log.info("Sending LM Eaton Cloud Shed Command:{} devices:{} event id:{}", command, devices.size(), eventId);
        
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
                log.trace("Attempting to send shed command to device id:{} guid:{} name:{} event id:{} relay:{}", deviceId, guid,
                        deviceName, eventId, command.getVirtualRelayId());
                EatonCloudCommandResponseV1 response = eatonCloudCommunicationService.sendCommand(guid,
                        new EatonCloudCommandRequestV1("LCR_Control", params));
                if (response.getStatusCode() == HttpStatus.OK.value()) {
                    recentEventParticipationDao.updateDeviceControlEvent(eventId.toString(), deviceId,
                            ControlEventDeviceStatus.SUCCESS_RECEIVED, new Instant(),
                            null, null);
                    totalSucceeded.incrementAndGet();
                    log.trace("Success sending shed command to device id:{} guid:{} name:{} eventId:{} relay:{}", deviceId,
                            guid, deviceName, eventId, command.getVirtualRelayId(), params);
                } else {
                    throw new EatonCloudException(response.getMessage());
                }
            } catch (EatonCloudCommunicationExceptionV1 e) {
                totalFailed.getAndIncrement();
                log.error("Error sending shed command device id:{} guid:{} name:{} eventId:{} relay:{}", deviceId, guid,
                        deviceName, eventId, command.getVirtualRelayId(), e);
                processError(eventId, params, deviceId, e.getErrorMessage().getMessage());
            } catch (EatonCloudException e) {
                totalFailed.getAndIncrement();
                log.error("Error sending shed command device id:{} guid:{} name:{} eventId:{} relay:{}", deviceId, guid,
                        deviceName, eventId, command.getVirtualRelayId(), e);
                processError(eventId, params, deviceId, e.getMessage());
            }

            eatonCloudEventLogService.sendShed(deviceName,
                    guid,
                    command.getDutyCyclePercentage(),
                    command.getDutyCyclePeriod(),
                    command.getCriticality());
        });

        stopwatch.stop();
        if (log.isDebugEnabled()) {
            var duration = Duration.standardSeconds((long) stopwatch.getTotalTimeSeconds());
            log.debug("Commands timer - devices: {}, total time: {}", guids.size(), duration);
        }

        int readTimeFromNowInMinutes = command.getDutyCyclePeriod() == null ? 5 : IntMath.divide(command.getDutyCyclePeriod() / 60,
                2, RoundingMode.CEILING);
        Instant nextReadTime = DateTime.now().plusMinutes(readTimeFromNowInMinutes).toInstant();
        nextRead.put(eventId, Pair.of(nextReadTime, sendTime));

        sendSmartNotifications(command.getGroupId(), programId, devices.size(), totalFailed.intValue());
        
        log.info(
                "Finished sending LM Eaton Cloud Shed Command:{} devices:{} event id:{} relay:{} failed:{} succeeded:{} next device read in {} minutes at {}",
                command,
                devices.size(),
                eventId, 
                command.getVirtualRelayId(), 
                totalFailed.intValue(), 
                totalSucceeded,
                readTimeFromNowInMinutes,
                nextReadTime.toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"));
        
    }
    
    private void sendSmartNotifications(int groupId, int programId, int totalDevices, int totalFailed) {
        boolean sendNotification = (totalFailed * 100) / totalDevices > failureNotificationPercent;
        if (sendNotification) {
            String program = dbCache.getAllLMPrograms().stream().filter(p -> p.getLiteID() == programId).findFirst().get()
                    .getPaoName();
            String group = dbCache.getAllLMGroups().stream().filter(p -> p.getLiteID() == groupId).findFirst().get()
                    .getPaoName();
            SmartNotificationEvent event = EatonCloudDrEventAssembler.assemble(group, program, totalDevices, totalFailed);

            log.debug("Sending event: {}", event);
            smartNotificationEventCreationService.send(SmartNotificationEventType.EATON_CLOUD_DR, List.of(event));
        }
    }

    private void processError(Integer eventId, Map<String, Object> params, Integer deviceId, String message) {
        recentEventParticipationDao.updateDeviceControlEvent(eventId.toString(),
                deviceId,
                ControlEventDeviceStatus.FAILED,
                new Instant(),
                StringUtils.isEmpty(message) ? null : message.length() > 100 ? message.substring(0, 100) : message,
                null);
    }
    
    private void sendRestoreCommands(Set<Integer> devices, LMEatonCloudStopCommand command, Integer eventId) {
        Map<String, Object> params = getRestoreParams(command, eventId);
        Map<Integer, String> guids = deviceDao.getGuids(devices);
        
        log.info("Sending LM Eaton Cloud Restore Command:{} devices:{} event id:{}", command, devices.size(), eventId);
        
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
            log.trace("Attampting to send restore command to device id:{} eventId:{} name:{} relay:{}", deviceId,
                    eventId, deviceName, command.getVirtualRelayId());
            try {
                eatonCloudCommunicationService.sendCommand(guid, new EatonCloudCommandRequestV1("LCR_Control", params));
                eatonCloudEventLogService.sendRestore(deviceName, guid);
            } catch (Exception e) {
                totalFailed.incrementAndGet();
                log.error("Error sending restore command to device id:{} eventId:{} name:{} relay:{}", deviceId,
                        eventId, deviceName, command.getVirtualRelayId());
            }
        });
        stopwatch.stop();
        if (log.isDebugEnabled()) {
            var duration = Duration.standardSeconds((long) stopwatch.getTotalTimeSeconds());
            log.debug("Commands timer - devices: {}, total time: {}", guids.size(), duration);
        }
        log.info("Finished sending LM Eaton Cloud Restore Command:{} devices:{} event id:{} relay:{} failed:{}", command,
                devices.size(), eventId, command.getVirtualRelayId(), totalFailed.intValue());
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
         *                RampIN - TRUE | RampIN - FALSE
         * randomization|        1      |        0
         * 
         *               RampOUT - TRUE | RampOUT - FALSE
         * stop flag    |        1      |         0
         */

        params.put(CommandParam.VRELAY.getParamName(), command.getVirtualRelayId() - 1);
        params.put(CommandParam.CYCLE_PERCENT.getParamName(), command.getDutyCyclePercentage());
        params.put(CommandParam.CYCLE_PERIOD.getParamName(), command.getDutyCyclePeriod() / 60);
        params.put(CommandParam.CYCLE_COUNT.getParamName(), cycleCount);
        params.put(CommandParam.START_TIME.getParamName(), startTimeSeconds);
        params.put(CommandParam.EVENT_ID.getParamName(), eventId);
        params.put(CommandParam.CRITICALITY.getParamName(), command.getCriticality());
        params.put(CommandParam.RANDOMIZATION.getParamName(), command.getIsRampIn() ? 1 : 0);
        params.put(CommandParam.CONTROL_FLAGS.getParamName(), 0);
        params.put(CommandParam.STOP_TIME.getParamName(), stopTimeSeconds);
        params.put(CommandParam.STOP_FLAGS.getParamName(), command.getIsRampOut() ? 1 : 0);
        return params;
    }

    public void handleRestoreMessage(LMEatonCloudStopCommand command) {
        LiteYukonPAObject group = dbCache.getAllLMGroups().stream()
                .filter(g -> g.getLiteID() == command.getGroupId()).findAny().orElse(null);
        if (group == null) {
            log.error("Group with id {} is not found", command.getGroupId());
            return;
        }

        Set<Integer> devices = findInventoryAndRemoveOptOut(command.getGroupId());
        if (devices.isEmpty()) {
            log.info("No devices found for groupId: {}", command.getGroupId());
            return;
        }
        
        Integer eventId = recentEventParticipationDao.getExternalEventId(command.getGroupId());
        if (eventId != null) {
            log.info("Sending LM Eaton Cloud Restore Command:{} devices:{} event id:{}", command, devices.size(), eventId);
            sendRestoreCommands(devices, command, eventId);
        }
        controlHistoryService.sendControlHistoryRestoreMessage(command.getGroupId(), Instant.now());
    }
    
    private Map<String, Object> getRestoreParams(LMEatonCloudStopCommand command, int eventId) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(CommandParam.EVENT_ID.getParamName(), eventId);
        params.put(CommandParam.FLAGS.getParamName(), 0);
        return params;
    }

    /**
     * Returns valid devices, removes opt outs
     */
    private Set<Integer> findInventoryAndRemoveOptOut(Integer groupId) {
        Set<Integer> optOutInventory = optOutEventDao.getOptedOutInventoryByLoadGroups(Arrays.asList(groupId));
        Set<Integer> inventoryIds = enrollmentDao.getActiveEnrolledInventoryIdsForGroupIds(Arrays.asList(groupId));
        inventoryIds.removeAll(optOutInventory);
        Set<Integer> deviceIds = inventoryDao.getDeviceIds(inventoryIds).values().stream().collect(Collectors.toSet());
        return deviceIds;
    }

}