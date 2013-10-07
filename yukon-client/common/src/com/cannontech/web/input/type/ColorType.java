package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import com.sun.beans.editors.StringEditor;

/**
 * Implementation of input type which represents a css valid color ie: #0066CC or red or rgb(32, 34, 59).
 * Renderer uses spectrum color picker.
 */
public class ColorType extends DefaultValidatedType<String> {

    private String renderer = "colorType.jsp";

    public String getRenderer() {
        return renderer;
    }

    public Class<String> getTypeClass() {
        return String.class;
    }

    public PropertyEditor getPropertyEditor() {
        return new StringEditor();
    }

}
