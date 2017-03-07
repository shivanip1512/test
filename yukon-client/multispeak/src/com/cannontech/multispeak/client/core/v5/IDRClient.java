package com.cannontech.multispeak.client.core.v5;

import java.util.List;

import com.cannontech.msp.beans.v5.dr_server.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface IDRClient {
    /**
     * Pings the URL. 
     * @param String the URI of the DR Server
     * @param MultispeakVendor used as input.
     * @return PingURLResponse
     */
    public PingURLResponse pingURL(MultispeakVendor mspVendor, String uri)
            throws MultispeakWebServiceClientException;

    /**
     * Get all the supported methods.
     * 
     * @param String the URI of the DR Server
     */
    public List<String> getMethods(MultispeakVendor mspVendor, String uri)
            throws MultispeakWebServiceClientException;

}
