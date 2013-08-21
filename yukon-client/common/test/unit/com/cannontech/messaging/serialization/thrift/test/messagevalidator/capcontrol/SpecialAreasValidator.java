package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.message.capcontrol.model.SpecialAreas;
import com.cannontech.message.capcontrol.streamable.SpecialArea;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class SpecialAreasValidator extends AutoInitializedClassValidator<SpecialAreas> {

    private static long DEFAULT_SEED = 205;

    public SpecialAreasValidator() {
        super(SpecialAreas.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(SpecialAreas ctrlObj, RandomGenerator generator) {
        ctrlObj.setAreas(getDefaultObjectListFor(SpecialArea.class , generator));
    }
}
