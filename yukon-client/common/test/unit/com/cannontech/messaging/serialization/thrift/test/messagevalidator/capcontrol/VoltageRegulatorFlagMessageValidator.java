package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol;

import com.cannontech.message.capcontrol.model.VoltageRegulatorFlagMessage;
import com.cannontech.message.capcontrol.streamable.VoltageRegulatorFlags;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class VoltageRegulatorFlagMessageValidator extends AutoInitializedClassValidator<VoltageRegulatorFlagMessage> {

    private static long DEFAULT_SEED = 210;

    public VoltageRegulatorFlagMessageValidator() {
        super(VoltageRegulatorFlagMessage.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(VoltageRegulatorFlagMessage ctrlObj, RandomGenerator generator) {
        ctrlObj
            .setVoltageRegulatorFlags(getDefaultObjectVectorFor(VoltageRegulatorFlags.class, generator, 2));
    }
}
