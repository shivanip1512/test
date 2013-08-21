package com.cannontech.messaging.serialization.thrift.test.validator;

import java.lang.reflect.Field;

public class ValidatableField {

    private final Field field;
    private Validator<?> fieldValidator;
    private boolean ignore = false;

    public ValidatableField(Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }

    public ValidationResult validateField(Object valueTarget, Object controlTarget, ValidatorService validatorSvc) {
        ValidationResult result = new ValidationResult("field '" + field.getName() + "'", getFieldClass());

        if (ignore) {
            result.addComments("Field ignored");
            return result;
        }

        Object expected;
        Object actual;

        try {
            expected = field.get(controlTarget);
            actual = field.get(valueTarget);
        }
        catch (Exception e) {
            result.addError("impossible to get the field value [" + e + "]");
            return result;
        }
        
       Validator<?> validator = fieldValidator;

        if (validator == null) {
             validator = validatorSvc.getValidator(actual);
            if (validator == null) {
                result.addError("Unable to find appropriate validator");
                return result;
            }
        }

        result.addNestedResults(((Validator) validator).validate(actual, expected));

        return result;
    }

    public Class<?> getFieldClass() {
        return field.getType();
    }

    public Validator<?> getValidator() {
        return fieldValidator;
    }

    public void setValidator(Validator<?> validator) {
        this.fieldValidator = validator;
    }

    public String getName() {
        return field.getDeclaringClass().getSimpleName() + "." + getField().getName();
    }

    public Field getField() {
        return field;
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }
}
