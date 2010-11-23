package com.cannontech.web.common.collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;

/**
 * Collection Factory to create collections of type T from the given request.
 * @param <T> Type of collection to create
 */
public interface CollectionFactory<T> {

    /**
     * Method to create the required collection of type T based on request
     * parameters
     * @param request - Current request
     * @return The required collection
     * @throws ServletRequestBindingException, CollectionCreationException
     */
    public T createCollection(HttpServletRequest request) throws ServletRequestBindingException, CollectionCreationException;
    
}