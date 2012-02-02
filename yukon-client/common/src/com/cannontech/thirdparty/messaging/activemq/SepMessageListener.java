package com.cannontech.thirdparty.messaging.activemq;

import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.CancelZigbeeText;
import com.cannontech.common.model.ZigbeeTextMessage;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;
import com.cannontech.thirdparty.service.SepMessageHandler;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

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
                    handler.handleControlMessage(sepMessage);
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
        
        logger.debug(sep.toString());

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
                    handler.handleRestoreMessage(sepMessage);
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
        
        logger.debug(restore.toString());
        
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
    
    /**
     * Handle the sending of a text message to a gateway
     * 
     * @param zigbeeTextMessage
     */
    public void handleSendTextMessage(Message message) {
        logger.debug("Received message on yukon.notif.stream.dr.smartEnergyProfileTextMessage.Send Queue");

        ZigbeeTextMessage zigbeeTextMessage;
        
        if (message instanceof StreamMessage) {
            //This is how C++ will send us the message
            try {
                StreamMessage streamMessage = (StreamMessage) message;
                
                int listSize = streamMessage.readInt();
                Set<Integer> inventoryIds = Sets.newHashSet();
                
                for (int i = 0; i < listSize; i++) {
                    int inventoryId = streamMessage.readInt();
                    inventoryIds.add(inventoryId);
                }

                int messageId = streamMessage.readInt();
                int stringSize = streamMessage.readInt();
                
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < stringSize; i++) {
                    char character = streamMessage.readChar();
                    sb.append(character);
                }

                boolean confirmationRequired = streamMessage.readBoolean();
                int durationInt = streamMessage.readInt();
                Duration displayDuration = new Duration(durationInt);
                
                long timeSeconds = streamMessage.readLong();
                Instant startTime = new Instant(timeSeconds*1000);
                
                zigbeeTextMessage = new ZigbeeTextMessage();
                zigbeeTextMessage.setInventoryIds(inventoryIds);
                zigbeeTextMessage.setMessageId(messageId);
                zigbeeTextMessage.setMessage(sb.toString());
                zigbeeTextMessage.setConfirmationRequired(confirmationRequired);
                zigbeeTextMessage.setDisplayDuration(displayDuration);
                zigbeeTextMessage.setStartTime(startTime);
            } catch (JMSException e) {
                logger.error("Exception parsing StreamMessage.");
                return;
            }            
        } else {
            logger.error("Unhandled message type on the queue.");
            return;
        }

        handleSendTextMessage(zigbeeTextMessage);
    }
    
    public void handleSendTextMessage(ZigbeeTextMessage zigbeeTextMessage) {
        //Passes the message to the handlers
        for (SepMessageHandler handler : sepMessageHandlers) {
            handler.handleSendTextMessage(zigbeeTextMessage);
        }
    }
    
    /**
     * Handle the canceling of a text message event.
     * 
     * @param zigbeeTextMessage
     */
    public void handleCancelTextMessage(Message message) {
        logger.debug("Received message on yukon.notif.stream.dr.smartEnergyProfileTextMessage.Cancel Queue");
        
        CancelZigbeeText cancelZigbeeText;
        
        if (message instanceof StreamMessage) {
            //This is how C++ will send us the message
            try {
                StreamMessage streamMessage = (StreamMessage) message;
                
                int messageId = streamMessage.readInt();
                
                int listSize = streamMessage.readInt();
                Set<Integer> inventoryIds = Sets.newHashSet();
                
                for (int i = 0; i < listSize; i++) {
                    int inventoryId = streamMessage.readInt();
                    inventoryIds.add(inventoryId);
                }
                
                cancelZigbeeText = new CancelZigbeeText();
                cancelZigbeeText.setMessageId(messageId);
                cancelZigbeeText.setInventoryIds(inventoryIds);                
            } catch (JMSException e) {
                logger.error("Exception parsing StreamMessage.");
                return;
            }
        } else if (message instanceof ObjectMessage) {
            //This is how Java would send us the message.
            cancelZigbeeText = (CancelZigbeeText)message;
        } else {
            logger.error("Unhandled message type on the queue.");
            return;
        }

        handleCancelTextMessage(cancelZigbeeText);
    }
    
    public void handleCancelTextMessage(CancelZigbeeText cancelZigbeeText) {
        //Passes the message to the handlers
        for (SepMessageHandler handler : sepMessageHandlers) {
            handler.handleCancelTextMessage(cancelZigbeeText);
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
