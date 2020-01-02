package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import org.springframework.beans.propertyeditors.CustomNumberEditor;

/**
 * Implementation of input type which represents a Primary CIS Vendor input type
 */
public class PrimaryCisVendorType extends DefaultValidatedType<Integer>{
    
    private String renderer = "primaryCisVendorType.jsp";

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public Class<Integer> getTypeClass() {
        return Integer.class;
    }

    public PropertyEditor getPropertyEditor() {
        return new CustomNumberEditor(Integer.class, true);
    }
}
