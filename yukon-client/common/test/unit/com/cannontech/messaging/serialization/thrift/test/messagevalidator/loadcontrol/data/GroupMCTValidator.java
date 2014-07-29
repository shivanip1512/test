package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMGroupMCT;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class GroupMCTValidator extends AutoInitializedClassValidator<LMGroupMCT> {

    public GroupMCTValidator() {
        super(LMGroupMCT.class);
    }

    @Override
    public void populateExpectedValue(LMGroupMCT ctrlObj, RandomGenerator generator) {
        
    }
}
