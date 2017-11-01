package com.cannontech.messaging.util.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.messaging.serialization.MessageFactory;
import com.cannontech.services.jms.InternalMessagingConnectionFactory;

public class AmqConnectionFactoryBase {
    protected static final String AMQ_CONNECTION_DEFAULT_QUEUE_NAME_PREFIX = "com.eaton.eas.yukon.";

    @Autowired private InternalMessagingConnectionFactory connectionFactory;
    private MessageFactory messageFactory;
    private String queuePrefixName;
    private String queueName;

    protected AmqConnectionFactoryBase(MessageFactory messageFactory, String queueName) {
        this(messageFactory, AMQ_CONNECTION_DEFAULT_QUEUE_NAME_PREFIX, queueName);
    }

    protected AmqConnectionFactoryBase(MessageFactory messageFactory, String queueNamePrefix, String queueName) {
        this.messageFactory = messageFactory;
        this.queueName = queueName;
        this.queuePrefixName = queueNamePrefix;
    }

    protected String getFullQueueName() {
        if (getQueuePrefixName() == null) {
            return AMQ_CONNECTION_DEFAULT_QUEUE_NAME_PREFIX + getQueueName();
        }
        else {
            return getQueuePrefixName() + getQueueName();
        }
    }

    public InternalMessagingConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    public void setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    public String getQueuePrefixName() {
        return queuePrefixName;
    }

    public void setQueuePrefixName(String queuePrefixName) {
        this.queuePrefixName = queuePrefixName;
    }

    protected String getQueueName() {
        return queueName;
    }

    protected void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
