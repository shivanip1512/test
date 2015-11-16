package com.cannontech.web.deviceConfiguration.model;

import com.cannontech.web.input.type.IntegerType;
import com.cannontech.web.input.validate.IntegerRangeValidator;

public class IntegerField extends Field<Integer> {
    public IntegerField(String displayName, String fieldName, String description, IntegerType inputType,
            String defaultValue) {
        super(displayName, fieldName, description, inputType, defaultValue,
              createValidator(inputType.getMinValue(), inputType.getMaxValue()));
    }
    
    private static IntegerRangeValidator createValidator(int minValue, int maxValue) {
        IntegerRangeValidator validator = new IntegerRangeValidator();

        if (minValue != maxValue) {
            validator.setMinValue(minValue);
            validator.setMaxValue(maxValue);
        }
        
        return validator;
    }
}
