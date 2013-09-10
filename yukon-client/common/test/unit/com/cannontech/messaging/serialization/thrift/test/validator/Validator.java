package com.cannontech.messaging.serialization.thrift.test.validator;

public abstract class Validator<T> {
    private Class<T> clazz;
    private ValidatorService validatorSvc;
    private boolean trustEqual;

    protected Validator(Class<T> clazz) {
        this(clazz, null);
    }

    protected Validator(Class<T> clazz, ValidatorService validatorSvc) {
        this.clazz = clazz;
        this.validatorSvc = validatorSvc;
        this.trustEqual = false;
    }

    public Class<T> getValidatedClass() {
        return clazz;
    }

    public ValidatorService getValidatorSvc() {
        return validatorSvc;
    }

    public void setValidatorSvc(ValidatorService validatorSvc) {
        this.validatorSvc = validatorSvc;
    }

    public abstract ValidationResult validate(T value, T expected);

    protected boolean isBasicEqualityCheckConclusive(Object value, Object expected, ValidationResult result) {
        // Same ref or both null
        if (value == expected) {
            result.addComments("Object reference are equal");
            return true;
        }

        // Only one is null
        if (value == null) {
            result.addError("value object is null while expected object is not");
            return true;
        }
        
        if (expected == null) {
            result.addError("expected object is null while value object is not");
            return true;
        }

        // Same class validation
        if (value.getClass() != expected.getClass()) {
            result.addError("Objects are not of the same class: expected class (" + expected.getClass().getName() +
                            "), actual class(" + value.getClass().getName() + ")");
            return true;
        }

        if (ValidationUtil.isPrimitive(value.getClass()) || trustEqual) {
            if (value.equals(expected)) {
                result.addComments("Object are said equal by equal()");
                return true;
            }
        }

        return false;
    }
   

    public boolean trustEqual() {
        return trustEqual;
    }

    public void setTrustEqual(boolean trustEqual) {
        this.trustEqual = trustEqual;
    }

}
