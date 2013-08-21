package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.message.capcontrol.model.VerifyInactiveBanks;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class VerifyInactiveBanksValidator extends AutoInitializedClassValidator<VerifyInactiveBanks> {

    private static long DEFAULT_SEED = 216;

    public VerifyInactiveBanksValidator() {
        super(VerifyInactiveBanks.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(VerifyInactiveBanks ctrlObj, RandomGenerator generator) {
        ctrlObj.setCbInactivityTime(generator.generateInt());
    }
}
