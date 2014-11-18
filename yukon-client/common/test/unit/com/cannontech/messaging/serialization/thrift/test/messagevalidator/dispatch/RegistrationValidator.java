package com.cannontech.messaging.serialization.thrift.test.messagevalidator.dispatch;

import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class RegistrationValidator extends AutoInitializedClassValidator<Registration> {
    private static long DEFAULT_SEED = 15;

    public RegistrationValidator() {
        super(Registration.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(Registration ctrlObj, RandomGenerator generator) {
        ctrlObj.setAppName(generator.generateString());
        generator.generateInt(); // for cpp _appId field
        ctrlObj.setAppIsUnique(ConverterHelper.boolToInt(generator.generateBoolean()));
        ctrlObj.setAppExpirationDelay( generator.generateInt());
        ignoreField("regID");
    }
}
