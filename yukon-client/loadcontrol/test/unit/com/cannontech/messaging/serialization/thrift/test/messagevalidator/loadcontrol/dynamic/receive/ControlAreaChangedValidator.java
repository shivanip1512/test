package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.dynamic.receive;

import com.cannontech.loadcontrol.dynamic.receive.LMControlAreaChanged;
import com.cannontech.loadcontrol.dynamic.receive.LMTriggerChanged;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.ClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ControlAreaChangedValidator extends AutoInitializedClassValidator<LMControlAreaChanged> {
    private static long DEFAULT_SEED = 311;

    public ControlAreaChangedValidator() {
        super(LMControlAreaChanged.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(LMControlAreaChanged ctrlObj, RandomGenerator generator) {
        ctrlObj.setPaoID(generator.generateInt());
        ctrlObj.setDisableFlag(generator.generateBoolean());
        ctrlObj.setNextCheckTime(generator.generateCalendar());
        ctrlObj.setControlAreaState(generator.generateInt());
        ctrlObj.setCurrentPriority(generator.generateInt());
        ctrlObj.setCurrentDailyStartTime(generator.generateInt());
        ctrlObj.setCurrentDailyStopTime(generator.generateInt());

        ctrlObj.setTriggers(getDefaultObjectListFor(LMTriggerChanged.class, generator));
    }
    
    @Override
    public ClassValidator<? super LMControlAreaChanged> getParentValidator() {
     return null;
    }
}
