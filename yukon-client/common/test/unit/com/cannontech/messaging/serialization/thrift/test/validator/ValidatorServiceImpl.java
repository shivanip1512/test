package com.cannontech.messaging.serialization.thrift.test.validator;

import java.util.HashMap;
import java.util.Map;

public class ValidatorServiceImpl implements ValidatorService {

    private Map<Class<?>, Validator<?>> registeredValidators;
    private Map<Class<?>, Validator<?>> cachedValidators;

    public ValidatorServiceImpl() {
        registeredValidators = new HashMap<Class<?>, Validator<?>>();
        cachedValidators = new HashMap<Class<?>, Validator<?>>();
    }

    @Override
    public void registerValidator(Validator<?> validator) {
        registeredValidators.put(validator.getValidatedClass(), validator);
    }

    @Override
    public Validator findValidator(Object validatedObject) {
        if (validatedObject == null) {
            return null;
        }
        return findValidator(validatedObject.getClass());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> Validator<C> findValidator(Class<C> validatedClass) {
        return (Validator<C>) registeredValidators.get(validatedClass);
    }

    @Override
    public Validator getValidator(Object validatedObject) {
    	Class<?> validatedClass = validatedObject == null ? Object.class : validatedObject.getClass();
        return getValidator(validatedClass);
    }

    @Override
    public <C> Validator<C> getValidator(Class<C> validatedClass) {
        Validator<C> validator = findValidator(validatedClass);
        if (validator == null) {
            validator = getDefaultValidator(validatedClass);
        }
        return validator;
    }

    @Override
    public Validator getDefaultValidator(Object validatedObject) {
        return getDefaultValidator(validatedObject == null ? Object.class : validatedObject.getClass());
    }

    @Override
    public <C> Validator<C> getDefaultValidator(Class<C> clazz) {
        Validator<C> validator = (Validator<C>) cachedValidators.get(clazz);
        if (validator != null) {
            return validator;
        }

        if (ValidationUtil.isClassValidatorCandidate(clazz)) {
            validator = new ClassValidator<C>(clazz, this);
        }
        else {
            validator = new DefaultValidator<C>(clazz, this);
        }

        cachedValidators.put(validator.getValidatedClass(), validator);
        return validator;
    }

    @Override
    public ValidationResult validate(Object value, Object expected) {
        Validator validator = getValidator(value == null ? expected : value);

        return validator.validate(value, expected);
    }
}
