package com.cannontech.web.infrastructure.service.impl;

import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.infrastructure.model.InfrastructureWarningsRequest;
import com.cannontech.web.infrastructure.service.InfrastructureWarningsRefreshService;

public class InfrastructureWarningsRefreshServiceImpl implements InfrastructureWarningsRefreshService {
    private static final Logger log = YukonLogManager.getLogger(InfrastructureWarningsRefreshServiceImpl.class);
    private static final String queueName = "yukon.notif.obj.infrastructure.InfrastructureWarningsRequest";
    private JmsTemplate jmsTemplate;
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setDeliveryPersistent(false);
    }
    
    @Override
    public void initiateRecalculation() {
        log.info("Manually initiating a recalculation of infrastructure warning.s");
        jmsTemplate.convertAndSend(queueName, new InfrastructureWarningsRequest());
    }
}
