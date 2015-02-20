package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import org.springframework.beans.propertyeditors.CustomNumberEditor;

/**
 * Implementation of input type which represents a float input type
 */
public class FloatType extends DefaultValidatedType<Float> {

    private float minValue = -Float.MAX_VALUE;
    private float maxValue = Float.MAX_VALUE;

    private String renderer = null;

    public FloatType() {
        // There's no difference between how float and integer are represented, so we can use integerType.jsp here.
        setRenderer("integerType.jsp");
    }

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

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

    public Class<Float> getTypeClass() {
        return Float.class;
    }

    public PropertyEditor getPropertyEditor() {
        return new CustomNumberEditor(Float.class, true);
    }

}