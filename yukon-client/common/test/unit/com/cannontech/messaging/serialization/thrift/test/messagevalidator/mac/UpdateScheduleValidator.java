package com.cannontech.messaging.serialization.thrift.test.messagevalidator.mac;

import com.cannontech.message.macs.message.Schedule;
import com.cannontech.message.macs.message.UpdateSchedule;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class UpdateScheduleValidator extends AutoInitializedClassValidator<UpdateSchedule> {
    private static long DEFAULT_SEED = 101;

    public UpdateScheduleValidator() {
        super(UpdateSchedule.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(UpdateSchedule ctrlObj, RandomGenerator generator) {
        ctrlObj.setScript(generator.generateString());
        
        ctrlObj.setSchedule(getDefaultObjectFor(Schedule.class, generator));
    }
}