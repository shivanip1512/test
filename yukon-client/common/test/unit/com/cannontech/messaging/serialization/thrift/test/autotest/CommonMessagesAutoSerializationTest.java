package com.cannontech.messaging.serialization.thrift.test.autotest;

import org.junit.jupiter.api.Test;

import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;

public class CommonMessagesAutoSerializationTest extends MessageAutoSerializationTestBase {

    @Override
    protected ThriftMessageFactory createMessageFactory() {
        return appContext.getBean("messaging.thrift.CommonMessageFactory", ThriftMessageFactory.class);
    }

    @Test
    public void testCommonMessages() {
        autoTestMessage(com.cannontech.message.util.Message.class);
        autoTestMessage(com.cannontech.message.util.Ping.class);
        autoTestMessage(com.cannontech.message.util.CollectableBoolean.class);
        autoTestMessage(com.cannontech.message.util.Command.class);
        autoTestMessage(com.cannontech.message.dispatch.message.Multi.class);
        autoTestMessage(com.cannontech.message.server.ServerRequestMsg.class);
        autoTestMessage(com.cannontech.message.server.ServerResponseMsg.class);
    }
}
