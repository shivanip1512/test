package com.cannontech.multispeak.client.core.v5;


import com.cannontech.msp.beans.v5.od_server.GetMethods;
import com.cannontech.msp.beans.v5.od_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.od_server.PingURL;
import com.cannontech.msp.beans.v5.od_server.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface IODClient {

    /**
     * Pings the URL.
     * 
     * @param String the URI of the MDM Server
     * @param PingURL the PingURL used as input.
     * @return PingURLResponse
     */
    public PingURLResponse pingURL(MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException;

    /**
     * Get all the supported methods.
     * 
     * @param String the URI of the MDM Server
     * @param GetMethods the GetMethods used as input.
     * @return GetMethodsResponse
     */
    public GetMethodsResponse getMethods(MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException;
}

