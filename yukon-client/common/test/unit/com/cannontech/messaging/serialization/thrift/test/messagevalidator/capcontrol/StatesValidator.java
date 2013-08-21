package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.database.db.state.State;
import com.cannontech.message.capcontrol.model.States;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class StatesValidator extends AutoInitializedClassValidator<States> {
    private static long DEFAULT_SEED = 202;

    public StatesValidator() {
        super(States.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(States ctrlObj, RandomGenerator generator) {
        ctrlObj.setStates(getDefaultObjectVectorFor(State.class, generator));
    }
}
