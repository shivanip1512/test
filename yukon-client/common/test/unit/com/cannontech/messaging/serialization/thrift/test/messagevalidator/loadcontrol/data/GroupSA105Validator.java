package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMGroupSA105;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class GroupSA105Validator extends AutoInitializedClassValidator<LMGroupSA105> {

    public GroupSA105Validator() {
        super(LMGroupSA105.class);
    }

    @Override
    public void populateExpectedValue(LMGroupSA105 ctrlObj, RandomGenerator generator) {

    }
}
