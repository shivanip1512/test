package com.cannontech.messaging.serialization.thrift.test.messagevalidator.notif;

import com.cannontech.message.notif.ProgramActionMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ProgramActionValidator extends AutoInitializedClassValidator<ProgramActionMsg> {

    private static long DEFAULT_SEED = 1;

    public ProgramActionValidator() {
        super(ProgramActionMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(ProgramActionMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.action = generator.generateString();
        ctrlObj.customerIds = ConverterHelper.toIntArray(generator.generateList(int.class));
        ctrlObj.eventDisplayName = generator.generateString();
        ctrlObj.notificationTime = generator.generateDate();
        ctrlObj.programId = generator.generateInt();
        ctrlObj.startTime = generator.generateDate();
        ctrlObj.stopTime = generator.generateDate();
    }
}
