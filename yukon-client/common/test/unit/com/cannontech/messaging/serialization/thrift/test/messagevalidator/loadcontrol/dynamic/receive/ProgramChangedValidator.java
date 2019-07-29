package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.dynamic.receive;

import com.cannontech.loadcontrol.dynamic.receive.LMProgramChanged;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.ClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ProgramChangedValidator extends AutoInitializedClassValidator<LMProgramChanged> {
    private static long DEFAULT_SEED = 309;

    public ProgramChangedValidator() {
        super(LMProgramChanged.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(LMProgramChanged ctrlObj, RandomGenerator generator) {
        ctrlObj.setPaoID(generator.generateInt());
        ctrlObj.setDisableFlag(generator.generateBoolean());
        ctrlObj.setCurrentGearNumber(generator.generateInt() + 1); // see CPP side for the reason behind the +1
        ctrlObj.setLastGroupControlled(generator.generateInt());
        ctrlObj.setProgramState(generator.generateInt());
        ctrlObj.setReductionTotal(generator.generateDouble());
        ctrlObj.setDirectStartTime(generator.generateCalendar());
        ctrlObj.setDirectStopTime(generator.generateCalendar());
        ctrlObj.setNotifyActiveTime(generator.generateCalendar());
        ctrlObj.setNotifyInactiveTime(generator.generateCalendar());
        ctrlObj.setStartedRampingOutTime(generator.generateCalendar());
        ctrlObj.setOriginSource(generator.generateString());
    }
    
    @Override
    public ClassValidator<? super LMProgramChanged> getParentValidator() {
     return null;
    }
}
