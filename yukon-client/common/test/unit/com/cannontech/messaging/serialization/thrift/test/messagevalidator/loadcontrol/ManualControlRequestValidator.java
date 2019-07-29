package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol;


import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ManualControlRequestValidator extends AutoInitializedClassValidator<LMManualControlRequest> {

    private static long DEFAULT_SEED = 302;
    
    
    public ManualControlRequestValidator() {
        super(LMManualControlRequest.class, DEFAULT_SEED);        
    }

    @Override
    public void populateExpectedValue(LMManualControlRequest ctrlObj, RandomGenerator generator) {
        ctrlObj.setCommand(generator.generateInt());
        ctrlObj.setYukonID(generator.generateInt());
        ctrlObj.setNotifyTime(generator.generateCalendar());
        ctrlObj.setStartTime(generator.generateCalendar());
        ctrlObj.setStopTime(generator.generateCalendar());
        ctrlObj.setStartGear(generator.generateInt());
        ctrlObj.setStartPriority(generator.generateInt());
        ctrlObj.setAddditionalInfo(generator.generateString());
        ctrlObj.setOriginSource(generator.generateString());
        ctrlObj.setConstraintFlag(generator.generateInt());
    }
}
