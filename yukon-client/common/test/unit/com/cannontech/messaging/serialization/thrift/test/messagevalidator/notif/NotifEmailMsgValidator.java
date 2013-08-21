package com.cannontech.messaging.serialization.thrift.test.messagevalidator.notif;

import com.cannontech.message.notif.NotifEmailMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class NotifEmailMsgValidator extends AutoInitializedClassValidator<NotifEmailMsg> {
    private static long DEFAULT_SEED = 7;

    public NotifEmailMsgValidator() {
        super(NotifEmailMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(NotifEmailMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.setNotifGroupID(generator.generateInt());
        ctrlObj.setTo(generator.generateString());
        ctrlObj.setSubject(generator.generateString());
        ctrlObj.setBody(generator.generateString());
        ctrlObj.setTo_CC(generator.generateString());
        ctrlObj.setTo_BCC(generator.generateString());
    }
}
