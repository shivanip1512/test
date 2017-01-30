package com.cannontech.multispeak.client.core.v5;

import java.util.List;

import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface IOAClient {

    /**
     * Pings the URL.
     *
     * @param Multispeak vendor
     * @param String the URI of the OA Server
     */
    void pingURL(MultispeakVendor mspVendor, String uri) throws MultispeakWebServiceClientException;

    /**
     * Get all the supported methods.
     * 
     * @param Multispeak vendor
     * @param String the URI of the OA Server
     * @return List of methods
     */
    List<String> getMethods(MultispeakVendor mspVendor, String uri) throws MultispeakWebServiceClientException;

}
