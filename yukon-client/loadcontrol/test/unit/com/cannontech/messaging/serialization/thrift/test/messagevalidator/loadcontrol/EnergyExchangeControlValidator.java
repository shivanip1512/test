package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol;

import com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class EnergyExchangeControlValidator extends AutoInitializedClassValidator<LMEnergyExchangeControlMsg> {

    private static long DEFAULT_SEED = 304;

    public EnergyExchangeControlValidator() {
        super(LMEnergyExchangeControlMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(LMEnergyExchangeControlMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.setCommand(generator.generateInt());
        ctrlObj.setYukonID(generator.generateInt());
        ctrlObj.setOfferID(generator.generateInt());
        ctrlObj.setOfferDate(generator.generateDate());
        ctrlObj.setNotificationDateTime(generator.generateDate());
        ctrlObj.setExpirationDateTime(generator.generateDate());
        ctrlObj.setAdditionalInfo(generator.generateString());
        ctrlObj.setAmountRequested(ConverterHelper.toArray(generator.generateList(Double.class, 24), Double.class));
        ctrlObj.setPricesOffered(ConverterHelper.toArray(generator.generateList(Integer.class, 24),Integer.class));
    }
}
