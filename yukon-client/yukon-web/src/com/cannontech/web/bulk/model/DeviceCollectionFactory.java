package com.cannontech.web.bulk.model;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.common.bulk.collection.DeviceCollection;

/**
 * Factory used to create the required device collection based on request
 * parameters
 */
public interface DeviceCollectionFactory {

    /**
     * Method to create the required device collection based on request
     * parameters
     * @param request - Current request
     * @return The required device collection
     * @throws ServletRequestBindingException, DeviceCollectionCreationException
     */
    public DeviceCollection createDeviceCollection(HttpServletRequest request)
            throws ServletRequestBindingException, DeviceCollectionCreationException;

}