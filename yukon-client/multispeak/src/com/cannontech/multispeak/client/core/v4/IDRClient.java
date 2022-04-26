package com.cannontech.multispeak.client.core.v4;

import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface IDRClient {

    /**
     * Pings the URL. 
     * @param String the URI of the DR Server
     * @param MultispeakVendor used as input.
     * @return PingURLResponse
     */
    public PingURLResponse pingURL(MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException;

    /**
     * Get all the supported methods.
     * 
     * @param String the URI of the DR Server
     */
    public GetMethodsResponse getMethods(final MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException;

}