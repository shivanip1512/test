package com.cannontech.thirdparty.digi;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.thirdparty.messaging.ControlHistoryMessage;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.service.SepControlMessageHandler;
import com.cannontech.yukon.IServerConnection;

public class DigiControlMessageHandler implements SepControlMessageHandler {

    private IServerConnection dispatchConnection;
    private AttributeService attributeService;
    private DynamicDataSource dynamicDataSource;
    
    @Override
    public boolean handlePao(PaoIdentifier paoIdentifier) {
        
        if (paoIdentifier.getPaoType() == PaoType.LM_GROUP_DIGI_SEP) {
            return true;
        }
        
        return false;
    }

    //TODO This will be completed in YUK-9536 and YUK-9583
    @Override
    public void handleControlMessage(PaoIdentifier paoIdentifier, SepControlMessage message) {

        
        //Analyze message to decide what to do with it.
        
        //Do what needs to be done
        
        //If success return a control history message to dispatch.
        ControlHistoryMessage chMessage = buildControlHistoryMessage(paoIdentifier,message);
        
        dispatchConnection.queue(chMessage);
        
        //??? Return a failed message to dispatch?
        
        return;
    }
    
    /**
     * This is mostly using default values for now, just to get a response to dispatch.
     * @param message
     * @return
     */
    private ControlHistoryMessage buildControlHistoryMessage(PaoIdentifier paoIdentifier, SepControlMessage message) {
        ControlHistoryMessage chMessage = new ControlHistoryMessage();
        
        chMessage.setPaoId(message.getGroupId());
        
        //Hax
        final PaoIdentifier paoIdentifier2 = paoIdentifier;
        YukonPao pao = new YukonPao() {

            @Override
            public PaoIdentifier getPaoIdentifier() {
                return paoIdentifier2;
            }
            
        };
        
        LitePoint point = attributeService.getPointForAttribute(pao, BuiltInAttribute.LM_GROUP_STATUS);
        PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(point.getLiteID());
        
        //Get status point to set these.
        chMessage.setPointId(point.getLiteID());
        
        //Casting to int......? !?
        chMessage.setRawState((int)pointValue.getValue());
        
        //This needs to be the response time from Digi
        chMessage.setStartTime(new Date());
        
        //Where are these from? SEP message?
        chMessage.setControlDuration(message.getControlMinutes());
        chMessage.setReductionRatio(100);
        
        //Figure this out too
        chMessage.setControlType("N/A");
        
        //Distinguish between activate or restore  "Y" or "N"
        if (message.getControlMinutes() > 0) {
            chMessage.setActiveRestore("Y");
        }
        else {
            chMessage.setActiveRestore("N");
        }
        
        chMessage.setReductionValue(0.0);
        
        chMessage.setControlPriority(0);
        
        return chMessage;
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
}
