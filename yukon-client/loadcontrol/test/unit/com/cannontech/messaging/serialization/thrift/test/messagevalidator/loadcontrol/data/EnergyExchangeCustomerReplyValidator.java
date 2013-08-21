package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply;
import com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyCustomer;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class EnergyExchangeCustomerReplyValidator extends AutoInitializedClassValidator<LMEnergyExchangeCustomerReply> {

    public EnergyExchangeCustomerReplyValidator() {
        super(LMEnergyExchangeCustomerReply.class);
    }

    @Override
    public void populateExpectedValue(LMEnergyExchangeCustomerReply ctrlObj, RandomGenerator generator) {
        ctrlObj.setCustomerID(generator.generateInt());
        ctrlObj.setOfferID(generator.generateInt());
        ctrlObj.setAcceptStatus(generator.generateString());
        ctrlObj.setAcceptDateTime(generator.generateDate());
        ctrlObj.setRevisionNumber(generator.generateInt());
        ctrlObj.setIpAddressOfAcceptUser(generator.generateString());
        ctrlObj.setUserIDName(generator.generateString());
        ctrlObj.setNameOfAcceptPerson(generator.generateString());
        ctrlObj.setEnergyExchangeNotes(generator.generateString());

        ctrlObj.setEnergyExchangeHourlyCustomer(getDefaultObjectVectorFor(LMEnergyExchangeHourlyCustomer.class, generator));
    }
}
