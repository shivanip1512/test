package com.cannontech.multispeak.client.core;

import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.InitiateOutageDetectionEventRequest;
import com.cannontech.msp.beans.v3.InitiateOutageDetectionEventRequestResponse;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
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

    /**
     * 
     * initiate Outage Detection Event Request
     * 
     * @param mspVendor
     * @param uri
     * @param initiateOutageDetectionEventRequest
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public InitiateOutageDetectionEventRequestResponse initiateOutageDetectionEventRequest(MultispeakVendor mspVendor,
            String uri, InitiateOutageDetectionEventRequest initiateOutageDetectionEventRequest)
            throws MultispeakWebServiceClientException;;
}
