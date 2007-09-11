package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import com.cannontech.web.input.validate.InputValidator;

/**
 * Interface which represents the type of input
 * @param <T> - The java type that this input type represents
 */
public interface InputType<T> {

    /**
     * Method used to get the renderer that will be used to render this input
     * type
     * @return - String representation of renderer
     */
    public String getRenderer();

    /**
     * Method used to get the validator that will be used to validate this input
     * @return - Validator
     */
    public InputValidator<T> getValidator();

    /**
     * Method used to get the property editor that will be used to change a
     * String value into the appropriate object for this input type
     * @return - Property editor
     */
    public PropertyEditor getPropertyEditor();

    /**
     * Method used to get the java type that this input represents
     * @return Input value class
     */
    public Class<T> getTypeClass();

}
