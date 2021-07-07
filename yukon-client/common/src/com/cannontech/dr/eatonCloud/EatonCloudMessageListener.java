package com.cannontech.dr.eatonCloud;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    
    // next read time, send time, set of ids to read
    private Map<Pair<Instant, Instant>, Set<Integer>> nextRead = Collections.synchronizedMap(new HashMap<Pair<Instant, Instant>, Set<Integer>>());
    
    private int failureNotificationPercent;
    
    @PostConstruct
    public void init() {
        failureNotificationPercent = configurationSource.getInteger(
            MasterConfigInteger.EATON_CLOUD_NOTIFICATION_COMMAND_FAILURE_PERCENT, 25);
        scheduleReads();
    }
                     
    private void scheduleReads() {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                Iterator<Pair<Instant, Instant>> iter = nextRead.keySet().iterator();
                // For each key in cache
                while (iter.hasNext()) {
                    Pair<Instant, Instant> time = iter.next();
                    if (time.getKey().isEqualNow() || time.getKey().isBeforeNow()) {
                        Instant max = Instant.now();
                        // command send time
                        Instant min = time.getValue();
                        Range<Instant> range = new Range<Instant>(min, true, max, true);
                        Set<Integer> devicesToRead = nextRead.get(time);
                        executor.execute(() -> {
                            log.debug("Reading devices {}  {}-{}", range.getMin().toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"),
                                    range.getMax().toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"));
                            eatonCloudDataReadService.collectDataForRead(devicesToRead, range);
                        });
                        iter.remove();
                    }
                }
            } catch (Exception e) {
                log.error("Error", e);
            }
        }, 0, 1, TimeUnit.MINUTES);
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
        EVENT_ID("event id");
        
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
        log.info("Sending LM Eaton Cloud Shed Command:{} devices:{} event id:{}", command, devices.size(), eventId);
        
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
        Map<String, Object> params = getShedParams(command, eventId);
        Map<Integer, String> guids = deviceDao.getGuids(devices);
        AtomicInteger totalFailed = new AtomicInteger(0);
        Set<Integer> successDeviceIds = new HashSet<>();
        Instant sendTime = new Instant();
        
        guids.forEach((deviceId, guid) -> {
            String deviceName = dbCache.getAllDevices().stream()
                    .filter(d -> d.getLiteID() == deviceId)
                    .findAny()
                    .map(d -> d.getPaoName())
                    .orElse(null);
            try {
                EatonCloudCommandResponseV1 response = eatonCloudCommunicationService.sendCommand(guid,
                        new EatonCloudCommandRequestV1("LCR_Control", params));
                if (response.getStatusCode() == HttpStatus.OK.value()) {
                    recentEventParticipationDao.updateDeviceControlEvent(eventId.toString(), deviceId,
                            ControlEventDeviceStatus.SUCCESS_RECEIVED, new Instant(),
                            null, null);
                    successDeviceIds.add(deviceId);
                } else {
                    throw new EatonCloudException(response.getMessage());
                }
            } catch (EatonCloudCommunicationExceptionV1 e) {
                totalFailed.getAndIncrement();
                log.error("Error sending command device id:{} params:{}", deviceId, params, e);
                processError(eventId, params, deviceId, e.getErrorMessage().getMessage());
            } catch (EatonCloudException e) {
                totalFailed.getAndIncrement();
                log.error("Error sending command device id:{} params:{}", deviceId, params, e);
                processError(eventId, params, deviceId, e.getMessage());
            }
            
            eatonCloudEventLogService.sendShed(deviceName,
                    guid,
                    command.getDutyCyclePercentage(),
                    command.getDutyCyclePeriod(),
                    command.getCriticality());
        });
        
        DateTime dateTime = new DateTime();
        if (!successDeviceIds.isEmpty()) {
            int readTimeFromNowInMinutes = command.getDutyCyclePeriod() == null ? 5 : IntMath.divide(command.getDutyCyclePeriod(),
                    2, RoundingMode.CEILING);
            nextRead.put(Pair.of(dateTime.plusMinutes(readTimeFromNowInMinutes).toInstant(), sendTime), successDeviceIds);
        }
        sendSmartNotifications(command.getGroupId(), programId, devices.size(), totalFailed.intValue());
        
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
        guids.forEach((deviceId, guid) -> {
            try {
                String deviceName = dbCache.getAllDevices().stream()
                        .filter(d -> d.getLiteID() == deviceId)
                        .findAny()
                        .map(d -> d.getPaoName())
                        .orElse(null);
                
                eatonCloudCommunicationService.sendCommand(guid, new EatonCloudCommandRequestV1("LCR_Control", params));
                eatonCloudEventLogService.sendRestore(deviceName, guid);
            } catch (Exception e) {
                log.error("Error sending command device id:{} params:{}", deviceId, params, e);
            }
        });
    }

    /**
     * Returns shed parameters
     */
    private Map<String, Object> getShedParams(LMEatonCloudScheduledCycleCommand command, int eventId) {
        Map<String, Object> params = new HashMap<>();
        if (command.getControlStartDateTime() != null) {
            params.put(CommandParam.START_TIME.getParamName(), command.getControlStartDateTime().getMillis() / 1000);
        } else {
            params.put(CommandParam.START_TIME.getParamName(), System.currentTimeMillis() / 1000);
        }
        
        if (command.getControlEndDateTime() != null) {
            params.put(CommandParam.STOP_TIME.getParamName(), command.getControlEndDateTime().getMillis() / 1000);
        } else {
            params.put(CommandParam.STOP_TIME.getParamName(), 0);
        }
        
        params.put(CommandParam.EVENT_ID.getParamName(), eventId);
        params.put(CommandParam.CYCLE_PERCENT.getParamName(), command.getDutyCyclePercentage());
        params.put(CommandParam.CYCLE_COUNT.getParamName(), command.getDutyCyclePeriod());
        params.put(CommandParam.CRITICALITY.getParamName(), command.getCriticality());
        params.put(CommandParam.CONTROL_FLAGS.getParamName(), 0);
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
        if (command.getRestoreTime() != null) {
            params.put(CommandParam.STOP_TIME.getParamName(), command.getRestoreTime().getMillis() / 1000);
        } else {
            params.put(CommandParam.STOP_TIME.getParamName(), 0);
        }
        params.put(CommandParam.STOP_FLAGS.getParamName(), 0);
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
