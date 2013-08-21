package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMGroupSADigital;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class GroupSADigitalValidator extends AutoInitializedClassValidator<LMGroupSADigital> {

    public GroupSADigitalValidator() {
        super(LMGroupSADigital.class);
    }

    @Override
    public void populateExpectedValue(LMGroupSADigital ctrlObj, RandomGenerator generator) {

    }
}
