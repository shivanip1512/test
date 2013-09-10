package com.cannontech.messaging.serialization.thrift.test.autotest;

import org.junit.Test;

import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;

public class MacsMessagesAutoSerializationTest  extends MessageAutoSerializationTestBase {

    @Override
    protected ThriftMessageFactory createMessageFactory() {
        return appContext.getBean("messaging.thrift.MacMessageFactory", ThriftMessageFactory.class);
    }

    @Test
    public void testMacsMessages() {        
        autoTestMessage(com.cannontech.message.macs.message.AddSchedule.class);
        autoTestMessage(com.cannontech.message.macs.message.DeleteSchedule.class);
        autoTestMessage(com.cannontech.message.macs.message.Info.class);
        autoTestMessage(com.cannontech.message.macs.message.OverrideRequest.class);
        autoTestMessage(com.cannontech.message.macs.message.RetrieveSchedule.class);
        autoTestMessage(com.cannontech.message.macs.message.RetrieveScript.class);
        autoTestMessage(com.cannontech.message.macs.message.Schedule.class);
        autoTestMessage(com.cannontech.message.macs.message.ScriptFile.class);
        autoTestMessage(com.cannontech.message.macs.message.UpdateSchedule.class);
    }
}
