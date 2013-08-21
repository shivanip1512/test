package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer;
import com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class EnergyExchangeCustomerValidator extends AutoInitializedClassValidator<LMEnergyExchangeCustomer> {

    public EnergyExchangeCustomerValidator() {
        super(LMEnergyExchangeCustomer.class);
    }

    @Override
    public void populateExpectedValue(LMEnergyExchangeCustomer ctrlObj, RandomGenerator generator) {       
        
        ctrlObj.setEnergyExchangeCustomerReplies(getDefaultObjectVectorFor(LMEnergyExchangeCustomerReply.class, generator));
    }
}
