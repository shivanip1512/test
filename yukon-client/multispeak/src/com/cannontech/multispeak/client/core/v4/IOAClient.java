package com.cannontech.multispeak.client.core.v4;

import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.ODEventNotification;
import com.cannontech.msp.beans.v4.ODEventNotificationResponse;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface IOAClient {

    /**
     * Pings the URL.
     * 
     * @param MSP     vendor details
     * @param String  the URI of the OA Server
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
     * @param String     the URI of the OA Server
     * @param GetMethods the GetMethods used as input.
     * @return GetMethodsResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetMethodsResponse getMethods(MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException;
    
    /**
     * Gets the Event Notification.
     * 
     * @param String              the URI of the OA Server
     * @param ODEventNotification the ODEventNotification used as input.
     * @return ODEventNotificationResponse
     * @throws MultispeakWebServiceClientException
     */
    public ODEventNotificationResponse odEventNotification(MultispeakVendor mspVendor, String uri,
            ODEventNotification odEventNotification) throws MultispeakWebServiceClientException;
}
