package com.cannontech.common.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.cannontech.user.YukonUserContext;

public abstract class SimpleValidator<T> implements Validator {

    private final Class<T> objectType;
    private YukonUserContext yukonUserContext;

    public void setYukonUserContext(YukonUserContext yukonUserContext) {
        this.yukonUserContext = yukonUserContext;
    }

    public SimpleValidator(Class<T> objectType) {
        this.objectType = objectType;
    }

    @Override
    public boolean supports(Class clazz) {
        return objectType.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        T t = objectType.cast(target);
        if(yukonUserContext == null) {
            doValidation(t, errors); 
        }else {
            doValidation(t, errors, yukonUserContext);
        }
        
    }

    protected void doValidation(T target, Errors errors) {
        
    }

    protected void doValidation(T target, Errors errors, YukonUserContext yukonUserContext) {
    }
}
