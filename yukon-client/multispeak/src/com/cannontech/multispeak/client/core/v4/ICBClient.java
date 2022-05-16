package com.cannontech.multispeak.client.core.v4;

import com.cannontech.msp.beans.v3.GetServiceLocationByMeterNo;
import com.cannontech.msp.beans.v3.GetServiceLocationByMeterNoResponse;
import com.cannontech.msp.beans.v4.GetCustomerByMeterID;
import com.cannontech.msp.beans.v4.GetCustomerByMeterIDResponse;
import com.cannontech.msp.beans.v4.GetMeterByMeterID;
import com.cannontech.msp.beans.v4.GetMeterByMeterIDResponse;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.GetServiceLocationByMeterID;
import com.cannontech.msp.beans.v4.GetServiceLocationByMeterIDResponse;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface ICBClient {

    /**
     * Pings the URL.
     * 
     * @param MSP     vendor details
     * @param String  the URI of the CB Server
     * @param PingURL the PingURL used as input.
     * @return PingURLResponse
     * @throws MultispeakWebServiceClientException
     */
    public PingURLResponse pingURL(MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException;

    /**
     * Get all the supported methods.
     * 
     * @param MSP        vendor details
     * @param String     the URI of the CB Server
     * @param GetMethods the GetMethods used as input.
     * @return GetMethodsResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetMethodsResponse getMethods(MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException;

    public GetMeterByMeterIDResponse getMeterByMeterID(final MultispeakVendor mspVendor, String uri,
            GetMeterByMeterID getMeterByMeterId) throws MultispeakWebServiceClientException ;
    
    /**
     * Gets Customer by Meter Number.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetCustomerByMeterNo the GetCustomerByMeterNo used as input.
     * @return GetCustomerByMeterNoResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetCustomerByMeterIDResponse getCustomerByMeterId(MultispeakVendor mspVendor, String uri,
            GetCustomerByMeterID getCustomerByMeterId) throws MultispeakWebServiceClientException;
    
    /**
     * Gets Service Location By Meter Number.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetServiceLocationByMeterNo the GetServiceLocationByMeterNo used as input.
     * @return GetServiceLocationByMeterNoResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetServiceLocationByMeterIDResponse getServiceLocationByMeterId(MultispeakVendor mspVendor, String uri,
            GetServiceLocationByMeterID getServiceLocationByMeterId) throws MultispeakWebServiceClientException;

}
