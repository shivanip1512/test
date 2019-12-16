package com.cannontech.message.publisher.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.message.model.SupportedDataType;
import com.cannontech.message.publisher.service.impl.ConfigurationDataPublisher;

/**
 * Base class for publisher.
 */
public abstract class PublisherBase implements Publisher {

    Logger log = (Logger) LogManager.getLogger(PublisherBase.class);
    List<Publisher> publishers;

    @Override
    public void requestData(SupportedDataType dataType) {
        // Find which publisher support this type of dataand call publishMessage();

    }

    @Override
    public abstract SupportedDataType getSupportedDataType();
}
