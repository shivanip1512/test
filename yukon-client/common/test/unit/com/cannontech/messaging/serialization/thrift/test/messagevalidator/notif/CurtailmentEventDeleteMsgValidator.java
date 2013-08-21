package com.cannontech.messaging.serialization.thrift.test.messagevalidator.notif;

import com.cannontech.message.notif.CurtailmentEventDeleteMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class CurtailmentEventDeleteMsgValidator extends AutoInitializedClassValidator<CurtailmentEventDeleteMsg> {

    private static long DEFAULT_SEED = 1;

    public CurtailmentEventDeleteMsgValidator() {
        super(CurtailmentEventDeleteMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(CurtailmentEventDeleteMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.curtailmentEventId = generator.generateInt();
        ctrlObj.deleteStart = generator.generateBoolean();
        ctrlObj.deleteStop =  generator.generateBoolean();
    }
}
