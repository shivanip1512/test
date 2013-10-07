package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import com.sun.beans.editors.StringEditor;

/**
 * Implementation of input type which represents the url of and image on the server.
 * Renderer uses jquery file upload plugin for edit mode.
 */
public class ImageType extends DefaultValidatedType<String> {

    private String renderer = "imageType.jsp";

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
