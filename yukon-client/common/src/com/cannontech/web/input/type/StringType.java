package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import sun.beans.editors.StringEditor;

import com.cannontech.web.input.validate.DefaultValidator;
import com.cannontech.web.input.validate.InputValidator;

/**
 * Implementation of input type which represents a string input type
 */
public class StringType implements InputType<String> {

    private String renderer = "stringType.jsp";

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public Class<String> getTypeClass() {
        return String.class;
    }

    public InputValidator getValidator() {
        return DefaultValidator.getInstance();
    }

    public PropertyEditor getPropertyEditor() {
        return new StringEditor();
    }

}
