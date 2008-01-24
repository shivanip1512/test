package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import sun.beans.editors.StringEditor;

/**
 * Implementation of input type which represents a string input type
 */
public class StringType extends DefaultValidatedType<String> {

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

    public PropertyEditor getPropertyEditor() {
        return new StringEditor();
    }

}
