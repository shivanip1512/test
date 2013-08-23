package com.cannontech.messaging.serialization.thrift.test.messagevalidator;

import com.cannontech.message.util.CollectableBoolean;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class BooleanDataValidator extends AutoInitializedClassValidator<CollectableBoolean> {
    
    public BooleanDataValidator() {
        super(CollectableBoolean.class);
    }

    @Override
    public void populateExpectedValue(CollectableBoolean ctrlObj, RandomGenerator generator) {
        ctrlObj.setValue(generator.generateBoolean());
    }
}
