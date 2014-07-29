package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol;

import com.cannontech.loadcontrol.messages.LMMessage;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class LmMessageValidator extends AutoInitializedClassValidator<LMMessage> {

    private static long DEFAULT_SEED = 300;

    public LmMessageValidator() {
        super(LMMessage.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(LMMessage ctrlObj, RandomGenerator generator) {
        ctrlObj.setMessage(generator.generateString());
    }
}
