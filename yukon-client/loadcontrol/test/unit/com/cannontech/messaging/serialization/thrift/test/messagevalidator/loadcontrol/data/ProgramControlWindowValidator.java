package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMProgramControlWindow;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ProgramControlWindowValidator extends AutoInitializedClassValidator<LMProgramControlWindow> {

    public ProgramControlWindowValidator() {
        super(LMProgramControlWindow.class);
    }

    @Override
    public void populateExpectedValue(LMProgramControlWindow ctrlObj, RandomGenerator generator) {
        ctrlObj.setYukonID(generator.generateInt());
        ctrlObj.setWindowNumber(generator.generateInt());
        ctrlObj.setAvailableStartTime(generator.generateInt());
        ctrlObj.setAvailableStopTime(generator.generateInt());        
    }
}
