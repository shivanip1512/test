package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;

/**
 * Implementation of input type which represents the url of and image on the server.
 * Renderer uses jquery file upload plugin for edit mode.
 */
public class ImageType extends DefaultValidatedType<String> {

    private String category = "logos";
    private String renderer = "imageType.jsp";
    
    public ImageType() {}
    
    public ImageType(String category) {
        this.category = category;
    }

    @Override
    public String getRenderer() {
        return renderer;
    }
    
    public String getCategory() {
        return category;
    }

    @Override
    public Class<String> getTypeClass() {
        return String.class;
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return new StringTrimmerEditor(true);
    }
}
