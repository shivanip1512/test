package com.cannontech.web.input;

import java.util.List;
import java.util.Map;

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
     * Method used to get the list of inputs that make up this input (useful for
     * input groups - which are inputs themselves)
     * @return - List of inputs contained in this input
     */
    public List<InputSource> getInputList();

    /**
     * Method to get a map of <prefix + input-field-path, inputSource> for this
     * input
     * @param prefix - Prefix to append to the field path map key
     * @return This input's input map
     */
    public Map<String, ? extends InputSource> getInputMap(String prefix);

    /**
     * Method to get the security class for this input
     * @return Input security for this input
     */
    public InputSecurity getSecurity();

}
