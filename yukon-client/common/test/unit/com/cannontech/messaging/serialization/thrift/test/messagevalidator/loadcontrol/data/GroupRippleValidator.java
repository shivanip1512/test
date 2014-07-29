package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMGroupRipple;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class GroupRippleValidator extends AutoInitializedClassValidator<LMGroupRipple> {

    public GroupRippleValidator() {
        super(LMGroupRipple.class);
    }

    @Override
    public void populateExpectedValue(LMGroupRipple ctrlObj, RandomGenerator generator) {
        ctrlObj.setShedTime(generator.generateInt());
    }
}
