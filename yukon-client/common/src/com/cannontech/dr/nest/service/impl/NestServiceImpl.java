package com.cannontech.dr.nest.service.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.nest.model.LoadShaping;
import com.cannontech.dr.nest.model.LoadShapingPeak;
import com.cannontech.dr.nest.model.LoadShapingPost;
import com.cannontech.dr.nest.model.LoadShapingPreparation;
import com.cannontech.dr.nest.model.NestEventId;
import com.cannontech.dr.nest.model.StandardEvent;
import com.cannontech.dr.nest.service.NestCommunicationService;
import com.cannontech.dr.nest.service.NestService;
import com.cannontech.dr.service.ControlHistoryService;
import com.google.common.collect.Lists;

public class NestServiceImpl implements NestService {

    @Autowired private NestCommunicationService nestCommunicationService;
    @Autowired private ControlHistoryService controlHistoryService;
    
    private ConcurrentHashMap<String, String> groupsToEventIds = new ConcurrentHashMap<>();

    @Override
    public void control() {

        List<String> groups = Lists.newArrayList("Test");

        // CriticalEvent criticalEvent = new CriticalEvent("2018-09-14T00:00:00.000Z", "PT30M", groups);
        // String eventId = nestCommunicationService.createCriticalEvent(criticalEvent);
        // groupsToEventIds.put("Group1", eventId);

        LoadShaping loadShaping =
            new LoadShaping(LoadShapingPreparation.STANDARD, LoadShapingPeak.STANDARD, LoadShapingPost.STANDARD);
        StandardEvent standardEvent = new StandardEvent("2018-09-14T00:00:00.000Z", "PT30M", groups, loadShaping);
        NestEventId eventId = nestCommunicationService.createStandardEvent(standardEvent);
        
    
        
        if(eventId == null) {
            //got an error - log?
            //don't send message controlHistoryService?
           // controlHistoryService.sendControlHistoryShedMessage(groupId, startTime, controlType, associationId, controlDurationSeconds, reductionRatio);sendControlHistoryRestoreMessage(groupId, time);
        } else{
            groupsToEventIds.put("Test", eventId.getId());
            //send message controlHistoryService?
        }
    }
}
