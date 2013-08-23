package com.cannontech.messaging.serialization.thrift.test.serverclienttest;

import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;

public class ServerToPorterMessageSerializationTest extends ServerToClientMessageSerializationTestBase {
    @Override
    protected ThriftMessageFactory createMessageFactory() {
        return appContext.getBean("messaging.thrift.PorterMessageFactory", ThriftMessageFactory.class);
    }
}
