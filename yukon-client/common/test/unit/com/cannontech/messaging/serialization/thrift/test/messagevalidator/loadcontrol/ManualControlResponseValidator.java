package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol;

import com.cannontech.loadcontrol.messages.ConstraintViolation;
import com.cannontech.loadcontrol.messages.LMManualControlResponse;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ManualControlResponseValidator extends AutoInitializedClassValidator<LMManualControlResponse> {

    private static long DEFAULT_SEED = 303;

    public ManualControlResponseValidator() {
        super(LMManualControlResponse.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(LMManualControlResponse ctrlObj, RandomGenerator generator) {
        ctrlObj.setProgramID(generator.generateInt());

        ctrlObj.setConstraintViolations(getDefaultObjectListFor(ConstraintViolation.class, generator));
        ctrlObj.setBestFitAction(generator.generateString());
    }
}
