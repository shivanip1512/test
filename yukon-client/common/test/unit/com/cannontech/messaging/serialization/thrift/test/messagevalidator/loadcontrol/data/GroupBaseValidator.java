package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class GroupBaseValidator extends AutoInitializedClassValidator<LMGroupBase> {

    public GroupBaseValidator() {
        super(LMGroupBase.class);
    }

    @Override
    public void populateExpectedValue(LMGroupBase ctrlObj, RandomGenerator generator) {
        // Because LMGroupBase and LMDirectGroupBase are merge into one single class on the C++ side,
        // we do not generate any value in this level of the hierarchy, all generating code is in the
        // DirectGroupBaseValidator class
    }
}
