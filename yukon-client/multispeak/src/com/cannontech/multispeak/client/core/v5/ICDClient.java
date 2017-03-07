package com.cannontech.multispeak.client.core.v5;

import java.util.List;

import com.cannontech.msp.beans.v5.cd_server.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface ICDClient {

    /**
     * Pings the URL. 
     * @param String the URI of the CD Server
     * @param MultispeakVendor used as input.
     * @return PingURLResponse
     */
    public PingURLResponse pingURL(MultispeakVendor mspVendor, String uri)
            throws MultispeakWebServiceClientException;

    /**
     * Get all the supported methods.
     * @param MultispeakVendor used as input.
     * @param String the URI of the CD Server
     */
    public List<String> getMethods(MultispeakVendor mspVendor, String uri)
            throws MultispeakWebServiceClientException;

}
