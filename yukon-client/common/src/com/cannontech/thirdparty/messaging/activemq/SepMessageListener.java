package com.cannontech.thirdparty.messaging.activemq;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.StreamMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.service.SepMessageHandler;
import com.google.common.collect.ImmutableList;

public class SepMessageListener {

    private PaoDao paoDao;
    
    private static final Logger logger = YukonLogManager.getLogger(SepMessageListener.class);
    
    private ImmutableList<SepMessageHandler> sepMessageHandlers;

    public void handleControlMessage(Message message) {
        
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
            for (SepMessageHandler handler : sepMessageHandlers) {
                PaoIdentifier paoIdentifier = paoDao.getYukonPao(sepMessage.getGroupId()).getPaoIdentifier();
                
                if(handler.handlePao(paoIdentifier)) {
                    handler.handleControlMessage(paoIdentifier,sepMessage);
                }
            }
        }
    }
    
    private SepControlMessage buildControlMessage(StreamMessage message) throws JMSException {
        SepControlMessage sep = new SepControlMessage();
        
        int groupId  = message.readInt();
        long utcStartTime = message.readLong();
        int controlMinutes = message.readInt();
        int criticality = message.readShort();
        int coolTempOffset = message.readShort();
        int heatTempOffset = message.readShort();
        int coolTempSetpoint = message.readShort();
        int heatTempSetpoint = message.readShort();
        int averageCyclePercent = message.readByte();
        int standardCyclePercent = message.readShort();
        int eventFlags = message.readShort();
        
        
        logger.debug(sep.toString());
        
        sep.setGroupId(groupId);
        sep.setUtcStartTime(utcStartTime);
        sep.setControlMinutes(controlMinutes);
        sep.setCriticality(criticality);
        sep.setCoolTempOffset(coolTempOffset);
        sep.setHeatTempOffset(heatTempOffset);
        sep.setCoolTempSetpoint(coolTempSetpoint);
        sep.setHeatTempSetpoint(heatTempSetpoint);
        sep.setAverageCyclePercent(averageCyclePercent);
        sep.setStandardCyclePercent(standardCyclePercent);
        sep.setEventFlags(eventFlags);
        
        return sep;
    }
    
    public void handleRestoreMessage(Message message) {
        
        logger.debug("Received message on *.dr.SmartEnergyProfileRestoreMessage Queue");
        
        if (message instanceof StreamMessage) {
            SepRestoreMessage sepMessage;
            
            //Build the ControlMessage.
            try {
                logger.debug("Received StreamMessage on *.dr.SmartEnergyProfileRestoreMessage Queue");
                sepMessage = buildRestoreMessage((StreamMessage)message);
            } catch (JMSException e) {
                logger.error("Exception parsing StreamMessage.");
                return;
            }
            
            //Passes the message to the handlers that handle this LM Group Type
            for (SepMessageHandler handler : sepMessageHandlers) {
                PaoIdentifier paoIdentifier = paoDao.getYukonPao(sepMessage.getGroupId()).getPaoIdentifier();
                
                if(handler.handlePao(paoIdentifier)) {
                    handler.handleRestoreMessage(paoIdentifier,sepMessage);
                }
            }
        }
    }
    
    private SepRestoreMessage buildRestoreMessage(StreamMessage message) throws JMSException {
        
        int groupId = message.readInt();
        int restoreTime = message.readInt();
        byte eventFlags = message.readByte();
        
        SepRestoreMessage restore = new SepRestoreMessage();

        restore.setGroupId(groupId);
        restore.setRestoreTime(restoreTime);
        restore.setEventFlags(eventFlags);
        
        return restore;
    }
    
    public void handleControlHistoryAssociation(Message message) {
        logger.debug("Received message on *.dr.HistoryRowAssociationResponse Queue");
        if (message instanceof StreamMessage) {
            try {
                StreamMessage streamMessage = (StreamMessage) message;
                int controlHistoryId = streamMessage.readInt();
                int eventId = streamMessage.readInt();

                //Passes the message to the handlers
                for (SepMessageHandler handler : sepMessageHandlers) {
                    handler.handleAssociationMessage(eventId, controlHistoryId);
                }
                
            } catch (JMSException e) {
                logger.error("Exception parsing StreamMessage.");
                return;
            }
        }
    }
    
    @Autowired
    public void setSepMessageHandlers(List<SepMessageHandler> sepMessageHandlers) {
        this.sepMessageHandlers = ImmutableList.copyOf(sepMessageHandlers);
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}
