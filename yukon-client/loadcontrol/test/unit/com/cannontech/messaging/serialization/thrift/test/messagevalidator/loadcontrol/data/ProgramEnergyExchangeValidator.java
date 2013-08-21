package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer;
import com.cannontech.loadcontrol.data.LMEnergyExchangeOffer;
import com.cannontech.loadcontrol.data.LMProgramEnergyExchange;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ProgramEnergyExchangeValidator extends AutoInitializedClassValidator<LMProgramEnergyExchange> {

    public ProgramEnergyExchangeValidator() {
        super(LMProgramEnergyExchange.class);
    }

    @Override
    public void populateExpectedValue(LMProgramEnergyExchange ctrlObj, RandomGenerator generator) {
        ctrlObj.setMinNotifyTime(generator.generateInt());
        ctrlObj.setHeading(generator.generateString());
        ctrlObj.setMessageHeader(generator.generateString());
        ctrlObj.setMessageFooter(generator.generateString());
        ctrlObj.setCanceledMsg(generator.generateString());
        ctrlObj.setStoppedEarlyMsg(generator.generateString());

        ctrlObj.setEnergyExchangeOffers(getDefaultObjectVectorFor(LMEnergyExchangeOffer.class, generator));

        ctrlObj.setEnergyExchangeCustomers(getDefaultObjectVectorFor(LMEnergyExchangeCustomer.class, generator));

    }
}
