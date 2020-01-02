package com.cannontech.configurationSettingPublisher.service.impl;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.configurationSettingPublisher.service.ConfigurationSettingPublisherService;
import com.cannontech.services.configurationSettingMessage.model.ConfigurationSettings;

public class ConfigurationSettingPublisherServiceImpl implements ConfigurationSettingPublisherService {

    private JmsTemplate jmsTemplate;

    @Override
    public void publish(ConfigurationSettings configurationSetting) {
        jmsTemplate.convertAndSend(JmsApiDirectory.CLOUD_CONFIGURATION_SETTINGS.getQueue().getName(), configurationSetting);
    }

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
        jmsTemplate.setMessageConverter(jacksonJmsMessageConverter());
        jmsTemplate.setPubSubDomain(true);
    }

    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        return converter;
    }
}
