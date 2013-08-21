package com.cannontech.messaging.serialization.thrift.test.messagevalidator.loadcontrol;

import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.messages.LMControlAreaMsg;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ControlAreaValidator extends AutoInitializedClassValidator<LMControlAreaMsg> {

    private static long DEFAULT_SEED = 306;

    public ControlAreaValidator() {
        super(LMControlAreaMsg.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(LMControlAreaMsg ctrlObj, RandomGenerator generator) {

        ctrlObj.setMsgInfoBitMask(generator.generateUInt());

        ctrlObj.setLMControlAreaVector(getDefaultObjectListFor(LMControlArea.class, generator));
    }
}
