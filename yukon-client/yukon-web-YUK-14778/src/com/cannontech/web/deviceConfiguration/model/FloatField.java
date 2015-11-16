package com.cannontech.web.deviceConfiguration.model;

import com.cannontech.web.input.type.FloatType;
import com.cannontech.web.input.validate.FloatRangeValidator;

public class FloatField extends Field<Float> {
    private final int decimalDigits;
    private final boolean digitsLimited;
    
    public FloatField(String displayName, String fieldName, String description, int decimalDigits, FloatType inputType,
            String defaultValue) {
        super(displayName, fieldName, description, inputType, defaultValue,
              createValidator(inputType.getMinValue(), inputType.getMaxValue()));
        this.decimalDigits = decimalDigits;
        this.digitsLimited = true;
    }
    
    public FloatField(String displayName, String fieldName, String description, FloatType inputType, String defaultValue) {
        super(displayName, fieldName, description, inputType, defaultValue,
              createValidator(inputType.getMinValue(), inputType.getMaxValue()));
        this.decimalDigits = Integer.MAX_VALUE;
        this.digitsLimited = false;
    }
    
    private static FloatRangeValidator createValidator(float minValue, float maxValue) {
        FloatRangeValidator validator = new FloatRangeValidator();
        validator.setMinValue(minValue);
        validator.setMaxValue(maxValue);
        
        return validator;
    }
    
    public int getDecimalDigits() {
        return decimalDigits;
    }

    public boolean isDigitsLimited() {
        return digitsLimited;
    }
}
