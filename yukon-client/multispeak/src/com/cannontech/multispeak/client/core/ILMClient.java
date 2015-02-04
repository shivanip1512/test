package com.cannontech.multispeak.client.core;

import com.cannontech.msp.beans.v3.GetAllSubstationLoadControlStatuses;
import com.cannontech.msp.beans.v3.GetAllSubstationLoadControlStatusesResponse;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface ILMClient {
    /**
     * Pings the URL.
     * 
     * @param String the URI of the LM Server
     * @param PingURL the PingURL used as input.
     * @return PingURLResponse
     */
    public PingURLResponse pingURL(MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException;

    /**
     * Get all the supported methods.
     * 
     * @param String the URI of the LM Server
     * @param GetMethods the GetMethods used as input.
     * @return GetMethodsResponse
     */
    public GetMethodsResponse getMethods(MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException;

    /**
     * Get All Substation Load Control Statuses
     * 
     * @param mspVendor
     * @param uri
     * @param getAllSubstationLoadControlStatuses
     * @return
     * @throws MultispeakWebServiceClientException
     */
    GetAllSubstationLoadControlStatusesResponse getAllSubstationLoadControlStatuses(MultispeakVendor mspVendor,
            String uri, GetAllSubstationLoadControlStatuses getAllSubstationLoadControlStatuses)
            throws MultispeakWebServiceClientException;
}
