package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyCustomer;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class EnergyExchangeHourlyCustomerValidator extends
    AutoInitializedClassValidator<LMEnergyExchangeHourlyCustomer> {

    public EnergyExchangeHourlyCustomerValidator() {
        super(LMEnergyExchangeHourlyCustomer.class);
    }

    @Override
    public void populateExpectedValue(LMEnergyExchangeHourlyCustomer ctrlObj, RandomGenerator generator) {
        ctrlObj.setCustomerID(generator.generateInt());
        ctrlObj.setOfferID(generator.generateInt());
        ctrlObj.setRevisionNumber(generator.generateInt());
        ctrlObj.setHour(generator.generateInt());
        ctrlObj.setAmountCommitted(generator.generateDouble());
    }
}
