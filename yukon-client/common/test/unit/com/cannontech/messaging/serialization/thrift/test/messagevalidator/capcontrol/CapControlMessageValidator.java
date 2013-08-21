package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.message.capcontrol.model.CapControlMessage;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class CapControlMessageValidator  extends AutoInitializedClassValidator<CapControlMessage> {
    private static long DEFAULT_SEED = 200;

    public CapControlMessageValidator() {
        super(CapControlMessage.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(CapControlMessage ctrlObj, RandomGenerator generator) {
    }
}