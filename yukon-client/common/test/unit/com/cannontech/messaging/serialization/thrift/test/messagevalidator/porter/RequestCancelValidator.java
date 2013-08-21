package com.cannontech.messaging.serialization.thrift.test.messagevalidator.porter;

import com.cannontech.message.porter.message.RequestCancel;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class RequestCancelValidator extends AutoInitializedClassValidator<RequestCancel> {
    private static long DEFAULT_SEED = 16;

    public RequestCancelValidator() {
        super(RequestCancel.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(RequestCancel ctrlObj, RandomGenerator generator) {
        ctrlObj.setRequestId(generator.generateInt());
        ctrlObj.setRequestIdCount(generator.generateUIntAsLong());
        ctrlObj.setUserMessageId(generator.generateInt());
        ctrlObj.setTime(generator.generateDate());      
    }
}
