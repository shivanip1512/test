package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyOffer;
import com.cannontech.loadcontrol.data.LMEnergyExchangeOfferRevision;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class EnergyExchangeOfferRevisionValidator extends AutoInitializedClassValidator<LMEnergyExchangeOfferRevision> {

    public EnergyExchangeOfferRevisionValidator() {
        super(LMEnergyExchangeOfferRevision.class);
    }

    @Override
    public void populateExpectedValue(LMEnergyExchangeOfferRevision ctrlObj, RandomGenerator generator) {

        ctrlObj.setOfferID(generator.generateInt());
        ctrlObj.setRevisionNumber(generator.generateInt());
        ctrlObj.setActionDateTime(generator.generateDate());
        ctrlObj.setNotificationDateTime(generator.generateDate());
        ctrlObj.setOfferExpirationDateTime(generator.generateDate());
        ctrlObj.setAdditionalInfo(generator.generateString());
        
        ctrlObj.setEnergyExchangeHourlyOffers(getDefaultObjectVectorFor(LMEnergyExchangeHourlyOffer.class, generator));      
    }
}
