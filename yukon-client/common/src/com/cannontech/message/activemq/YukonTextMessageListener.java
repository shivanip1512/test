package com.cannontech.message.activemq;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.service.impl.PorterExpressComCommandStrategy;

public class YukonTextMessageListener {
    
    private static final Logger log = YukonLogManager.getLogger(YukonTextMessageListener.class);
    
    @Autowired private InventoryDao inventoryDao;
    @Autowired private PorterExpressComCommandStrategy porterExpressComCommandStrategy;
    
    //@Autowired by setter
    private JmsTemplate jmsTemplate;
    
    public void handleYukonTextMessage(YukonTextMessage yukonTextMessage) {
        log.info("Received message on yukon.notif.stream.dr.YukonTextMessage.Send Queue");
        
        Map<Integer, HardwareSummary> hardwareSummary = inventoryDao.findHardwareSummariesById(yukonTextMessage.getInventoryIds());
        Map<HardwareType, Set<Integer>> hardwareTypeToInventoryIds = getHardwareTypeToInventoryIdsMap(yukonTextMessage.getInventoryIds(), hardwareSummary);
        
        for (HardwareType hardwareType : hardwareTypeToInventoryIds.keySet()) {
            yukonTextMessage.setInventoryIds(hardwareTypeToInventoryIds.get(hardwareType));
            // Sending text messages are only supported by Utility Pro thermostats
            if (hardwareType.isZigbee() && hardwareType.isSupportsTextMessages()) {
                jmsTemplate.convertAndSend("yukon.notif.stream.dr.smartEnergyProfileTextMessage.Send", yukonTextMessage);
            } else if (hardwareType.isExpressCom() && hardwareType.isSupportsTextMessages()) {
                porterExpressComCommandStrategy.sendTextMessage(yukonTextMessage, hardwareSummary);
            } else {
                log.error("Send Text Message is not supported by hardware config type "
                          + hardwareType.getHardwareConfigType() + " for " + yukonTextMessage.getInventoryIds().size()
                          + " devices.");
            }
        }
    }
    
     
    public void handleYukonCancelTextMessage(YukonCancelTextMessage yukonTextMessage) {
        log.debug("Received message on yukon.notif.stream.dr.YukonTextMessage.Cancel Queue");
        
        Map<Integer, HardwareSummary> hardwareSummary = inventoryDao.findHardwareSummariesById(yukonTextMessage.getInventoryIds());
        Map<HardwareType, Set<Integer>> hardwareTypeToInventoryIds = getHardwareTypeToInventoryIdsMap(yukonTextMessage.getInventoryIds(), hardwareSummary);
        
        for (HardwareType hardwareType : hardwareTypeToInventoryIds.keySet()) {
            yukonTextMessage.setInventoryIds(hardwareTypeToInventoryIds.get(hardwareType));
            // Canceling sending text messages are only supported by Zigbee
            if (hardwareType.isZigbee() && hardwareType.isSupportsTextMessages()) {
                jmsTemplate.convertAndSend("yukon.notif.stream.dr.smartEnergyProfileTextMessage.Cancel", yukonTextMessage);
            } else {
                log.error("Canceling Send Text Message is not supported by hardware config type "
                          + hardwareType.getHardwareConfigType() + " for " + yukonTextMessage.getInventoryIds().size()
                          + " devices.");
            }
        }
        jmsTemplate.convertAndSend("yukon.notif.stream.dr.smartEnergyProfileTextMessage.Cancel", yukonTextMessage);
    }
    
    private Map<HardwareType, Set<Integer>> getHardwareTypeToInventoryIdsMap( Set<Integer> inventoryIds,  Map<Integer, HardwareSummary> hardwareSummary){
        Map<HardwareType, Set<Integer>> hardwareTypeToInventoryIds = new EnumMap<HardwareType, Set<Integer>>(HardwareType.class);
        for (int inventoryId : inventoryIds) {
            HardwareType type = hardwareSummary.get(inventoryId).getHardwareType();
            
            if(!hardwareTypeToInventoryIds.containsKey(type)){
                hardwareTypeToInventoryIds.put(type, new HashSet<Integer>());
            }
            hardwareTypeToInventoryIds.get(type).add(inventoryId);
        }
        return hardwareTypeToInventoryIds;
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(false);
    }
}
