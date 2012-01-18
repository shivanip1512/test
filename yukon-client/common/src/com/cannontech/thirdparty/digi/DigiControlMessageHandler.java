package com.cannontech.thirdparty.digi;

import static com.cannontech.thirdparty.messaging.ControlHistoryMessage.CONTROL_RESTORE_DURATION;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.CancelZigbeeText;
import com.cannontech.common.model.ZigbeeTextMessage;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.thirdparty.digi.dao.ZigbeeControlEventDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.exception.ZigbeeClusterLibraryException;
import com.cannontech.thirdparty.messaging.ControlHistoryMessage;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.service.SepMessageHandler;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.cannontech.yukon.IServerConnection;

public class DigiControlMessageHandler implements SepMessageHandler {

    private static final Logger log = YukonLogManager.getLogger(DigiControlMessageHandler.class);
    
    private IServerConnection dispatchConnection;
    private AttributeService attributeService;
    private ZigbeeWebService zigbeeWebService;
    private NextValueHelper nextValueHelper;
    private PaoDao paoDao;
    private ZigbeeDeviceDao zigbeeDeviceDao;
    private ZigbeeControlEventDao zigbeeControlEventDao;
    
    private Set<Integer> pendingEvents = new HashSet<Integer>();
    
    @Override
    public boolean handlePao(PaoIdentifier paoIdentifier) {
        
        if (paoIdentifier.getPaoType() == PaoType.LM_GROUP_DIGI_SEP) {
            return true;
        }
        
        return false;
    }

    @Override
    public void handleControlMessage(SepControlMessage message) {
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(message.getGroupId());
        log.info("Sending Control Command to Load Group: " + pao.getPaoName());
        
        int eventId = nextValueHelper.getNextValue("ZBControlEvent");
        Instant now = new Instant();

        //Creating the event prior will leave us with an EventId to attempt to cancel in case of a partial control
        zigbeeControlEventDao.createNewEventMapping(eventId,message.getGroupId(),now);
        
        try {
            //Do what needs to be done
            zigbeeWebService.sendSEPControlMessage(eventId, message);
            
            //Log the devices for this event.
            List<ZigbeeDevice> devices =  zigbeeDeviceDao.getZigbeeDevicesForGroupId(message.getGroupId());
            for (ZigbeeDevice device : devices) {
                zigbeeControlEventDao.insertDeviceControlEvent(eventId, device.getZigbeeDeviceId());
            }
            
            //Saving the EventId to qualify AssociationMessages from Dispatch
            pendingEvents.add(eventId);
    
            //If success return a control history message to dispatch.
            ControlHistoryMessage chMessage = buildControlHistoryMessage(eventId, message, now);
            dispatchConnection.queue(chMessage);

        } catch (DigiWebServiceException e) {
            log.error("Error in ZigBeeWebService: " + e.getMessage());
        }
    }

    @Override
    public void handleRestoreMessage(SepRestoreMessage message) {
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(message.getGroupId());
        log.info("Sending Restore Command to Load Group: " + pao.getPaoName());
        
        int eventId = zigbeeControlEventDao.findCurrentEventId(message.getGroupId());
        
        try {
            zigbeeWebService.sendSEPRestoreMessage(eventId, message);
            
            //If success return a control history message to dispatch.
            ControlHistoryMessage histMsg = buildControlHistoryMessageForRestore(message, new Instant());        
            dispatchConnection.queue(histMsg);
            
        } catch (DigiWebServiceException e) {
            log.error("Error in ZigBeeWebService: " + e.getMessage());
        }
    }
    
    private ControlHistoryMessage buildControlHistoryMessageForRestore(SepRestoreMessage message, Instant date) {
        ControlHistoryMessage chMessage = buildCommonControlHistoryMessage(0,message.getGroupId(),date);
        
        chMessage.setControlDuration(CONTROL_RESTORE_DURATION);
        chMessage.setReductionRatio(0);
        
        chMessage.setActiveRestore("M");
        chMessage.setReductionValue(0.0);

        return chMessage;
    }
    
    private ControlHistoryMessage buildControlHistoryMessage(int eventId, SepControlMessage message, Instant date) {
        ControlHistoryMessage chMessage = buildCommonControlHistoryMessage(eventId,message.getGroupId(),date);
        
        // For this message, duration is in seconds.
        chMessage.setControlDuration(message.getControlMinutes() * 60);
        
        int standardCycle = message.getStandardCyclePercent();
        int averageCycle = Math.abs(message.getAverageCyclePercent());
        
        //Standard Cycle is one of the two values above depending on:
        //if standard 0<x<=100 use it
        //if average -1 < x >= -100
        //else 100
        
        if (standardCycle > 0 && standardCycle <= 100) {
            chMessage.setReductionRatio(standardCycle);
        } else if (averageCycle > 0 && averageCycle <= 100) {
            chMessage.setReductionRatio(averageCycle);
        } else {
            chMessage.setReductionRatio(100);
        }
        
        //Shed = T, Restore = M, based on existing porter code
        if (message.getControlMinutes() > 0) {
            chMessage.setActiveRestore("T");
            chMessage.setRawState(1);
        }
        else {
            chMessage.setActiveRestore("M");
            chMessage.setRawState(0);
            chMessage.setControlDuration(CONTROL_RESTORE_DURATION);
        }
        
        chMessage.setReductionValue(0.0);
        
        return chMessage;
    }
    
    private ControlHistoryMessage buildCommonControlHistoryMessage(int eventId, int groupId, Instant date) {
        PaoIdentifier paoIdentifier = paoDao.getYukonPao(groupId).getPaoIdentifier();
        LitePoint point = attributeService.getPointForAttribute(paoIdentifier, BuiltInAttribute.LM_GROUP_STATUS);
        
        ControlHistoryMessage chMessage = new ControlHistoryMessage();
        
        chMessage.setPaoId(groupId);
        chMessage.setPointId(point.getLiteID());

        chMessage.setStartTime(date.toDate());
        chMessage.setControlType("Digi Control");
        chMessage.setControlPriority(0);
        chMessage.setAssociationId(eventId);
        
        return chMessage;
    }
    
    @Override
    public void handleAssociationMessage(int eventId, int controlHistoryId) {
        //Check if this is our Event;
        if (pendingEvents.contains(eventId)) {
            zigbeeControlEventDao.associateControlHistory(eventId,controlHistoryId);
            pendingEvents.remove(eventId);
        }
    }
    
    @Override
    public void handleSendTextMessage(ZigbeeTextMessage zigbeeTextMessage) {
        try {
            zigbeeWebService.sendTextMessage(zigbeeTextMessage);
        } catch (DigiWebServiceException e) {
            log.warn("caught exception in handleTextMessage", e);
        } catch (ZigbeeClusterLibraryException e) {
            log.warn("caught exception in handleTextMessage", e);
        }
    }

    @Override
    public void handleCancelTextMessage(CancelZigbeeText cancelZigbeeText) {
        try {
            zigbeeWebService.cancelTextMessage(cancelZigbeeText);
        } catch (DigiWebServiceException e) {
            log.warn("caught exception in handleTextMessage", e);
        } catch (ZigbeeClusterLibraryException e) {
            log.warn("caught exception in handleTextMessage", e);
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
    public void setZigbeeWebService(ZigbeeWebService zigbeeWebService) {
        this.zigbeeWebService = zigbeeWebService;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setZigbeeControlEventDao(ZigbeeControlEventDao zigbeeControlEventDao) {
        this.zigbeeControlEventDao = zigbeeControlEventDao;
    }
    
    @Autowired
    public void setZigbeeDeviceDao(ZigbeeDeviceDao zigbeeDeviceDao) {
        this.zigbeeDeviceDao = zigbeeDeviceDao;
    }
}
