package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMCurtailCustomer;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class CurtailCustomerValidator extends AutoInitializedClassValidator<LMCurtailCustomer> {

    public CurtailCustomerValidator() {
        super(LMCurtailCustomer.class);
    }

    @Override
    public void populateExpectedValue(LMCurtailCustomer ctrlObj, RandomGenerator generator) {
        ctrlObj.setRequireAck(generator.generateBoolean());
        ctrlObj.setCurtailRefID((long)generator.generateInt());
        ctrlObj.setAckStatus(generator.generateString());
        ctrlObj.setAckDateTime(generator.generateDate());
        ctrlObj.setIpAddress(generator.generateString());
        ctrlObj.setUserIDname(generator.generateString());
        ctrlObj.setNameOfAckPerson(generator.generateString());
        ctrlObj.setCurtailmentNotes(generator.generateString());
        ctrlObj.setAckLateFlag(generator.generateBoolean());
    }
}
