package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import org.springframework.beans.propertyeditors.CustomNumberEditor;

/**
 * Implementation of input type which represents an amount of pixels ie: 35px.
 */
public class PixelType extends DefaultValidatedType<Integer> {

    private int min = 0;
    private int max = 0;
    private String renderer = "pixelType.jsp";

    public PixelType() {}
    
    public PixelType(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public int getMin() {
        return min;
    }
    
    public void setMin(int min) {
        this.min = min;
    }
    
    public int getMax() {
        return max;
    }
    
    public void setMax(int max) {
        this.max = max;
    }

    public Class<Integer> getTypeClass() {
        return Integer.class;
    }

    public PropertyEditor getPropertyEditor() {
        return new CustomNumberEditor(Integer.class, true);
    }

}