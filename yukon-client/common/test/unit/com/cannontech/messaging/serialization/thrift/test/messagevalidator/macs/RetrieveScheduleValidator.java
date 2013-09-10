package com.cannontech.messaging.serialization.thrift.test.messagevalidator.macs;

import com.cannontech.message.macs.message.RetrieveSchedule;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class RetrieveScheduleValidator extends AutoInitializedClassValidator<RetrieveSchedule> {
    private static long DEFAULT_SEED = 104;

    public RetrieveScheduleValidator () {
        super(RetrieveSchedule.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(RetrieveSchedule ctrlObj, RandomGenerator generator) {
        ctrlObj.setScheduleId(generator.generateInt());
    }
}