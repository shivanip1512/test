package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import java.util.List;

import com.cannontech.loadcontrol.data.LMCurtailCustomer;
import com.cannontech.loadcontrol.data.LMProgramCurtailment;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ProgramCurtailmentValidator extends AutoInitializedClassValidator<LMProgramCurtailment> {

    public ProgramCurtailmentValidator() {
        super(LMProgramCurtailment.class);
    }

    @Override
    public void populateExpectedValue(LMProgramCurtailment ctrlObj, RandomGenerator generator) {
        ctrlObj.setMinNotifyTime(generator.generateInt());
        ctrlObj.setHeading(generator.generateString());
        ctrlObj.setMessageHeader(generator.generateString());
        ctrlObj.setMessageFooter(generator.generateString());
        ctrlObj.setAckTimeLimit(generator.generateInt());
        ctrlObj.setCanceledMsg(generator.generateString());
        ctrlObj.setStoppedEarlyMsg(generator.generateString());
        ctrlObj.setCurtailReferenceId(generator.generateInt());
        ctrlObj.setActionDateTime(generator.generateCalendar());
        ctrlObj.setNotificationDateTime(generator.generateCalendar());
        ctrlObj.setCurtailmentStartTime(generator.generateCalendar());
        ctrlObj.setCurtailmentStopTime(generator.generateCalendar());
        ctrlObj.setRunStatus(generator.generateString());
        ctrlObj.setAdditionalInfo(generator.generateString());
        
        ctrlObj.setLoadControlGroupVector((List)getDefaultObjectListFor(LMCurtailCustomer.class, generator));
    }
}
