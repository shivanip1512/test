package com.cannontech.message.publisher.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.message.model.SupportedDataType;

/**
 * Base class for publisher.
 */
public abstract class PublisherBase implements Publisher {

    Logger log = (Logger) LogManager.getLogger(PublisherBase.class);
    List<Publisher> publishers;

    @Override
    public void requestData(SupportedDataType dataType) {
        // Find which publisher support this type of data
        // call publishData()
        log.info("In request data, finding publisher");
        publishers.forEach(publisher -> {
            if (publisher.getSupportedDataType() == dataType) {
                publisher.publishMessage();
            }
        });
    }

    @Override
    public abstract SupportedDataType getSupportedDataType();

    @Autowired
    void setPublishers(List<Publisher> publishers) {
        this.publishers = publishers;
    }

}
