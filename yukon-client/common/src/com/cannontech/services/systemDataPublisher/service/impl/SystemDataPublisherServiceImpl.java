package com.cannontech.services.systemDataPublisher.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.stereotype.Service;

import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.services.systemDataPublisher.service.SystemDataPublisherService;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;

@Service
public class SystemDataPublisherServiceImpl implements SystemDataPublisherService {
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private YukonJmsTemplate jmsTemplate;
    private MappingJackson2MessageConverter converter;

    @Override
    public void publish(SystemData systemData) {
        jmsTemplate.convertAndSend(systemData);
    }

    @PostConstruct
    public void initialize() {
        converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.SYSTEM_DATA, converter);
    }
}
