package com.cannontech.configurationSettingPublisher.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.configurationSettingPublisher.service.ConfigurationSettingPublisherService;
import com.cannontech.services.configurationSettingMessage.model.ConfigurationSettings;

public class ConfigurationSettingPublisherServiceImpl implements ConfigurationSettingPublisherService {

    @Autowired private YukonJmsTemplate jmsTemplate;

    @Override
    public void publish(ConfigurationSettings configurationSetting) {
        jmsTemplate.convertAndSend(JmsApiDirectory.CLOUD_CONFIGURATION_SETTINGS, configurationSetting,
                jacksonJmsMessageConverter());
    }

    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        return converter;
    }
}
