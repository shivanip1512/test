package com.cannontech.messaging.serialization.thrift.test.messagevalidator.dispatch;

import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class SignalValidator extends AutoInitializedClassValidator<Signal> {
    private static long DEFAULT_SEED = 19;

    public SignalValidator() {
        super(Signal.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(Signal ctrlObj, RandomGenerator generator) {
        ctrlObj.setPointID(generator.generateInt());
        ctrlObj.setLogType(generator.generateInt());
        ctrlObj.setCategoryID(generator.generateUIntAsLong());
        ctrlObj.setDescription(generator.generateString());
        ctrlObj.setAction(generator.generateString());
        ctrlObj.setTags(generator.generateUInt());
        ctrlObj.setCondition(generator.generateInt());
        ctrlObj.setMillis(generator.generateLong(0, 999));

        // For the missing field pointValue.
        generator.generateDouble();
    }
}
