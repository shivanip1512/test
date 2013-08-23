package com.cannontech.messaging.serialization.thrift.test.autotest;

import org.junit.Test;

import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;

public class NotifMessagesAutoSerializationTest  extends MessageAutoSerializationTestBase {

    @Override
    protected ThriftMessageFactory createMessageFactory() {
        return appContext.getBean("messaging.thrift.NotifMessageFactory", ThriftMessageFactory.class);
    }

    @Test
    public void testNotifMessages() {               
        autoTestMessage(com.cannontech.message.notif.NotifAlarmMsg.class);
        autoTestMessage(com.cannontech.message.notif.CurtailmentEventDeleteMsg.class);
        autoTestMessage(com.cannontech.message.notif.CurtailmentEventMsg.class);               
        autoTestMessage(com.cannontech.message.notif.EconomicEventDeleteMsg.class);
        autoTestMessage(com.cannontech.message.notif.EconomicEventMsg.class);
        autoTestMessage(com.cannontech.message.notif.NotifEmailMsg.class);
        autoTestMessage(com.cannontech.message.notif.ProgramActionMsg.class);          
        autoTestMessage(com.cannontech.message.notif.NotifLMControlMsg.class);
        autoTestMessage(com.cannontech.message.notif.NotifCompletedMsg.class);
        autoTestMessage(com.cannontech.message.notif.VoiceDataRequestMsg.class);
        autoTestMessage(com.cannontech.message.notif.VoiceDataResponseMsg.class);
    }
}
