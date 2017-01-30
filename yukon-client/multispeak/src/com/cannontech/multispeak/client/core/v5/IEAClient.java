package com.cannontech.multispeak.client.core.v5;

import java.util.List;

import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface IEAClient {

    /**
     * Get all the supported methods.
     * 
     * @param Multispeak vendor
     * @param String the URI of the EA Server
     * @return List of methods
     */
    public List<String> getMethods(MultispeakVendor mspVendor, String uri)
            throws MultispeakWebServiceClientException;

    /**
     * Pings the URL.
     * 
     * @param Multispeak vendor
     * @param String the URI of the EA Server
     */
    public void pingURL(MultispeakVendor mspVendor, String uri)
            throws MultispeakWebServiceClientException;

}
