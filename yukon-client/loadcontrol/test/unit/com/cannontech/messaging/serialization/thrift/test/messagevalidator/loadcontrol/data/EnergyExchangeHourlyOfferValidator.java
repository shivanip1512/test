package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyOffer;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class EnergyExchangeHourlyOfferValidator extends AutoInitializedClassValidator<LMEnergyExchangeHourlyOffer> {

    public EnergyExchangeHourlyOfferValidator() {
        super(LMEnergyExchangeHourlyOffer.class);
    }

    @Override
    public void populateExpectedValue(LMEnergyExchangeHourlyOffer ctrlObj, RandomGenerator generator) {
        ctrlObj.setOfferID(generator.generateInt());
        ctrlObj.setRevisionNumber(generator.generateInt());
        ctrlObj.setHour(generator.generateInt());
        ctrlObj.setPrice(generator.generateDouble());
        ctrlObj.setAmountRequested(generator.generateDouble());
    }
}
