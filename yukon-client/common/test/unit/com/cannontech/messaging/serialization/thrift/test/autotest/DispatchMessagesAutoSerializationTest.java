package com.cannontech.messaging.serialization.thrift.test.autotest;

import org.junit.Test;

import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;

public class DispatchMessagesAutoSerializationTest extends MessageAutoSerializationTestBase {

    @Override
    protected ThriftMessageFactory createMessageFactory() {
        return appContext.getBean("messaging.thrift.DispatchMessageFactory", ThriftMessageFactory.class);
    }

    @Test
    public void testDispatchMessages() {
        autoTestMessage(com.cannontech.message.dispatch.message.Multi.class);
        autoTestMessage(com.cannontech.message.dispatch.message.DBChangeMsg.class);
        autoTestMessage(com.cannontech.message.dispatch.message.PointData.class);
        autoTestMessage(com.cannontech.message.dispatch.message.PointRegistration.class);
        autoTestMessage(com.cannontech.message.dispatch.message.Registration.class);
        autoTestMessage(com.cannontech.message.dispatch.message.Signal.class);
        autoTestMessage(com.cannontech.message.dispatch.message.TagMsg.class);

        // Needed by Dispatch event if defined in thirdparty package
        autoTestMessage(com.cannontech.dr.message.ControlHistoryMessage.class);
    }
}
