package com.cannontech.services.dictionariesField.publisher.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.services.dictionariesField.publisher.service.DictionariesFieldPublisherService;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;

@Service
public class DictionariesFieldPublisherServiceImpl implements DictionariesFieldPublisherService {
    @Autowired private YukonJmsTemplate jmsTemplate;

    @Override
    public void publish(DictionariesField dictionariesField) {
        jmsTemplate.convertAndSend(JmsApiDirectory.DICTIONARY_DATA, dictionariesField);
    }

}
