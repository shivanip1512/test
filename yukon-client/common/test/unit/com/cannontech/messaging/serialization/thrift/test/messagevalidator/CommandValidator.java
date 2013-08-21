package com.cannontech.messaging.serialization.thrift.test.messagevalidator;

import com.cannontech.message.util.Command;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class CommandValidator extends AutoInitializedClassValidator<Command> {

    private static long DEFAULT_SEED = 1;

    public CommandValidator() {
        super(Command.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(Command ctrlObj, RandomGenerator generator) {
        ctrlObj.setOperation(generator.generateInt());
        ctrlObj.setOpString(generator.generateString());
        ctrlObj.setOpArgList(generator.generateList(Integer.class));
    }
}
