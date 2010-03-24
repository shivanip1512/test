package com.cannontech.common.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public abstract class SimpleValidator<T> implements Validator {

    private final Class<T> objectType;

    public SimpleValidator(Class<T> objectType) {
        this.objectType = objectType;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
        return clazz.isAssignableFrom(objectType);
    }

    @Override
    public void validate(Object target, Errors errors) {
        T t = objectType.cast(target);
        doValidation(t, errors);
    }
    
    protected abstract void doValidation(T target, Errors errors);
}

