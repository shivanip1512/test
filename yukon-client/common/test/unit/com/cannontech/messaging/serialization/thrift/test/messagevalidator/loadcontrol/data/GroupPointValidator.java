package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMGroupPoint;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class GroupPointValidator extends AutoInitializedClassValidator<LMGroupPoint> {

    public GroupPointValidator() {
        super(LMGroupPoint.class);
    }

    @Override
    public void populateExpectedValue(LMGroupPoint ctrlObj, RandomGenerator generator) {
        
    }
}
