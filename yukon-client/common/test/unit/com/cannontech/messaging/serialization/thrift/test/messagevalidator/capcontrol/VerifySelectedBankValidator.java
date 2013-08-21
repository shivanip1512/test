package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.message.capcontrol.model.VerifySelectedBank;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class VerifySelectedBankValidator extends AutoInitializedClassValidator<VerifySelectedBank> {

    private static long DEFAULT_SEED = 217;
    
    
    public VerifySelectedBankValidator() {
        super(VerifySelectedBank.class, DEFAULT_SEED);        
    }

    @Override
    public void populateExpectedValue(VerifySelectedBank ctrlObj, RandomGenerator generator) {
        ctrlObj.setBankId(generator.generateInt());
    }
}
