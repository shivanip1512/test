package com.cannontech.common.bulk.processor;

import java.util.Collection;

/**
 * Interface which processes an object
 * @param <T> - Type of object to process
 */
public interface Processor<T> {

    /**
     * Method to process an object
     * @param object - Object to process
     */
    public void process(T object) throws ProcessingException;

    /**
     * Method to process a list of objects
     * @param objectCollection - Collection of objects to process
     */
    public void process(Collection<T> objectCollection) throws ProcessingException;
}
