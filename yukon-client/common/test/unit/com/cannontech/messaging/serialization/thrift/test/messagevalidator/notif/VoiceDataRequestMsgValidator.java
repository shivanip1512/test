package com.cannontech.messaging.serialization.thrift.test.messagevalidator.notif;

import com.cannontech.message.notif.VoiceDataRequestMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class VoiceDataRequestMsgValidator extends AutoInitializedClassValidator<VoiceDataRequestMsg> {

    private static long DEFAULT_SEED = 1;

    public VoiceDataRequestMsgValidator() {
        super(VoiceDataRequestMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(VoiceDataRequestMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.callToken = generator.generateString();
    }
}