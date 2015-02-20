package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import org.springframework.beans.propertyeditors.CustomNumberEditor;

public class LongType  extends DefaultValidatedType<Long> {

    private long minValue = Long.MIN_VALUE;
    private long maxValue = Long.MAX_VALUE;

    private String renderer = null;

    public LongType() {
        setRenderer("integerType.jsp");
    }

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public long getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public long getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public Class<Long> getTypeClass() {
        return Long.class;
    }

    public PropertyEditor getPropertyEditor() {
        return new CustomNumberEditor(Long.class, true);
    }

}
