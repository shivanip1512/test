package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.dynamic.receive;

import com.cannontech.loadcontrol.dynamic.receive.LMTriggerChanged;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.ClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class TriggerChangedValidator extends AutoInitializedClassValidator<LMTriggerChanged> {
    private static long DEFAULT_SEED = 310;
    public TriggerChangedValidator() {
        super(LMTriggerChanged.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(LMTriggerChanged ctrlObj, RandomGenerator generator) {
        ctrlObj.setPaoID(generator.generateInt());
        ctrlObj.setTriggerNumber(generator.generateInt());
        ctrlObj.setPointValue(generator.generateDouble());
        ctrlObj.setLastPointValueTimestamp(generator.generateCalendar());
        ctrlObj.setNormalState(generator.generateInt());
        ctrlObj.setThreshold(generator.generateDouble());
        ctrlObj.setPeakPointValue(generator.generateDouble());
        ctrlObj.setLastPeakPointValueTimestamp(generator.generateCalendar());
        ctrlObj.setProjectedPointValue(generator.generateDouble());
    }
    
    @Override
    public ClassValidator<? super LMTriggerChanged> getParentValidator() {
     return null;
    }
}
