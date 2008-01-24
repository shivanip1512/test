package com.cannontech.web.input;

import com.cannontech.web.input.type.InputType;

/**
 * Interface which extends input and represents a single input
 */
public interface InputSource<T> extends Input<T> {

    /**
     * Method to get the description of the field for this input
     * @return - Field description
     */
    public String getDescription();

    /**
     * Method to get the input type for this input
     * @return - Input type
     */
    public InputType<T> getType();

}
