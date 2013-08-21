package com.cannontech.messaging.serialization.thrift.test.messagevalidator.notif;

import com.cannontech.message.notif.NotifCallEvent;
import com.cannontech.message.notif.NotifCompletedMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class NotifCompletedMsgValidator extends AutoInitializedClassValidator<NotifCompletedMsg> {

    private static long DEFAULT_SEED = 1;

    public NotifCompletedMsgValidator() {
        super(NotifCompletedMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(NotifCompletedMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.status = generator.generateEnum(NotifCallEvent.class);
        ctrlObj.token = generator.generateString();
    }
}
