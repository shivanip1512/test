package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;

/**
 * Implementation of input type which represents a css valid color ie: #0066CC or red or rgb(32, 34, 59).
 * Renderer uses spectrum color picker.
 */
public class ColorType extends DefaultValidatedType<String> {

    private String renderer = "colorType.jsp";

    @Override
    public String getRenderer() {
        return renderer;
    }

    @Override
    public Class<String> getTypeClass() {
        return String.class;
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return new StringTrimmerEditor(false);
    }

}
