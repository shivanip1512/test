package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.message.capcontrol.model.CapControlResponseType;
import com.cannontech.message.capcontrol.model.CapControlServerResponse;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class CapControlServerResponseValidator extends AutoInitializedClassValidator<CapControlServerResponse> {
    private static long DEFAULT_SEED = 201;

    public CapControlServerResponseValidator() {
        super(CapControlServerResponse.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(CapControlServerResponse ctrlObj, RandomGenerator generator) {
        ctrlObj.setMessageId(generator.generateInt());
        ctrlObj.setResponseType(generator.generateEnum(CapControlResponseType.class));
        ctrlObj.setResponse(generator.generateString());        
    }
}
