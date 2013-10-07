package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import org.springframework.beans.propertyeditors.CustomNumberEditor;

/**
 * Implementation of input type which represents an amount of pixels ie: 35px.
 */
public class PixelType extends DefaultValidatedType<Integer> {

    private int minValue = 0;
    private int maxValue = 0;

    private String renderer = null;

    public PixelType() {
        setRenderer("pixelType.jsp");
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
