package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol.streamable;

import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class StreamableCapObjectValidator extends AutoInitializedClassValidator<StreamableCapObject> {

    public StreamableCapObjectValidator() {
        super(StreamableCapObject.class);
    }

    @Override
    public void populateExpectedValue(StreamableCapObject ctrlObj, RandomGenerator generator) {
        ctrlObj.setCcId(generator.generateInt());
        ctrlObj.setCcCategory(generator.generateString());
        ctrlObj.setCcClass(generator.generateString());
        ctrlObj.setCcName(generator.generateString());
        ctrlObj.setCcType(generator.generateString());
        ctrlObj.setCcArea(generator.generateString());
        ctrlObj.setCcDisableFlag(generator.generateBoolean());
    }
}
