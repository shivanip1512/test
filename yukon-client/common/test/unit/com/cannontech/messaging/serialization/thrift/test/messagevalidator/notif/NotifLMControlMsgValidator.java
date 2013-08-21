package com.cannontech.messaging.serialization.thrift.test.messagevalidator.notif;

import com.cannontech.message.notif.NotifLMControlMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class NotifLMControlMsgValidator extends AutoInitializedClassValidator<NotifLMControlMsg> {
    private static long DEFAULT_SEED = 9;

    public NotifLMControlMsgValidator() {
        super(NotifLMControlMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(NotifLMControlMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.notifGroupIds = ConverterHelper.toIntArray(generator.generateList(Integer.class));
        ctrlObj.notifType = generator.generateInt();
        ctrlObj.programId = generator.generateInt();
        ctrlObj.startTime = generator.generateDate();
        ctrlObj.stopTime = generator.generateDate();
    }
}
