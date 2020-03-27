package com.cannontech.services.systemDataPublisher.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.stereotype.Service;

import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.services.systemDataPublisher.service.SystemDataPublisherService;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;

@Service
public class SystemDataPublisherServiceImpl implements SystemDataPublisherService {
    @Autowired private YukonJmsTemplate jmsTemplate;

    @Override
    public void publish(SystemData systemData) {
        jmsTemplate.convertAndSend(JmsApiDirectory.SYSTEM_DATA, systemData, jacksonJmsMessageConverter());
    }

    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        return converter;
    }
}
