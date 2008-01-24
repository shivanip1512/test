package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/**
 * Implementation of input type which represents a boolean input type.
 */
public class BooleanType extends DefaultValidatedType<Boolean> {

    private String renderer = "booleanType.jsp";

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public Class<Boolean> getTypeClass() {
        return Boolean.class;
    }

    public PropertyEditor getPropertyEditor() {
        return new CustomBooleanEditor(true);
    }

}
