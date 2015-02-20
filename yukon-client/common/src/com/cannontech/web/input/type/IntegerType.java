package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import org.springframework.beans.propertyeditors.CustomNumberEditor;

/**
 * Implementation of input type which represents an integer input type
 */
public class IntegerType extends DefaultValidatedType<Integer> {

    private int minValue = Integer.MIN_VALUE;
    private int maxValue = Integer.MAX_VALUE;

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

    public PropertyEditor getPropertyEditor() {
        return new CustomNumberEditor(Integer.class, true);
    }

}