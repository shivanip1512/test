package com.cannontech.messaging.serialization.thrift.test.autotest;

import org.junit.Test;

import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;

public class PorterMessagesAutoSerializationTest  extends MessageAutoSerializationTestBase {

    @Override
    protected ThriftMessageFactory createMessageFactory() {
        return appContext.getBean("messaging.thrift.PorterMessageFactory", ThriftMessageFactory.class);
    }

    @Test
    public void testPorterMessages() {
        autoTestMessage(com.cannontech.message.porter.message.QueueData.class);
        autoTestMessage(com.cannontech.message.porter.message.Request.class);
        autoTestMessage(com.cannontech.message.porter.message.RequestCancel.class);
        autoTestMessage(com.cannontech.message.porter.message.Return.class); 
        autoTestMessage(com.cannontech.message.dispatch.message.PointData.class);
        autoTestMessage(com.cannontech.message.dispatch.message.Signal.class);
    }
}
