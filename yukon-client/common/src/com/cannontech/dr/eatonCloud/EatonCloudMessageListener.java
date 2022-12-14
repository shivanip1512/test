package com.cannontech.dr.eatonCloud;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.EatonCloudEventLogService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandRequestV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
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
   
        sendShedCommands(devices, command, eventId);
        
        List<ProgramLoadGroup> programsByLMGroupId = applianceAndProgramDao.getProgramsByLMGroupId(command.getGroupId());
        int programId = programsByLMGroupId.get(0).getPaobjectId();
        
        recentEventParticipationService.createDeviceControlEvent(programId, 
                String.valueOf(eventId), 
                command.getGroupId(),
                command.getControlStartDateTime(), 
                command.getControlEndDateTime());
        
        controlHistoryService.sendControlHistoryShedMessage(command.getGroupId(),
                command.getControlStartDateTime(),
                ControlType.EATON_CLOUD,
                null,
                controlDurationSeconds,
                command.getDutyCyclePeriod());
    }

    private void sendShedCommands(Set<Integer> devices, LMEatonCloudScheduledCycleCommand command, Integer eventId) {
        Map<String, Object> params = getShedParams(command, eventId);
        Map<Integer, String> guids = deviceDao.getGuids(devices);
        guids.forEach((deviceId, guid) -> {
            try {
                String deviceName = dbCache.getAllDevices().stream()
                                                           .filter(d -> d.getLiteID() == deviceId)
                                                           .findAny()
                                                           .map(d -> d.getPaoName())
                                                           .orElse(null);

                eatonCloudCommunicationService.sendCommand(guid, new EatonCloudCommandRequestV1("LCR_Control", params));
                eatonCloudEventLogService.sendShed(deviceName, 
                                                   guid, 
                                                   command.getDutyCyclePercentage(),
                                                   command.getDutyCyclePeriod(),
                                                   command.getCriticality());
            } catch (Exception e) {
                log.error("Error sending command device id:{} params:{}", deviceId, params, e);
            }
        });
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
