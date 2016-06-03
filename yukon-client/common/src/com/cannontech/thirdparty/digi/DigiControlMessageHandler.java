package com.cannontech.thirdparty.digi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.service.ControlHistoryService;
import com.cannontech.dr.service.ControlType;
import com.cannontech.thirdparty.digi.dao.ZigbeeControlEventDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.exception.DigiNotConfiguredException;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.exception.ZigbeeClusterLibraryException;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.service.SepMessageHandler;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.IServerConnection;

public class DigiControlMessageHandler implements SepMessageHandler {

    private static final Logger log = YukonLogManager.getLogger(DigiControlMessageHandler.class);

    @Autowired private IDatabaseCache databaseCache;
    @Autowired private IServerConnection dispatchConnection;
    @Autowired private AttributeService attributeService;
    @Autowired private ZigbeeWebService zigbeeWebService;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private ZigbeeDeviceDao zigbeeDeviceDao;
    @Autowired private ZigbeeControlEventDao zigbeeControlEventDao;
    @Autowired private ControlHistoryService controlHistoryService;
    
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
        LiteYukonPAObject pao = databaseCache.getAllPaosMap().get(message.getGroupId());
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
            int controlDurationSeconds = message.getControlMinutes() * 60;
            int reductionRatio = getReductionRatio(message);
            controlHistoryService.sendControlHistoryShedMessage(message.getGroupId(), now, ControlType.DIGI, eventId, 
                                                                controlDurationSeconds, reductionRatio);

        } catch (DigiNotConfiguredException e) {
            log.error("Error in ZigBeeWebService: " + e.getMessage());
        } catch (DigiWebServiceException e) {
            log.error("Error in ZigBeeWebService: " + e.getMessage());
        }
    }

    @Override
    public void handleRestoreMessage(SepRestoreMessage message) {
        LiteYukonPAObject pao = databaseCache.getAllPaosMap().get(message.getGroupId());
        log.info("Sending Restore Command to Load Group: " + pao.getPaoName());
        
        int eventId = zigbeeControlEventDao.findCurrentEventId(message.getGroupId());
        
        try {
            zigbeeWebService.sendSEPRestoreMessage(eventId, message);
            
            //If success return a control history message to dispatch.
            controlHistoryService.sendControlHistoryRestoreMessage(message.getGroupId(), Instant.now());
            
        } catch (DigiNotConfiguredException e) {
            log.error("Error in ZigBeeWebService: " + e.getMessage());
        } catch (DigiWebServiceException e) {
            log.error("Error in ZigBeeWebService: " + e.getMessage());
        }
    }
    
    private int getReductionRatio(SepControlMessage message) {
        int standardCycle = message.getStandardCyclePercent();
        int averageCycle = Math.abs(message.getAverageCyclePercent());
        
        //Standard Cycle is one of the two values above depending on:
        //if standard 0<x<=100 use it
        //if average -1 < x >= -100
        //else 100
        
        if (standardCycle > 0 && standardCycle <= 100) {
            return standardCycle;
        } else if (averageCycle > 0 && averageCycle <= 100) {
            return averageCycle;
        } else {
            return 100;
        }
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
    public void handleSendTextMessage(YukonTextMessage yukonTextMessage) {
        try {
            zigbeeWebService.sendTextMessage(yukonTextMessage);
        } catch (DigiNotConfiguredException e) {
            log.warn("caut exception in handlTextMessage", e);
        } catch (DigiWebServiceException e) {
            log.warn("caught exception in handleTextMessage", e);
        } catch (ZigbeeClusterLibraryException e) {
            log.warn("caught exception in handleTextMessage", e);
        }
    }

    @Override
    public void handleCancelTextMessage(YukonCancelTextMessage cancelZigbeeText) {
        try {
            zigbeeWebService.cancelTextMessage(cancelZigbeeText);
        } catch (DigiNotConfiguredException e){
            log.error("Digi not configured", e);
        } catch (DigiWebServiceException e) {
            log.warn("caught exception in handleTextMessage", e);
        } catch (ZigbeeClusterLibraryException e) {
            log.warn("caught exception in handleTextMessage", e);
        }
    }
}
