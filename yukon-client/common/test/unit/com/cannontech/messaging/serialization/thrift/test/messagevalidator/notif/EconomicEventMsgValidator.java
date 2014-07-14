package com.cannontech.messaging.serialization.thrift.test.messagevalidator.notif;

import com.cannontech.cc.service.EconomicEventAction;
import com.cannontech.message.notif.EconomicEventMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class EconomicEventMsgValidator extends AutoInitializedClassValidator<EconomicEventMsg> {

    private static long DEFAULT_SEED = 1;

    public EconomicEventMsgValidator() {
        super(EconomicEventMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(EconomicEventMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.action = generator.generateEnum(EconomicEventAction.class);
        ctrlObj.economicEventId = generator.generateInt();
        ctrlObj.revisionNumber = generator.generateInt();
    }
}
