package com.cannontech.services.systemDataPublisher.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.stereotype.Service;

import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.services.systemDataPublisher.service.CloudDataConfigurationsPublisherService;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfigurations;

@Service
public class CloudDataConfigurationsPublisherServiceImpl implements CloudDataConfigurationsPublisherService {
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    private YukonJmsTemplate jmsTemplate;
    private MappingJackson2MessageConverter converter;

    @Override
    public void publish(CloudDataConfigurations cloudDataConfigurations) {
        jmsTemplate.convertAndSend(cloudDataConfigurations);
    }

    @PostConstruct
    public void init() {
        converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.CLOUD_DATA_CONFIGURATIONS, converter);
    }

}
