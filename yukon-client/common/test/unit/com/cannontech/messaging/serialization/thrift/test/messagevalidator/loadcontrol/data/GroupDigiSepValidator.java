package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMGroupDigiSep;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class GroupDigiSepValidator extends AutoInitializedClassValidator<LMGroupDigiSep> {

    public GroupDigiSepValidator() {
        super(LMGroupDigiSep.class);
    }

    @Override
    public void populateExpectedValue(LMGroupDigiSep ctrlObj, RandomGenerator generator) {

    }
}
