package com.cannontech.messaging.serialization.thrift.test.messagevalidator.macs;

import com.cannontech.message.macs.message.ScriptFile;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ScriptFileValidator extends AutoInitializedClassValidator<ScriptFile> {
    private static long DEFAULT_SEED = 109;

    public ScriptFileValidator() {
        super(ScriptFile.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(ScriptFile ctrlObj, RandomGenerator generator) {
        ctrlObj.setFileName(generator.generateString());
        ctrlObj.setFileContents(generator.generateString());       
    }
}
