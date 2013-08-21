package com.cannontech.messaging.serialization.thrift.test;

import org.junit.Test;

public class CommonMessagesAutoSerializationTest extends MessageAutoSerializationTestBase {

    @Override
    protected String[] getContextUri() {
        return new String[] { "com/cannontech/messaging/thriftConnectionContext.xml",
                             "com/cannontech/messaging/serialization/thrift/test/messagevalidator/messageValidatorContext.xml" };
    } 
    
    @Test
    public void testAllCommonMessage() {
        testAllMessages();
    }
}
