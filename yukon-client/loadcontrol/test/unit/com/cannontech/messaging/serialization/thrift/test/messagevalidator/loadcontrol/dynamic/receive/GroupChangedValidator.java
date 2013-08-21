package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.dynamic.receive;

import com.cannontech.loadcontrol.dynamic.receive.LMGroupChanged;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.ClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class GroupChangedValidator extends AutoInitializedClassValidator<LMGroupChanged> {

    private static long DEFAULT_SEED = 308;

    public GroupChangedValidator() {
        super(LMGroupChanged.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(LMGroupChanged ctrlObj, RandomGenerator generator) {
        ctrlObj.setPaoID(generator.generateInt());
        ctrlObj.setDisableFlag(generator.generateBoolean());
        ctrlObj.setGroupControlState(generator.generateInt());
        ctrlObj.setCurrentHoursDaily(generator.generateInt());
        ctrlObj.setCurrentHoursMonthly(generator.generateInt());
        ctrlObj.setCurrentHoursSeasonal(generator.generateInt());
        ctrlObj.setCurrentHoursAnnually(generator.generateInt());
        ctrlObj.setLastControlSent(generator.generateCalendar());
        ctrlObj.setControlStartTime(generator.generateCalendar());
        ctrlObj.setControlCompleteTime(generator.generateCalendar());
        ctrlObj.setNextControlTime(generator.generateCalendar());
        ctrlObj.setInternalState(generator.generateUInt());
        ctrlObj.setDailyOps(generator.generateInt());
    }
    
    @Override
    public ClassValidator<? super LMGroupChanged> getParentValidator() {
     return null;
    }
}
