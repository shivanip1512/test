package com.cannontech.messaging.serialization.thrift.test.messagevalidator.porter;

import com.cannontech.message.porter.message.Request;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class RequestMessageValidator extends AutoInitializedClassValidator<Request> {
    private static long DEFAULT_SEED = 10;

    public RequestMessageValidator() {
        super(Request.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(Request ctrlObj, RandomGenerator generator) {
        ctrlObj.setDeviceID(generator.generateInt());
        ctrlObj.setCommandString(generator.generateString());
        ctrlObj.setRouteID(generator.generateInt());
        ctrlObj.setMacroOffset(
            generator.generateBoolean()
                ? generator.generateUInt() + 1
                : 0);
        ctrlObj.setAttemptNum(generator.generateInt());
        ctrlObj.setGroupMessageID(generator.generateInt());
        ctrlObj.setUserMessageID(generator.generateInt());
        ctrlObj.setOptionsField(generator.generateInt());        
    }
}
