package com.cannontech.dr.eatonCloud;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.eatonCloud.service.EatonCloudSendControlService;
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
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private OptOutEventDao optOutEventDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private RecentEventParticipationService recentEventParticipationService;
    @Autowired private ApplianceAndProgramDao applianceAndProgramDao;
    @Autowired private RecentEventParticipationDao recentEventParticipationDao;
    @Autowired private EatonCloudSendControlService eatonCloudSendControlService;
    
    
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
        
        eatonCloudSendControlService.sendInitialShedCommand(programId, devices, command, eventId);
        
        controlHistoryService.sendControlHistoryShedMessage(command.getGroupId(),
                command.getControlStartDateTime(),
                ControlType.EATON_CLOUD,
                null,
                controlDurationSeconds,
                command.getDutyCyclePeriod());
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
            eatonCloudSendControlService.sendRestoreCommands(devices, command, eventId);
        }
        controlHistoryService.sendControlHistoryRestoreMessage(command.getGroupId(), Instant.now());
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