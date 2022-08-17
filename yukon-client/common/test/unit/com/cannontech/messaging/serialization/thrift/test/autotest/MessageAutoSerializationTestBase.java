package com.cannontech.messaging.serialization.thrift.test.autotest;

import org.junit.Assert;

import com.cannontech.messaging.serialization.SerializationResult;
import com.cannontech.messaging.serialization.thrift.test.MessageSerializationTestBase;
import com.cannontech.messaging.serialization.thrift.test.ValidationHelper;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;
import com.cannontech.messaging.serialization.thrift.test.validator.ValidationResult;
import com.cannontech.messaging.serialization.thrift.test.validator.Validator;

public abstract class MessageAutoSerializationTestBase extends MessageSerializationTestBase {
    private RandomGenerator generator;

    protected MessageAutoSerializationTestBase() {
        generator = new RandomGenerator(System.currentTimeMillis());
    }

    @Override
    protected String[] getContextUri() {
        return new String[] { "com/cannontech/messaging/testThriftConnectionContext.xml",
                             "com/cannontech/messaging/serialization/thrift/test/messagevalidator/messageValidatorContext.xml" };
    }    
    
    protected <T> void autoTestMessage(Class<T> msgClass) {
        checkResultsIgnoreAsymetric(validateMessage(msgClass));
    }

    protected <T> ValidationResult validateMessage(Class<T> msgClass) {
        ValidationResult result = new ValidationResult(msgClass);
        AutoInitializedClassValidator<T> validator = null;

        try {
            validator = (AutoInitializedClassValidator<T>) validatorSvc.findValidator(msgClass);
        }
        catch (Exception e) {}

        if (validator == null) {
            result.addError("Unable to find Auto Validator for message '" + msgClass + "'");
            return result;
        }

        if (validator.isAbstract()) {
            result.addComments("Validator is abstract and therefore can not be auto serialized");
            return result;
        }

        try {
            for (int i = 0; i < 10; i++) {

                T expected = validator.getDefaultObject(generator);

                SerializationResult encodingResult = messageFactory.encodeMessage(expected);
                if (!encodingResult.isValid()) {
                    result.addError("Serialization error occurred : " +
                                    ValidationHelper.getCauseTrace(encodingResult.getException()));
                    return result;
                }

                SerializationResult decodingResult =
                    messageFactory.decodeMessage(encodingResult.getMessageType(), encodingResult.getMessagePayload());
                if (!decodingResult.isValid()) {
                    result.addError("Deserialization error: " +
                                    ValidationHelper.getCauseTrace(encodingResult.getException()));
                    return result;
                }

                result = validator.validate(msgClass.cast(decodingResult.getMessageObject()), expected);
            }
        }
        catch (Exception e) {
            result.addError("Unexpected error: " + ValidationHelper.getCauseTrace(e));
        }

        return result;
    }

    public void testAllMessages() {
        for (Validator<?> validator : validatorList) {
            if (validator instanceof AutoInitializedClassValidator<?>) {
                checkResultsIgnoreAsymetric(validateMessage(validator.getValidatedClass()));
            }
        }
    }

    protected void checkResultsIgnoreAsymetric(ValidationResult result) {
        if (result.hasError()) {
            // If this message does not support serialization thus it can not be tested with auto
            // serialization. Therefore we don't fail the test
            if (!result.getErrors().get(0)
                .endsWith("UnsupportedOperationException (Message serialization not supported)")) {
                Assert.fail(ValidationHelper.formatErrorString(result));
            }
        }
    }
}
