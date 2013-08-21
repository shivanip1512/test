package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.message.capcontrol.model.SubstationBuses;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class SubstationBusesValidator extends AutoInitializedClassValidator<SubstationBuses> {

    private static long DEFAULT_SEED = 206;

    public SubstationBusesValidator() {
        super(SubstationBuses.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(SubstationBuses ctrlObj, RandomGenerator generator) {
        ctrlObj.setMsgInfoBitMask(generator.generateUInt());

        ctrlObj.setSubBuses(getDefaultObjectVectorFor(SubBus.class, generator));
    }
}
