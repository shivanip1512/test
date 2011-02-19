package com.cannontech.services.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.log4j.MDC;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

public class ContextLoggingMessageListenerContainer extends SimpleMessageListenerContainer {
    private static final String JMS_MESSAGE_ID = "JmsMessageId";

    @Override
    protected void invokeListener(Session session, Message message) throws JMSException {
        MDC.put(JMS_MESSAGE_ID, message.getJMSMessageID());
        try {
            super.invokeListener(session, message);
        } finally {
            MDC.remove(JMS_MESSAGE_ID);
        }
    }
}
