package com.cannontech.messaging.serialization.thrift.test.messagevalidator.macs;

import com.cannontech.message.macs.message.OverrideRequest;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class OverrideRequestValidator extends AutoInitializedClassValidator<OverrideRequest> {
    private static long DEFAULT_SEED = 107;

    public OverrideRequestValidator() {
        super(OverrideRequest.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(OverrideRequest ctrlObj, RandomGenerator generator) {
        ctrlObj.setAction(generator.generateInt(0,5));
        ctrlObj.setSchedId(generator.generateInt());
        ctrlObj.setStart(generator.generateDate());
        ctrlObj.setStop(generator.generateDate());        
    }
}
