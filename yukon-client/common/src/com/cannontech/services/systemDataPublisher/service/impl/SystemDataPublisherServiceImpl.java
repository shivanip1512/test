package com.cannontech.services.systemDataPublisher.service.impl;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.services.systemDataPublisher.service.SystemDataPublisherService;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;

@Service
public class SystemDataPublisherServiceImpl implements SystemDataPublisherService {
    private JmsTemplate jmsTemplate;

    @Override
    public void publish(SystemData systemData) {
        jmsTemplate.convertAndSend(JmsApiDirectory.SYSTEM_DATA.getQueue().getName(), systemData);
    }

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
        jmsTemplate.setPubSubDomain(true);
    }
}
