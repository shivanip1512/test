package com.cannontech.web.deviceConfiguration.model;

import com.cannontech.web.input.type.InputType;
import com.cannontech.web.input.validate.InputValidator;

public class Field<T> {
    private final String displayName;
    private final String fieldName;
    private final String description;
    private final boolean isEnum;
    private final InputType<T> inputType;
    private final InputValidator<T> validator;
    private final String defaultValue;

    protected Field(String displayName, String fieldName, String description, InputType<T> inputType,
            String defaultValue, InputValidator<T> validator) {
        this(displayName, fieldName, description, inputType, defaultValue, false, validator);
    }

    protected Field(String displayName, String fieldName, String description, InputType<T> inputType,
            String defaultValue, boolean isEnum, InputValidator<T> validator) {
        this.displayName = displayName;
        this.fieldName = fieldName;
        this.description = description;
        this.inputType = inputType;
        this.isEnum = isEnum;
        this.defaultValue = defaultValue;
        this.validator = validator;
    }

    public static <T> Field<T> createField(String displayName, String fieldName, String description,
            InputType<T> inputType, String defaultValue) {
        return new Field<T>(displayName, fieldName, description, inputType, defaultValue, null);
    }

    public static <T> Field<T> createEnumField(String displayName, String fieldName, String description,
            InputType<T> inputType, String defaultValue) {
        return new Field<T>(displayName, fieldName, description, inputType, defaultValue, true, null);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFieldName() {
        return fieldName;
    }
    
    public String getDescription() {
        return description;
    }

    public InputType<T> getInputType() {
        return inputType;
    }
    
    public boolean isEnum() {
        return isEnum;
    }
    
    public InputValidator<T> getValidator() {
        return validator;
    }

    public String getDefault() {
        return defaultValue;
    }
}
