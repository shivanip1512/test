package com.cannontech.messaging.serialization.thrift.test;


public class ServerToLoadControlClientMessageSerializationValidation extends
    ServerToClientMessageSerializationValidation {

    @Override
    protected String[] getContextUri() {
        return new String[] { "com/cannontech/messaging/thriftConnectionContext.xml",
                             "com/cannontech/messaging/thriftLoadcontrolConnectionContext.xml",
                             "com/cannontech/messaging/serialization/thrift/test/messagevalidator/messageValidatorContext.xml" };
    }
}
