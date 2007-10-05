package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import org.springframework.beans.propertyeditors.CustomBooleanEditor;

import com.cannontech.web.input.validate.DefaultValidator;
import com.cannontech.web.input.validate.InputValidator;

/**
 * Implementation of input type which represents a boolean input type.
 */
public class BooleanType implements InputType<Boolean> {

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

    public InputValidator getValidator() {
        return DefaultValidator.getInstance();
    }

    public PropertyEditor getPropertyEditor() {
        return new CustomBooleanEditor(true);
    }

}
