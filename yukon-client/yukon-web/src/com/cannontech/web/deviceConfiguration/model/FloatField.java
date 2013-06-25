package com.cannontech.web.deviceConfiguration.model;

import com.cannontech.web.input.type.FloatType;
import com.cannontech.web.input.validate.FloatRangeValidator;

public class FloatField extends Field<Float> {
    public FloatField(String displayName, String fieldName, String description, FloatType inputType) {
        super(displayName, fieldName, description, inputType, 
              createValidator(inputType.getMinValue(), inputType.getMaxValue()));
    }
    
    private static FloatRangeValidator createValidator(float minValue, float maxValue) {
        FloatRangeValidator validator = new FloatRangeValidator();
        validator.setMinValue(minValue);
        validator.setMaxValue(maxValue);
        
        return validator;
    }
}
