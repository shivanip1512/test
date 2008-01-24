package com.cannontech.web.input.validate;

import org.springframework.validation.Errors;

/**
 * InputValidator implementation which validates that an integer is in a given
 * range
 */
public class IntegerRangeValidator implements InputValidator<Integer> {

    private int minValue = Integer.MIN_VALUE;
    private int maxValue = Integer.MAX_VALUE;

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public String getDescription() {
        return "(" + minValue + " - " + maxValue + ")";
    }

    public void validate(String path, String displayName, Integer value, Errors errors) {

        if (value == null) {
            return;
        }

        if (value < minValue) {
            errors.rejectValue(path,
                               "yukon.web.input.error.belowMin",
                               new Object[] { displayName, minValue },
                               "The input value must be greater than or equal to " + minValue);
        }
        if (value > maxValue) {
            errors.rejectValue(path,
                               "yukon.web.input.error.aboveMax",
                               new Object[] { displayName, maxValue },
                               "The input value must be less than or equal to " + maxValue);
        }

    }

}
