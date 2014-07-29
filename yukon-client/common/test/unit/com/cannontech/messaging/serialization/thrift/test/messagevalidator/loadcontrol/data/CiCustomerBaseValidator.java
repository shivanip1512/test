package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMCICustomerBase;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class CiCustomerBaseValidator extends AutoInitializedClassValidator<LMCICustomerBase> {

    public CiCustomerBaseValidator() {
        super(LMCICustomerBase.class);
    }

    @Override
    public void populateExpectedValue(LMCICustomerBase ctrlObj, RandomGenerator generator) {
        ctrlObj.setCustomerID((long) generator.generateInt());
        ctrlObj.setCompanyName(generator.generateString());
        ctrlObj.setCustomerDemandLevel(generator.generateDouble());
        ctrlObj.setCurtailAmount(generator.generateDouble());
        ctrlObj.setCurtailmentAgreement(generator.generateString());
        ctrlObj.setTimeZone(generator.generateString());
        ctrlObj.setCustomerOrder((long) generator.generateInt());
    }
}
