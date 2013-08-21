package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.message.capcontrol.model.ChangeOpState;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ChangeOpStateValidator extends AutoInitializedClassValidator<ChangeOpState> {

    private static long DEFAULT_SEED = 213;

    public ChangeOpStateValidator() {
        super(ChangeOpState.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(ChangeOpState ctrlObj, RandomGenerator generator) {
        ctrlObj.setState(generator.generateEnum(BankOpState.class));
    }
}
