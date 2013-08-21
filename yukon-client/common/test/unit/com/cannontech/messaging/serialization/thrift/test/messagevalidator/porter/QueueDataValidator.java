package com.cannontech.messaging.serialization.thrift.test.messagevalidator.porter;

import com.cannontech.message.porter.message.QueueData;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class QueueDataValidator extends AutoInitializedClassValidator<QueueData> {
    private static long DEFAULT_SEED = 14;

    public QueueDataValidator() {
        super(QueueData.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(QueueData ctrlObj, RandomGenerator generator) {
        ctrlObj.setQueueId(generator.generateInt());
        ctrlObj.setRequestId(generator.generateInt());
        ctrlObj.setRate(generator.generateUIntAsLong());
        ctrlObj.setQueueCount(generator.generateUIntAsLong());
        ctrlObj.setRequestIdCount(generator.generateUIntAsLong());
        ctrlObj.setUserMessageId(generator.generateInt());
        ctrlObj.setTime(generator.generateDate());
    }
}
