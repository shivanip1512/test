package com.cannontech.multispeak.client.core;

import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.ODEventNotification;
import com.cannontech.msp.beans.v3.ODEventNotificationResponse;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface IOAClient {

    /**
     * Pings the URL.
     * 
     * @param String the URI of the EA Server
     * @param PingURL the PingURL used as input.
     * @return PingURLResponse
     */
    PingURLResponse pingURL(MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException;

    /**
     * Get all the supported methods.
     * 
     * @param String the URI of the EA Server
     * @param GetMethods the GetMethods used as input.
     * @return GetMethodsResponse
     */
    GetMethodsResponse getMethods(MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException;

    /**
     * Gets the Event Notification.
     * 
     * @param String the URI of the OA Server
     * @param ODEventNotification the ODEventNotification used as input.
     * @return ODEventNotificationResponse
     * @throws MultispeakWebServiceClientException
     */
    public ODEventNotificationResponse odEventNotification(MultispeakVendor mspVendor, String uri,
            ODEventNotification odEventNotification) throws MultispeakWebServiceClientException;

}
