package com.cannontech.message.publisher.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.message.publisher.service.impl.ConfigurationDataPublisher;

/**
 * Base class for publisher.
 */
public abstract class PublisherBase implements Publisher {

    Logger log = (Logger) LogManager.getLogger(PublisherBase.class);
    @Autowired ConfigurationDataPublisher publisher;
    
    List<Publisher> publishers;

    @Override
    public void requestData(SupportedDataType dataType) {
        log.info("All publishers " + publishers);
        publisher.publishMessage();
    }

    @Override
    public abstract SupportedDataType getSupportedDataType();

    public List<Publisher> getPublishers() {
        return publishers;
    }
}
	