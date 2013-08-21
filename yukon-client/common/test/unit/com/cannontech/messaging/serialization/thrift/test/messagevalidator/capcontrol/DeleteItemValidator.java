package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.message.capcontrol.model.DeleteItem;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class DeleteItemValidator extends AutoInitializedClassValidator<DeleteItem> {

    private static long DEFAULT_SEED = 208;
    
    
    public DeleteItemValidator() {
        super(DeleteItem.class, DEFAULT_SEED);        
    }

    @Override
    public void populateExpectedValue(DeleteItem ctrlObj, RandomGenerator generator) {
        ctrlObj.setItemId(generator.generateInt());
    }
}
