package com.cannontech.messaging.serialization.thrift.test.messagevalidator;

import com.cannontech.message.util.Ping;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class PingValidator extends AutoInitializedClassValidator<Ping> {
    
    public PingValidator() {
        super(Ping.class);
    }

    @Override
    public void populateExpectedValue(Ping ctrlObj, RandomGenerator generator) {
    }
}
