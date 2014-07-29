package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol;


import com.cannontech.loadcontrol.messages.LMEnergyExchangeAcceptMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class EnergyExchangeAcceptValidator extends AutoInitializedClassValidator<LMEnergyExchangeAcceptMsg> {

    private static long DEFAULT_SEED = 305;
    
    
    public EnergyExchangeAcceptValidator() {
        super(LMEnergyExchangeAcceptMsg.class, DEFAULT_SEED);        
    }

    @Override
    public void populateExpectedValue(LMEnergyExchangeAcceptMsg ctrlObj, RandomGenerator generator) {
        ctrlObj.setYukonID(generator.generateInt());
        ctrlObj.setOfferID(generator.generateInt());
        ctrlObj.setRevisionNumber(generator.generateInt());
        ctrlObj.setAcceptStatus(generator.generateString());
        ctrlObj.setIpAddressOfCustomer(generator.generateString());
        ctrlObj.setUserIDName(generator.generateString());
        ctrlObj.setNameOfAcceptPerson(generator.generateString());
        ctrlObj.setEnergyExchangeNotes(generator.generateString());
        ctrlObj.setAmountCommitted(ConverterHelper.toArray(generator.generateList(Double.class, 24), Double.class));        
    }
}
