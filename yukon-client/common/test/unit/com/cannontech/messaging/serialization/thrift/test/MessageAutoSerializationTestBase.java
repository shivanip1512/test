package com.cannontech.messaging.serialization.thrift.test;

import com.cannontech.messaging.serialization.SerializationResult;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;
import com.cannontech.messaging.serialization.thrift.test.validator.ValidationResult;
import com.cannontech.messaging.serialization.thrift.test.validator.Validator;

public abstract class MessageAutoSerializationTestBase extends MessageSerializationTestBase {
    private RandomGenerator generator;

    protected MessageAutoSerializationTestBase() {
        generator = new RandomGenerator(System.currentTimeMillis());
    }

    protected <T> void testMessage(Class<T> msgClass) {
        checkResults(validateMessage(msgClass));
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
                    result.addError("Serialization error occured : " +
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
        for (Validator<?> validator : globalValidatorList) {
            if (validator instanceof AutoInitializedClassValidator<?>) {
                ValidationResult result = validateMessage(validator.getValidatedClass());

                if (result.hasError()) {
                    if (result.getErrors().get(0)
                        .endsWith("UnsupportedOperationException (Message serialization not supported)")) {
                        // This message does not support serialization thus it can not be tested with auto
                        // serialization. Therefore we don't fail the test
                    }
                    else {
                        checkResults(result);
                    }
                }
            }
        }
    }
}
