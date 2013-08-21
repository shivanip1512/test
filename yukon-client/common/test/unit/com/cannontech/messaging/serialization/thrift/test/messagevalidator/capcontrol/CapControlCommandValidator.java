package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.message.capcontrol.model.CapControlCommand;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class CapControlCommandValidator extends AutoInitializedClassValidator<CapControlCommand> {
    private static long DEFAULT_SEED = 0;

    public CapControlCommandValidator() {
        super(CapControlCommand.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(CapControlCommand ctrlObj, RandomGenerator generator) {
        ctrlObj.setCommandId(generator.generateInt());
        ctrlObj.setMessageId(generator.generateInt());
    }
}
