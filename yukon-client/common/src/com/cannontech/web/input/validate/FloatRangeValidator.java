package com.cannontech.web.input.validate;

import java.text.DecimalFormat;

import org.springframework.validation.Errors;

/**
 * InputValidator implementation which validates that a float is in a given
 * range
 */
public class FloatRangeValidator implements InputValidator<Float> {
    private float minValue = Float.MIN_VALUE;
    private float maxValue = Float.MAX_VALUE;

    private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.####");

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    @Override
    public String getDescription() {
        return "(" + DECIMAL_FORMAT.format(minValue) + " - " + maxValue + ")";
    }

    @Override
    public void validate(String path, String displayName, Float value, Errors errors) {
        if (value == null) {
            return;
        }

        if (value < minValue) {
            errors.rejectValue(path,
                               "yukon.web.input.error.outOfRangeFloat",
                               new Object[] { displayName, minValue, maxValue },
                               "The input value must be greater than or equal to " + minValue);
        }
        if (value > maxValue) {
            errors.rejectValue(path,
                               "yukon.web.input.error.outOfRangeFloat",
                               new Object[] { displayName, minValue, maxValue },
                               "The input value must be less than or equal to " + maxValue);
        }
    }
}
