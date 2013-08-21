package com.cannontech.messaging.serialization.thrift.test.messagevalidator.notif;

import com.cannontech.message.notif.EconomicEventDeleteMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class EconomicEventDeleteMsgValidator extends AutoInitializedClassValidator<EconomicEventDeleteMsg> {

    private static long DEFAULT_SEED = 1;

    public EconomicEventDeleteMsgValidator() {
        super(EconomicEventDeleteMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(EconomicEventDeleteMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.deleteStart = generator.generateBoolean();
        ctrlObj.deleteStop = generator.generateBoolean();
        ctrlObj.economicEventId = generator.generateInt();
    }
}
