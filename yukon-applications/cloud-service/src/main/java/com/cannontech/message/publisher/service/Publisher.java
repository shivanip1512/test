package com.cannontech.message.publisher.service;

import com.cannontech.message.model.SupportedDataType;

/**
 * Publisher to publish data on Message Broker
 */
public interface Publisher {
    
    /**
     * Returns what type of data the publisher supports
     */
    public abstract SupportedDataType getSupportedDataType();

    /**
     * Finds which publisher can handle type of data.
     */
    public abstract void requestData(SupportedDataType dataType);

    /**
     * Will create and publish message on Message Broker
     */
    public abstract void publishMessage();
}
