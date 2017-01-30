package com.cannontech.multispeak.client.core.v5;

import java.util.List;

import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface IMDMClient {
    /**
     * Pings the URL.
     * 
     * @param String the URI of the MDM Server
     */
    public void pingURL(MultispeakVendor mspVendor, String uri) throws MultispeakWebServiceClientException;

    /**
     * Get all the supported methods.
     * 
     * @param String the URI of the MDM Server
     * @return List<String> - List of methods
     */
    public List<String> getMethods(MultispeakVendor mspVendor, String uri) throws MultispeakWebServiceClientException;

}
