package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMGroupSA305;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class GroupSA305Validator extends AutoInitializedClassValidator<LMGroupSA305> {

    public GroupSA305Validator() {
        super(LMGroupSA305.class);
    }

    @Override
    public void populateExpectedValue(LMGroupSA305 ctrlObj, RandomGenerator generator) {

    }
}
