package com.cannontech.common.util.jms;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

public class JmsHelper {
    public static <T> T extractObject(Message msg, Class<T> expectedClass) throws JMSException {
        ObjectMessage objMessage = (ObjectMessage) msg;
        Serializable object = objMessage.getObject();
        T result = expectedClass.cast(object);
        return result;
    }
}
