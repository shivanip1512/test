package com.cannontech.thirdparty.messaging.activemq;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.StreamMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.service.SepControlMessageHandler;
import com.google.common.collect.ImmutableList;

public class SepControlMessageListener implements MessageListener {

    private PaoDao paoDao;
    
    private static final Log logger = LogFactory.getLog(SepControlMessageListener.class);
    
    private ImmutableList<SepControlMessageHandler> sepControlMessageHandlers;

    @Override
    public void onMessage(Message message) {
        
        logger.debug("Received message on *.dr.SmartEnergyProfileControlMessage Queue");
        
        if (message instanceof StreamMessage) {
            SepControlMessage sepMessage;
            
            //Build the ControlMessage.
            try {
                logger.debug("Received StreamMessage on *.dr.SmartEnergyProfileControlMessage Queue");
                sepMessage = buildControlMessage((StreamMessage)message);
            } catch (JMSException e) {
                logger.error("Exception parsing StreamMessage.");
                return;
            }
            
            //Passes the message to the handlers that handle this LM Group Type
            for (SepControlMessageHandler handler : sepControlMessageHandlers) {
                PaoIdentifier paoIdentifier = paoDao.getPaoIdentifierForPaoId(sepMessage.getGroupId());
                
                if(handler.handlePao(paoIdentifier)) {
                    handler.handleControlMessage(paoIdentifier,sepMessage);
                }
            }
        }
    }
    
    private SepControlMessage buildControlMessage(StreamMessage message) throws JMSException {
        SepControlMessage sep = new SepControlMessage();
        
        int groupId  = message.readInt();
        int utcStartTime = message.readInt();
        short controlMinutes = message.readShort();
        byte coolTempOffset = message.readByte();
        byte heatTempOffset = message.readByte();
        short coolTempSetpoint = message.readShort();
        short heatTempSetpoint = message.readShort();
        byte averageCyclePercent = message.readByte();
        byte standardCyclePercent = message.readByte();
        byte eventFlags = message.readByte();

        //TODO debugging.. remove this
        logger.info("Sep Message: " + "Group Id: " + groupId 
                                              + " utcStartTime: " + utcStartTime 
                                              + " controlMinutes: " + controlMinutes
                                              + " coolTempOffset: " + coolTempOffset
                                              + " heatTempOffset: " +heatTempOffset
                                              + " coolTempSetPoint: " + coolTempSetpoint
                                              + " heatTempSetPoint: " + heatTempSetpoint
                                              + " averageCyclePercent: " + averageCyclePercent
                                              + " standardCyclePercent: " + standardCyclePercent
                                              + " eventFlags: " + eventFlags
                                              );
        
        sep.setGroupId(groupId);
        sep.setUtcStartTime(utcStartTime);
        sep.setControlMinutes(controlMinutes);
        sep.setCoolTempOffset(coolTempOffset);
        sep.setHeatTempOffset(heatTempOffset);
        sep.setCoolTempSetpoint(coolTempSetpoint);
        sep.setHeatTempSetpoint(heatTempSetpoint);
        sep.setAverageCyclePercent(averageCyclePercent);
        sep.setStandardCyclePercent(standardCyclePercent);
        sep.setEventFlags(eventFlags);
        
        return sep;
    }
    
    @Autowired
    public void setSepControlMessageHandlers(List<SepControlMessageHandler> sepControlMessageHandlers) {
        this.sepControlMessageHandlers = ImmutableList.copyOf(sepControlMessageHandlers);
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}
