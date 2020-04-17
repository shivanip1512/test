package com.cannontech.services.systemDataPublisher.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public void publish(CloudDataConfigurations cloudDataConfiguration) {
        jmsTemplate.convertAndSend(cloudDataConfiguration);
    }

    @PostConstruct
    public void init() {
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.CLOUD_DATA_CONFIGURATIONS);
    }

}
