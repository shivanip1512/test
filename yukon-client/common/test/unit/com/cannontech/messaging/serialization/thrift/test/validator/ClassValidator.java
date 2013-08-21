package com.cannontech.messaging.serialization.thrift.test.validator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ClassValidator<T> extends Validator<T> {

    private List<ValidatableField> validatableFields;

    protected ClassValidator(Class<T> clazz) {
        this(clazz, null);
    }

    protected ClassValidator(Class<T> clazz, ValidatorService validatorSvc) {
        super(clazz, validatorSvc);
        validatableFields = createValidatableFields();
    }

    private static <C> List<Field> getDeclaredInstanceFields(Class<C> clazz) {
        List<Field> fields = new ArrayList<Field>();

        for (Field f : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) {
                fields.add(f);
            }
        }
        return fields;
    }

    protected List<ValidatableField> createValidatableFields() {
        List<ValidatableField> validatables = new ArrayList<ValidatableField>();

        for (Field f : getDeclaredInstanceFields(getValidatedClass())) {
            validatables.add(new ValidatableField(f));
        }

        return validatables;
    }

    protected ValidatableField getValidatableField(String fieldName) {
        if (fieldName == null) {
            throw new IllegalArgumentException("FieldName argument can not be null");
        }

        for (ValidatableField fv : validatableFields) {
            if (fv.getField().getName().equals(fieldName)) {
                return fv;
            }
        }

        throw new IllegalArgumentException("Field '" + fieldName + "' could not be found");
    }

    protected ValidatableField getValidatableField(Field field) {
        if (field == null) {
            throw new IllegalArgumentException("Field argument can not be null");
        }

        for (ValidatableField fv : validatableFields) {
            if (fv.getField().equals(field)) {
                return fv;
            }
        }

        throw new IllegalArgumentException("Field '" + field.getName() + "' could not be found");
    }

    public void setValidatorForField(String fieldName, Validator<?> validator) {
        getValidatableField(fieldName).setValidator(validator);
    }

    public void setValidatorForField(Field field, Validator<?> validator) {
        getValidatableField(field).setValidator(validator);
    }

    public void ignoreField(String fieldName) {
        getValidatableField(fieldName).setIgnore(true);
    }

    public void ignoreField(Field field) {
        getValidatableField(field).setIgnore(true);
    }

    public ClassValidator<? super T> getParentValidator() {
        ClassValidator<? super T> parentValidator = null;
        Class<?> parentClass = getValidatedClass().getSuperclass();

        if (parentClass != null && parentClass != Object.class) {
            Validator<?> validator = getValidatorSvc().getValidator(parentClass);

            if (validator == null || !(validator instanceof ClassValidator<?>)) {
                throw new RuntimeException("Unable to find parent validator of type '" + parentClass.getSimpleName() +
                                           "'");
            }

            parentValidator = (ClassValidator<? super T>) validator;
        }

        return parentValidator;
    }

    @Override
    public ValidationResult validate(T value, T expected) {
        ValidationResult result = new ValidationResult(getValidatedClass());

        if (isBasicEqualityCheckConclusive(value, expected, result)) {
            return result;
        }
     
        Validator<? super T> parentValidator = null;
        try {
            parentValidator = getParentValidator();
        }
        catch (Exception e) {
            result.addError(e.getMessage());
        }

        if (parentValidator != null) {
            result.addNestedResults(parentValidator.validate(value, expected));
        }

        for (ValidatableField fv : validatableFields) {
            result.addNestedResults(fv.validateField(value, expected, getValidatorSvc()));
        }

        return result;
    }
}
