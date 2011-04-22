package com.cannontech.thirdparty.digi;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.thirdparty.digi.dao.impl.DigiControlEventDao;
import com.cannontech.thirdparty.exception.DigiWebServiceException;
import com.cannontech.thirdparty.messaging.ControlHistoryMessage;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.service.SepMessageHandler;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.cannontech.yukon.IServerConnection;

public class DigiControlMessageHandler implements SepMessageHandler {

    private static final Logger logger = YukonLogManager.getLogger(DigiControlMessageHandler.class);
    
    private IServerConnection dispatchConnection;
    private AttributeService attributeService;
    private DynamicDataSource dynamicDataSource;
    private ZigbeeWebService zigbeeWebService;
    private NextValueHelper nextValueHelper;
    private DigiControlEventDao digiControlEventDao;
    
    private Set<Integer> pendingEvents = new HashSet<Integer>();
    
    @Override
    public boolean handlePao(PaoIdentifier paoIdentifier) {
        
        if (paoIdentifier.getPaoType() == PaoType.LM_GROUP_DIGI_SEP) {
            return true;
        }
        
        return false;
    }

    @Override
    public void handleControlMessage(PaoIdentifier paoIdentifier, SepControlMessage message) {
        int eventId = nextValueHelper.getNextValue("DigiControlEventMapping");
        Date now = new Date();

        //Creating the event prior will leave us with an EventId to attempt to cancel in case of a partial control
        digiControlEventDao.createNewEventMapping(eventId,message.getGroupId(),now);
        
        try {            
            //Do what needs to be done
            int deviceCount = zigbeeWebService.sendSEPControlMessage(eventId, message);
            digiControlEventDao.updateDeviceCount(eventId, deviceCount);
            
            //Saving the EventId to qualify AssociationMessages from Dispatch
            pendingEvents.add(eventId);
    
            //If success return a control history message to dispatch.
            ControlHistoryMessage chMessage = buildControlHistoryMessage(eventId, paoIdentifier, message, now);
            dispatchConnection.queue(chMessage);

        } catch (DigiWebServiceException e) {
            logger.error("Error in ZigBeeWebService: " + e.getMessage());
        }
    }

    @Override
    public void handleRestoreMessage(PaoIdentifier paoIdentifier, SepRestoreMessage message) {
        int eventId = digiControlEventDao.findCurrentEventId(message.getGroupId());
        
        try {
            zigbeeWebService.sendSEPRestoreMessage(eventId, message);
    
            //If success return a control history message to dispatch.
            ControlHistoryMessage histMsg = buildControlHistoryMessageForRestore(paoIdentifier,message, new Date());        
            dispatchConnection.queue(histMsg);
            
        } catch (DigiWebServiceException e) {
            logger.error("Error in ZigBeeWebService: " + e.getMessage());
        }
    }
    
    private ControlHistoryMessage buildControlHistoryMessageForRestore(PaoIdentifier paoIdentifier, SepRestoreMessage message, Date date) {
        ControlHistoryMessage chMessage = buildCommonControlHistoryMessage(0,paoIdentifier,message.getGroupId(),date);
        
        chMessage.setControlDuration(0);
        chMessage.setReductionRatio(0);
        
        chMessage.setActiveRestore("M");
        chMessage.setReductionValue(0.0);

        return chMessage;
    }
    
    private ControlHistoryMessage buildControlHistoryMessage(int eventId, PaoIdentifier paoIdentifier, SepControlMessage message, Date date) {
        ControlHistoryMessage chMessage = buildCommonControlHistoryMessage(eventId,paoIdentifier,message.getGroupId(),date);
        
        chMessage.setControlDuration(message.getControlMinutes());
        
        int standardCycle = message.getStandardCyclePercent();
        int averageCycle = Math.abs(message.getAverageCyclePercent());
        
        //Standard Cycle is one of the two values above depending on:
        //if standard 0<x<=100 use it
        //if average -1 < x >= -100
        //else 100
        
        if (standardCycle > 0 || standardCycle <= 100) {
            chMessage.setReductionRatio(standardCycle);
        } else if (averageCycle > 0 || averageCycle <= 100) {
            chMessage.setReductionRatio(averageCycle);
        } else {
            chMessage.setReductionRatio(100);
        }
        
        //Distinguish between activate or restore  "Y" or "N"
        if (message.getControlMinutes() > 0) {
            chMessage.setActiveRestore("Y");
        }
        else {
            chMessage.setActiveRestore("N");
        }
        
        chMessage.setReductionValue(0.0);
        
        return chMessage;
    }
    
    private ControlHistoryMessage buildCommonControlHistoryMessage(int eventId, PaoIdentifier paoIdentifier, int groupId, Date date) {
        LitePoint point = attributeService.getPointForAttribute(paoIdentifier, BuiltInAttribute.LM_GROUP_STATUS);
        PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(point.getLiteID());
        
        ControlHistoryMessage chMessage = new ControlHistoryMessage();
        
        chMessage.setPaoId(groupId);
        chMessage.setPointId(point.getLiteID());
        chMessage.setRawState((int)pointValue.getValue());

        chMessage.setStartTime(date);
        chMessage.setControlType("Digi Control");
        chMessage.setControlPriority(0);
        chMessage.setAssociationId(eventId);
        
        return chMessage;
    }
    
    public void handleAssociationMessage(int eventId, int controlHistoryId) {
        //Check if this is our Event;
        if (pendingEvents.contains(eventId)) {
            digiControlEventDao.associateControlHistory(eventId,controlHistoryId);
            pendingEvents.remove(eventId);
        }
    }
    
    @Autowired
    public void setDispatchConnection(IServerConnection dispatchConnection) {
        this.dispatchConnection = dispatchConnection;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Autowired
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }
    
    @Autowired
    public void setZigbeeWebService(ZigbeeWebService zigbeeWebService) {
        this.zigbeeWebService = zigbeeWebService;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setDigiControlEventDao(DigiControlEventDao digiControlEventDao) {
        this.digiControlEventDao = digiControlEventDao;
    }
}
