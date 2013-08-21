package com.cannontech.messaging.serialization.thrift.test.messagevalidator.capcontrol.streamable;

import com.cannontech.capcontrol.TapOperation;
import com.cannontech.message.capcontrol.streamable.VoltageRegulatorFlags;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class VoltageRegulatorFlagsValidator extends AutoInitializedClassValidator<VoltageRegulatorFlags> {

    public VoltageRegulatorFlagsValidator() {
        super(VoltageRegulatorFlags.class);
    }

    @Override
    public void populateExpectedValue(VoltageRegulatorFlags ctrlObj, RandomGenerator generator) {
        ctrlObj.setLastOperation(generator.generateEnum(TapOperation.class));
        ctrlObj.setLastOperationTime(generator.generateDate());
        ctrlObj.setRecentOperation(generator.generateBoolean());
        ctrlObj.setAutoRemote(generator.generateBoolean());
        ctrlObj.setAutoRemoteManual(generator.generateBoolean());
    }
}
