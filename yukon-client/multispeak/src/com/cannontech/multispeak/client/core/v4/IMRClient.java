package com.cannontech.multispeak.client.core.v4;

import com.cannontech.msp.beans.v4.MeterRemoveNotification;
import com.cannontech.msp.beans.v4.MeterRemoveNotificationResponse;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.GetReadingsByDate;
import com.cannontech.msp.beans.v4.GetReadingsByDateResponse;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface IMRClient {

    /**
     * Pings the URL.
     * 
     * @param String  the URI of the MR Server
     * @param PingURL the PingURL used as input.
     * @return PingURLResponse
     */
    public PingURLResponse pingURL(MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException;

    /**
     * Get all the supported methods.
     * 
     * @param String     the URI of the MR Server
     * @param GetMethods the GetMethods used as input.
     * @return GetMethodsResponse
     */
    public GetMethodsResponse getMethods(MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException;

    /**
     * get Readings By Date
     * 
     * @param mspVendor
     * @param uri
     * @param getReadingsByDate
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public GetReadingsByDateResponse getReadingsByDate(MultispeakVendor mspVendor, String uri,
            GetReadingsByDate getReadingsByDate) throws MultispeakWebServiceClientException;

    /**
     * meter Remove Notification
     * 
     * @param mspVendor
     * @param uri
     * @param meterRemoveNotification
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public MeterRemoveNotificationResponse meterRemoveNotification(MultispeakVendor mspVendor, String uri,
            MeterRemoveNotification meterRemoveNotification) throws MultispeakWebServiceClientException;
}