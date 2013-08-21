package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.message.capcontrol.model.ItemCommand;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ItemCommandValidator extends AutoInitializedClassValidator<ItemCommand> {
    private static long DEFAULT_SEED = 212;

    public ItemCommandValidator() {
        super(ItemCommand.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(ItemCommand ctrlObj, RandomGenerator generator) {
        ctrlObj.setItemId(generator.generateInt());       
    }
}
