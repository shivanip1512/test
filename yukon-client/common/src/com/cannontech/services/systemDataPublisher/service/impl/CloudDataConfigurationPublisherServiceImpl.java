package com.cannontech.services.systemDataPublisher.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.services.systemDataPublisher.service.CloudDataConfigurationPublisherService;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

@Service
public class CloudDataConfigurationPublisherServiceImpl implements CloudDataConfigurationPublisherService {
    @Autowired private YukonJmsTemplate jmsTemplate;

    @Override
    public void publish(CloudDataConfiguration cloudDataConfiguration) {
        jmsTemplate.convertAndSend(JmsApiDirectory.CLOUD_DATA_CONFIGURATION, cloudDataConfiguration);
    }

}
