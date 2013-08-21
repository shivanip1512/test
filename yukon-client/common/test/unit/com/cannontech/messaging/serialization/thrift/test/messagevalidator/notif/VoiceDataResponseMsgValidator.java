package com.cannontech.messaging.serialization.thrift.test.messagevalidator.notif;

import com.cannontech.message.notif.VoiceDataResponseMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class VoiceDataResponseMsgValidator extends AutoInitializedClassValidator<VoiceDataResponseMsg> {

    private static long DEFAULT_SEED = 1;

    public VoiceDataResponseMsgValidator() {
        super(VoiceDataResponseMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(VoiceDataResponseMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.callToken = generator.generateString();
        ctrlObj.contactId = generator.generateInt();
        ctrlObj.xmlData = generator.generateString();
    }
}