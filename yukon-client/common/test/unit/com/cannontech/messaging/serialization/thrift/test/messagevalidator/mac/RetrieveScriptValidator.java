package com.cannontech.messaging.serialization.thrift.test.messagevalidator.mac;

import com.cannontech.message.macs.message.RetrieveScript;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class RetrieveScriptValidator extends AutoInitializedClassValidator<RetrieveScript> {
    private static long DEFAULT_SEED = 105;

    public RetrieveScriptValidator () {
        super(RetrieveScript.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(RetrieveScript ctrlObj, RandomGenerator generator) {
        ctrlObj.setScriptName(generator.generateString());
    }
}