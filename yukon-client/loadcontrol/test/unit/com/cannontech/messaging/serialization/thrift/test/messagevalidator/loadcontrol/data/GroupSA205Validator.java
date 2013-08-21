package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMGroupSA205;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class GroupSA205Validator extends AutoInitializedClassValidator<LMGroupSA205> {

    public GroupSA205Validator() {
        super(LMGroupSA205.class);
    }

    @Override
    public void populateExpectedValue(LMGroupSA205 ctrlObj, RandomGenerator generator) {

    }
}
