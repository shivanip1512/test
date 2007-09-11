package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.Errors;

import com.cannontech.web.input.InputSource;
import com.cannontech.web.input.validate.InputValidator;

/**
 * Implementation of input type which represents a string input type
 */
public class IntegerType {

    private int minValue = 0;
    private int maxValue = 0;

    private String renderer = null;

    public IntegerType() {
        setRenderer("integerType.jsp");
    }

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

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

    public Class<Integer> getTypeClass() {
        return Integer.class;
    }

    public InputValidator<Integer> getValidator() {

        return new InputValidator<Integer>() {

            public void validate(String path, InputSource field, Integer value, Errors errors) {

                if (value == null || minValue == maxValue) {
                    return;
                }

                if (value < minValue) {
                    errors.rejectValue(path,
                                       "error.belowMin",
                                       new Object[] { field.getDisplayName(), minValue },
                                       "The input value must be greater than or equal to " + minValue);
                }
                if (value > maxValue) {
                    errors.rejectValue(path,
                                       "error.aboveMax",
                                       new Object[] { field.getDisplayName(), maxValue },
                                       "The input value must be less than or equal to " + maxValue);
                }

            }
        };
    }

    public PropertyEditor getPropertyEditor() {
        return new CustomNumberEditor(Integer.class, true);
    }

}
