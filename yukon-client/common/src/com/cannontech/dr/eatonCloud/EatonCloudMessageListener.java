package com.cannontech.dr.eatonCloud;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.recenteventparticipation.service.RecentEventParticipationService;
import com.cannontech.dr.service.ControlHistoryService;
import com.cannontech.dr.service.ControlType;
import com.cannontech.yukon.IDatabaseCache;

public class EatonCloudMessageListener {
    private static final Logger log = YukonLogManager.getLogger(EatonCloudMessageListener.class);
    
    @Autowired private ControlHistoryService controlHistoryService;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private RecentEventParticipationService recentEventParticipationService;

    public void handleCyclingControlMessage(int groupId, Instant startTime, Instant endTime, int dutyCyclePercent) {
        Duration controlDuration = new Duration(startTime, endTime);
        int controlDurationSeconds = controlDuration.toStandardSeconds().getSeconds();

        log.debug("LM Eaton Cloud Command - Group Id: {}, startTime: {}, endTime: {}, Duty Cycle Percent: {}", groupId, startTime, endTime, dutyCyclePercent);

        recentEventParticipationService.createDeviceControlEvent(0, // ProgramId - I don't think we get/have this
                                                                 0, // EventId - I don't think we get/have this
                                                                 groupId,
                                                                 startTime,
                                                                 endTime);

        controlHistoryService.sendControlHistoryShedMessage(groupId, 
                                                            startTime, 
                                                            ControlType.EATON_CLOUD, 
                                                            null,
                                                            controlDurationSeconds, 
                                                            dutyCyclePercent);
    }

    public void handleRestoreMessage(int groupId, Instant restoreTime) {
        log.debug("LM Eaton Cloud Command - Group Id: {}, Restore Time: {}", groupId, restoreTime);

        LiteYukonPAObject group = dbCache.getAllLMGroups().stream()
                .filter(g -> g.getLiteID() == groupId).findAny().orElse(null);
        if (group == null) {
            log.error("Group with id {} is not found", groupId);
            return;
        }

        controlHistoryService.sendControlHistoryRestoreMessage(groupId, Instant.now());
    }

}
