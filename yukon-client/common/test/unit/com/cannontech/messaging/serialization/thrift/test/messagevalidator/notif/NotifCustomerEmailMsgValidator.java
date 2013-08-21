package com.cannontech.messaging.serialization.thrift.test.messagevalidator.notif;

import com.cannontech.message.notif.NotifCustomerEmailMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class NotifCustomerEmailMsgValidator extends AutoInitializedClassValidator<NotifCustomerEmailMsg> {
    private static long DEFAULT_SEED = 8;

    public NotifCustomerEmailMsgValidator() {
        super(NotifCustomerEmailMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(NotifCustomerEmailMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.setCustomerID(generator.generateInt());

        generator.generateString(); // msg._to field in cpp

        ctrlObj.setSubject(generator.generateString());
        ctrlObj.setBody(generator.generateString());

        generator.generateString(); // msg._toCC field in cpp
        generator.generateString(); // msg._toBCC field in cpp
    }
}
