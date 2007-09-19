package com.cannontech.web.input;

import java.util.List;
import java.util.Map;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;

/**
 * Interface which represents an input to be used to populate a field
 */
public interface Input {

    /**
     * Method to get the name of the field this input will populate
     * @return - Field name
     */
    public String getField();

    /**
     * Method used to get the renderer that will be used to render this input
     * @return - String representation of renderer
     */
    public String getRenderer();

    /**
     * Method used to register the property editor for this input
     * @param binder - Spring binder that the editor will be registered with
     */
    public void doRegisterEditor(ServletRequestDataBinder binder);

    /**
     * Method used to validate the value assigned to this input's field
     * @param path - Path to the field on the command object
     * @param command - Object that contains the field
     * @param errors - Spring errors object used if validation fails
     */
    public void doValidate(String path, Object command, BindException errors);

    /**
     * Method used to get the list of inputs that make up this input (useful for
     * input groups - which are inputs themselves)
     * @return - List of inputs contained in this input
     */
    public List<InputSource> getInputList();
    
    public Map<String, ? extends InputSource> getInputMap(String prefix);

}
