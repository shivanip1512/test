package com.cannontech.multispeak.client.core;

import com.cannontech.msp.beans.v3.GetAllSCADAAnalogs;
import com.cannontech.msp.beans.v3.GetAllSCADAAnalogsResponse;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface ISCADAClient {
    /**
     * Pings the URL.
     * 
     * @param String the URI of the SCADA Server
     * @param PingURL the PingURL used as input.
     * @return PingURLResponse
     */
    public PingURLResponse pingURL(MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException;

    /**
     * Get all the supported methods.
     * 
     * @param String the URI of the SCADA Server
     * @param GetMethods the GetMethods used as input.
     * @return GetMethodsResponse
     */
    public GetMethodsResponse getMethods(MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException;

    /**
     * 
     * get All SCADA Analogs
     * 
     * @param mspVendor
     * @param uri
     * @param getAllSCADAAnalogs
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public GetAllSCADAAnalogsResponse getAllSCADAAnalogs(MultispeakVendor mspVendor, String uri,
            GetAllSCADAAnalogs getAllSCADAAnalogs) throws MultispeakWebServiceClientException;
}
