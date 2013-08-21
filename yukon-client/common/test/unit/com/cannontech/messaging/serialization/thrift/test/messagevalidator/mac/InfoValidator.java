package com.cannontech.messaging.serialization.thrift.test.messagevalidator.mac;

import com.cannontech.message.macs.message.Info;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class InfoValidator extends AutoInitializedClassValidator<Info> {
    private static long DEFAULT_SEED = 108;

    public InfoValidator() {
        super(Info.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(Info ctrlObj, RandomGenerator generator) {
        ctrlObj.setId(generator.generateInt());
        ctrlObj.setInfo(generator.generateString());        
    }
}