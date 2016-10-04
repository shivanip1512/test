package com.cannontech.dr.service.impl;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.dr.message.ControlHistoryMessage;
import com.cannontech.dr.service.ControlHistoryService;
import com.cannontech.dr.service.ControlType;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.IServerConnection;

public class ControlHistoryServiceImpl implements ControlHistoryService {
    @Autowired private AttributeService attributeService;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private IServerConnection dispatchConnection;
     
    @Override
    public void sendControlHistoryShedMessage(int groupId, Instant startTime, ControlType controlType,
                                              Integer associationId, int controlDurationSeconds, int reductionRatio) {
        
        ControlHistoryMessage controlHistoryMessage = buildBaseControlHistoryMessage(groupId, startTime, controlType,
                                                                                     associationId);
        controlHistoryMessage.setReductionRatio(reductionRatio);
        controlHistoryMessage.setReductionValue(0.0);
        
        if (controlDurationSeconds > 0) {
            controlHistoryMessage.setActiveRestore("T");
            controlHistoryMessage.setRawState(1);
            controlHistoryMessage.setControlDuration(controlDurationSeconds);
        } else {
            controlHistoryMessage.setActiveRestore("M");
            controlHistoryMessage.setRawState(0);
            controlHistoryMessage.setControlDuration(ControlHistoryMessage.CONTROL_RESTORE_DURATION);
        }
        
        if (controlType == ControlType.ECOBEE) {
            int cyclePercent = 100 - reductionRatio;
            controlHistoryMessage.setControlType(controlType.toString() + " " + cyclePercent + "%");
        } else if (controlType == ControlType.DIGI) {
            controlHistoryMessage.setControlType(controlType.toString() + " " + reductionRatio + "%");
        } else if (controlType == ControlType.HONEYWELLWIFI) {
            int cyclePercent = 100 - reductionRatio;
            controlHistoryMessage.setControlType(controlType.toString() + " " + cyclePercent + "%");
        } else {
            controlHistoryMessage.setControlType(controlType.toString());
        }

        dispatchConnection.queue(controlHistoryMessage);
    }
    
    @Override
    public void sendControlHistoryRestoreMessage(int groupId, Instant time) {
        ControlHistoryMessage controlHistoryMessage = 
                buildBaseControlHistoryMessage(groupId, time, ControlType.TERMINATE, null);
        
        controlHistoryMessage.setControlDuration(ControlHistoryMessage.CONTROL_RESTORE_DURATION);
        controlHistoryMessage.setReductionRatio(0);
        controlHistoryMessage.setActiveRestore("M");
        controlHistoryMessage.setReductionValue(0.0);
        controlHistoryMessage.setRawState(0);
        
        dispatchConnection.queue(controlHistoryMessage);
    }
    
    private ControlHistoryMessage buildBaseControlHistoryMessage(int groupId, Instant time, ControlType controlType,
                                                                 Integer associationId) {
        
        PaoIdentifier paoIdentifier = databaseCache.getAllPaosMap().get(groupId).getPaoIdentifier();
        LitePoint point = attributeService.getPointForAttribute(paoIdentifier, BuiltInAttribute.LM_GROUP_STATUS);
        
        ControlHistoryMessage controlHistoryMessage = new ControlHistoryMessage();
        controlHistoryMessage.setPaoId(groupId);
        controlHistoryMessage.setPointId(point.getLiteID());
        controlHistoryMessage.setStartTime(time.toDate());
        controlHistoryMessage.setControlPriority(0);
        controlHistoryMessage.setControlType(controlType.toString());
        
        if (associationId == null) {
            controlHistoryMessage.setAssociationId(0);
        } else {
            controlHistoryMessage.setAssociationId(associationId);
        }
        
        return controlHistoryMessage;
    }
}
