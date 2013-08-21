package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMGroupGolay;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class GroupGolayValidator extends AutoInitializedClassValidator<LMGroupGolay> {

    public GroupGolayValidator() {
        super(LMGroupGolay.class);
    }

    @Override
    public void populateExpectedValue(LMGroupGolay ctrlObj, RandomGenerator generator) {
     
    }
}
