package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import org.springframework.beans.propertyeditors.CustomBooleanEditor;

import com.cannontech.web.input.validate.InputValidator;
import com.cannontech.web.input.validate.NullValidator;

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

    public InputValidator<Boolean> getValidator() {
        return NullValidator.getInstance();
    }

    public PropertyEditor getPropertyEditor() {
        return new CustomBooleanEditor(true);
    }

}
