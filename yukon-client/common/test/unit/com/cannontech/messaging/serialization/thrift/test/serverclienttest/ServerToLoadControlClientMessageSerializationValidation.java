package com.cannontech.messaging.serialization.thrift.test.serverclienttest;

import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;


public class ServerToLoadControlClientMessageSerializationValidation extends
    ServerToClientMessageSerializationTestBase {

    @Override
    protected String[] getContextUri() {
        return new String[] { "com/cannontech/messaging/testThriftContext.xml",
                             "com/cannontech/messaging/serialization/thrift/test/messagevalidator/messageValidatorContext.xml" };
    }
    
    @Override
    protected ThriftMessageFactory createMessageFactory() {
        return appContext.getBean("messaging.thrift.LoadcontrolMessageFactory", ThriftMessageFactory.class);
    }
}
