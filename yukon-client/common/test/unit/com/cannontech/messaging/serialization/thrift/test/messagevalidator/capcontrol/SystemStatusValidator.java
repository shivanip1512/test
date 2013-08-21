package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.message.capcontrol.model.SystemStatus;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class SystemStatusValidator extends AutoInitializedClassValidator<SystemStatus> {

    private static long DEFAULT_SEED = 209;
    
    
    public SystemStatusValidator() {
        super(SystemStatus.class, DEFAULT_SEED);        
    }

    @Override
    public void populateExpectedValue(SystemStatus ctrlObj, RandomGenerator generator) {
        ctrlObj.setEnabled(generator.generateBoolean());
    }
}
