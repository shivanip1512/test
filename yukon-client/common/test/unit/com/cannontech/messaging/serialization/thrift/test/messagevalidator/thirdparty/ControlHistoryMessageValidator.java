package com.cannontech.messaging.serialization.thrift.test.messagevalidator.thirdparty;

import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;
import com.cannontech.thirdparty.messaging.ControlHistoryMessage;

public class ControlHistoryMessageValidator extends AutoInitializedClassValidator<ControlHistoryMessage> {
    private static long DEFAULT_SEED = 4;

    public ControlHistoryMessageValidator() {
        super(ControlHistoryMessage.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(ControlHistoryMessage ctrlObj, RandomGenerator generator) {

        ctrlObj.setPaoId(generator.generateInt());
        ctrlObj.setPointId(generator.generateInt());
        ctrlObj.setRawState(generator.generateInt());
        ctrlObj.setStartTime(generator.generateDate());
        ctrlObj.setControlDuration(generator.generateInt());
        ctrlObj.setReductionRatio(generator.generateInt());
        ctrlObj.setControlType(generator.generateString());
        ctrlObj.setActiveRestore(generator.generateString());
        ctrlObj.setReductionValue(generator.generateDouble());
        ctrlObj.setControlPriority(generator.generateInt());
        ctrlObj.setAssociationId(generator.generateInt());
    }

}
