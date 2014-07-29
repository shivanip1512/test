package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMGroupEmetcon;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class GroupEmetconValidator extends AutoInitializedClassValidator<LMGroupEmetcon> {

    public GroupEmetconValidator() {
        super(LMGroupEmetcon.class);
    }

    @Override
    public void populateExpectedValue(LMGroupEmetcon ctrlObj, RandomGenerator generator) {

    }
}
