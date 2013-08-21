package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMEnergyExchangeOffer;
import com.cannontech.loadcontrol.data.LMEnergyExchangeOfferRevision;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class EnergyExchangeOfferValidator extends AutoInitializedClassValidator<LMEnergyExchangeOffer> {

    public EnergyExchangeOfferValidator() {
        super(LMEnergyExchangeOffer.class);
    }

    @Override
    public void populateExpectedValue(LMEnergyExchangeOffer ctrlObj, RandomGenerator generator) {
        ctrlObj.setYukonID(generator.generateInt());
        ctrlObj.setOfferID(generator.generateInt());
        ctrlObj.setRunStatus(generator.generateString());
        ctrlObj.setOfferDate(generator.generateDate());
        
        ctrlObj.setEnergyExchangeOfferRevisions(getDefaultObjectVectorFor(LMEnergyExchangeOfferRevision.class, generator));       
    }
}
