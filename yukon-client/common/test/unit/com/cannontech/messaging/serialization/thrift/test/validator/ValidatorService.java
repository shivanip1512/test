package com.cannontech.messaging.serialization.thrift.test.validator;

public interface ValidatorService {

    void registerValidator(Validator<?> validator);

    <C> Validator<C> findValidator(Class<C> validatedClass);

    Validator<?> findValidator(Object validatedObject);

    <C> Validator<C> getValidator(Class<C> validatedClass);

    Validator getValidator(Object validatedObject);

    <C> Validator<C> getDefaultValidator(Class<C> validatedClass);

    Validator getDefaultValidator(Object validatedObject);

    ValidationResult validate(Object value, Object expected);
}
