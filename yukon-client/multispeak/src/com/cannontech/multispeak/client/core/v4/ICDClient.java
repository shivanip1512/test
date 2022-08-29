package com.cannontech.multispeak.client.core.v4;

import com.cannontech.msp.beans.v4.GetCDSupportedMeters;
import com.cannontech.msp.beans.v4.GetCDSupportedMetersResponse;
import com.cannontech.msp.beans.v4.GetCDMeterState;
import com.cannontech.msp.beans.v4.GetCDMeterStateResponse;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface ICDClient {

    /**
     * Pings the URL.
     * 
     * @param String the URI of the CD Server
     * @param PingURL the PingURL used as input.
     * @return PingURLResponse
     */
    public PingURLResponse pingURL(MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException;

    /**
     * Get all the supported methods.
     * 
     * @param String the URI of the CD Server
     * @param GetMethods the GetMethods used as input.
     * @return GetMethodsResponse
     */
    public GetMethodsResponse getMethods(MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException;

    /**
     * Get CD Meter State
     * 
     * @param mspVendor
     * @param uri
     * @param getCDMeterState
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public GetCDMeterStateResponse getCDMeterState(final MultispeakVendor mspVendor, String uri,
            GetCDMeterState getCDMeterState) throws MultispeakWebServiceClientException;

    /**
     * Get CD Supported Meters
     * 
     * @param mspVendor
     * @param uri
     * @param getCDSupportedMeters
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public GetCDSupportedMetersResponse getCDSupportedMeters(final MultispeakVendor mspVendor, String uri,
            GetCDSupportedMeters getCDSupportedMeters) throws MultispeakWebServiceClientException;
}
