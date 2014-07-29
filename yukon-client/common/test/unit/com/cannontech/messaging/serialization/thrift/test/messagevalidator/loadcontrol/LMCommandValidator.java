package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol;


import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;


public class LMCommandValidator extends AutoInitializedClassValidator<LMCommand> {
    private static long DEFAULT_SEED = 301;

    public LMCommandValidator() {
        super(LMCommand.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(LMCommand ctrlObj, RandomGenerator generator) {
        ctrlObj.setCommand(generator.generateInt());
        ctrlObj.setYukonID(generator.generateInt());
        ctrlObj.setNumber(generator.generateInt());
        ctrlObj.setValue(generator.generateDouble());
        ctrlObj.setCount(generator.generateInt());
        ctrlObj.setAuxid(generator.generateInt());        
    }
}
