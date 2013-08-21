package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.message.capcontrol.model.SubAreas;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class SubAreasValidator extends AutoInitializedClassValidator<SubAreas> {
    private static long DEFAULT_SEED = 204;

    public SubAreasValidator() {
        super(SubAreas.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(SubAreas ctrlObj, RandomGenerator generator) {
        ctrlObj.setMsgInfoBitMask(generator.generateUInt());

        ctrlObj.setAreas(getDefaultObjectListFor(Area.class, generator));
    }
}
