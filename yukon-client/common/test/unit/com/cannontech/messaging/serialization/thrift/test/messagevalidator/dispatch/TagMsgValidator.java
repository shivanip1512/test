package com.cannontech.messaging.serialization.thrift.test.messagevalidator.dispatch;

import com.cannontech.message.dispatch.message.TagMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class TagMsgValidator extends AutoInitializedClassValidator<TagMsg> {
    private static long DEFAULT_SEED = 20;

    public TagMsgValidator() {
        super(TagMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(TagMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.setInstanceID(generator.generateInt());
        ctrlObj.setPointID(generator.generateInt());
        ctrlObj.setTagID(generator.generateInt());
        ctrlObj.setDescriptionStr(generator.generateString());
        ctrlObj.setAction(generator.generateInt(0,3));
        ctrlObj.setTagTime(generator.generateDate());
        ctrlObj.setReferenceStr(generator.generateString());
        ctrlObj.setTaggedForStr(generator.generateString());
        ctrlObj.setClientMessageID(generator.generateInt());      
    }
}
