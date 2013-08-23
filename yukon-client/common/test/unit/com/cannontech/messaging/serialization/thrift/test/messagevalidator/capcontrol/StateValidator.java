package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.database.db.state.State;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.ClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class StateValidator extends AutoInitializedClassValidator<State> {
    private static long DEFAULT_SEED = 200;

    public StateValidator() {
        super(State.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(State ctrlObj, RandomGenerator generator) {
        ctrlObj.setText(generator.generateString());
        ctrlObj.setForegroundColor(generator.generateInt());
        ctrlObj.setBackgroundColor(generator.generateInt());

        this.ignoreField("stateGroupID");
        this.ignoreField("rawState");
        this.ignoreField("imageID");
    }

    @Override
    /**
     * We ignore parent validator because State base class is a DB related class and is not involved in serialization
     */
    public ClassValidator<com.cannontech.database.db.DBPersistent> getParentValidator() {
        return null;
    }
}
