package com.cannontech.messaging.serialization.thrift.test.messagevalidator.macs;

import com.cannontech.message.macs.message.AddSchedule;
import com.cannontech.message.macs.message.Schedule;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class AddScheduleValidator extends AutoInitializedClassValidator<AddSchedule> {
    private static long DEFAULT_SEED = 102;

    public AddScheduleValidator() {
        super(AddSchedule.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(AddSchedule ctrlObj, RandomGenerator generator) {
        ctrlObj.setScript(generator.generateString());

        ctrlObj.setSchedule(getDefaultObjectFor(Schedule.class, generator));
    }
}
