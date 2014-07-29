package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMGroupVersacom;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class GroupVersacomValidator extends AutoInitializedClassValidator<LMGroupVersacom> {

    public GroupVersacomValidator() {
        super(LMGroupVersacom.class);
    }

    @Override
    public void populateExpectedValue(LMGroupVersacom ctrlObj, RandomGenerator generator) {

    }
}
