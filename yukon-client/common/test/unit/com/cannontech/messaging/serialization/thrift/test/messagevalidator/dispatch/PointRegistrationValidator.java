package com.cannontech.messaging.serialization.thrift.test.messagevalidator.dispatch;

import java.util.HashSet;

import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class PointRegistrationValidator extends AutoInitializedClassValidator<PointRegistration> {
    private static long DEFAULT_SEED = 13;

    public PointRegistrationValidator() {
        super(PointRegistration.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(PointRegistration ctrlObj, RandomGenerator generator) {
        ctrlObj.setRegFlags(generator.generateInt());
        ctrlObj.setPointIds(new HashSet<Integer>(generator.generateList(Integer.class)));
    }
}
