package com.cannontech.messaging.serialization.thrift.test.messagevalidator;

import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class MessageValidator extends AutoInitializedClassValidator<Message> {
    
    public MessageValidator() {
        super(Message.class);
    }

    @Override
    public void populateExpectedValue(Message ctrlObj, RandomGenerator generator) {
        ctrlObj.setTimeStamp(generator.generateDate());
        ctrlObj.setPriority(generator.generateInt(0, 15));
        ctrlObj.setSoeTag(generator.generateInt());
        ctrlObj.setUserName(generator.generateString());
        ctrlObj.setSource(generator.generateString());
    }
}
