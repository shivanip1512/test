package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.message.capcontrol.model.VerifyBanks;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class VerifyBanksValidator extends AutoInitializedClassValidator<VerifyBanks> {

    private static long DEFAULT_SEED = 215;
    
    
    public VerifyBanksValidator() {
        super(VerifyBanks.class, DEFAULT_SEED);        
    }

    @Override
    public void populateExpectedValue(VerifyBanks ctrlObj, RandomGenerator generator) {
        ctrlObj.setDisableOvUv(generator.generateBoolean());
    }
}
