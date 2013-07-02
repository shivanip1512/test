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

    public Field(String displayName, String fieldName, String description, InputType<T> inputType) {
        this(displayName, fieldName, description, inputType, false, null);
    }

    public Field(String displayName, String fieldName, String description, boolean isEnum, InputType<T> inputType) {
        this(displayName, fieldName, description, inputType, isEnum, null);
    }

    protected Field(String displayName, String fieldName, String description, InputType<T> inputType,
                    InputValidator<T> validator) {
        this(displayName, fieldName, description, inputType, false, validator);
    }
    
    protected Field(String displayName, String fieldName, String description, InputType<T> inputType,
                    boolean isEnum, InputValidator<T> validator) {
        this.displayName = displayName;
        this.fieldName = fieldName;
        this.description = description;
        this.inputType = inputType;
        this.isEnum = isEnum;
        this.validator = validator;
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
}
