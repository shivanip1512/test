package com.cannontech.message.activemq;

import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;

public class YukonTextMessageListener {
    
    private static final Logger log = YukonLogManager.getLogger(YukonTextMessageListener.class);
    
    //@Autowired by setter
    private JmsTemplate jmsTemplate;
    
    public void handleYukonTextMessage(YukonTextMessage yukonTextMessage) {
        log.info("Received message on yukon.notif.stream.dr.YukonTextMessage.Send Queue");
        //Decide if this is expresscom or SEP
        
        //If SEP:
        jmsTemplate.convertAndSend("yukon.notif.stream.dr.smartEnergyProfileTextMessage.Send", yukonTextMessage);
    }
     
    public void handleYukonCancelTextMessage(YukonCancelTextMessage yukonTextMessage) {
        log.debug("Received message on yukon.notif.stream.dr.YukonTextMessage.Cancel Queue");
        //Decide if this is expresscom or SEP
        
        //If SEP:
        jmsTemplate.convertAndSend("yukon.notif.stream.dr.smartEnergyProfileTextMessage.Cancel", yukonTextMessage);
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(false);
    }
}
