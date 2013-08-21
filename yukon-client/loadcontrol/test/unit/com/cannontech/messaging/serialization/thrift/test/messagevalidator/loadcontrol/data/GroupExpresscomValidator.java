package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMGroupExpresscom;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class GroupExpresscomValidator extends AutoInitializedClassValidator<LMGroupExpresscom> {

    public GroupExpresscomValidator() {
        super(LMGroupExpresscom.class);
    }

    @Override
    public void populateExpectedValue(LMGroupExpresscom ctrlObj, RandomGenerator generator) {

    }
}
