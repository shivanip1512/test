package com.cannontech.messaging.serialization.thrift.test.messagevalidator.notif;

import com.cannontech.cc.service.CurtailmentEventAction;
import com.cannontech.message.notif.CurtailmentEventMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class CurtailmentEventMsgValidator extends AutoInitializedClassValidator<CurtailmentEventMsg> {

    private static long DEFAULT_SEED = 1;

    public CurtailmentEventMsgValidator() {
        super(CurtailmentEventMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(CurtailmentEventMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.action = generator.generateEnum(CurtailmentEventAction.class);
        ctrlObj.curtailmentEventId = generator.generateInt();

    }
}
