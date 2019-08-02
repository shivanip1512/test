package com.cannontech.services.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

public class ContextLoggingMessageListenerContainer extends SimpleMessageListenerContainer {
    private static final String JMS_MESSAGE_ID = "JmsMessageId";

    ContextLoggingMessageListenerContainer() {
        setAcceptMessagesWhileStopping(true);
    }
    
    @Override
    protected void invokeListener(Session session, Message message) throws JMSException {
        ThreadContext.put(JMS_MESSAGE_ID, message.getJMSMessageID());
        try {
            super.invokeListener(session, message);
        } finally {
            ThreadContext.remove(JMS_MESSAGE_ID);
        }
    }
}