package com.cannontech.messaging.serialization.thrift.test.messagevalidator.mac;

import com.cannontech.message.macs.message.DeleteSchedule;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class DeleteScheduleValidator extends AutoInitializedClassValidator<DeleteSchedule> {
    private static long DEFAULT_SEED = 103;

    public DeleteScheduleValidator () {
        super(DeleteSchedule.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(DeleteSchedule ctrlObj, RandomGenerator generator) {
        ctrlObj.setScheduleId(generator.generateInt());
    }
}