package com.cannontech.multispeak.client.core.v5;

import com.cannontech.msp.beans.v5.not_server.CDStatesChangedNotification;
import com.cannontech.msp.beans.v5.not_server.EndDeviceEventsNotification;
import com.cannontech.msp.beans.v5.not_server.EndDeviceStatesNotification;
import com.cannontech.msp.beans.v5.not_server.FormattedBlockNotification;
import com.cannontech.msp.beans.v5.not_server.GetMethods;
import com.cannontech.msp.beans.v5.not_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.not_server.MeterReadingsNotification;
import com.cannontech.msp.beans.v5.not_server.PingURL;
import com.cannontech.msp.beans.v5.not_server.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface INOTClient {

    /**
     * Pings the URL.
     * 
     * @param String the URI of the NOT Server
     * @param PingURL the PingURL used as input.
     * @return PingURLResponse
     */
    public PingURLResponse pingURL(MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException;

    /**
     * Get all the supported methods.
     * 
     * @param String the URI of the NOT Server
     * @param GetMethods the GetMethods used as input.
     * @return GetMethodsResponse
     */
    public GetMethodsResponse getMethods(MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException;

    /**
     * end devices states notification
     * 
     * @param MSP vendor details
     * @param String the URI of the NOT Server
     * @param EndDeviceStatesNotification
     * @throws MultispeakWebServiceClientException
     */
    public void endDeviceStatesNotification(MultispeakVendor mspVendor, String uri,
            EndDeviceStatesNotification endDeviceStatesNotification) throws MultispeakWebServiceClientException;

    /**
     * cd states changed notification
     * 
     * @param MSP vendor details
     * @param String the URI of the NOT Server
     * @param CDStatesChangedNotification
     * @throws MultispeakWebServiceClientException
     */
    public void cdStatesChangedNotification(MultispeakVendor mspVendor, String uri,
            CDStatesChangedNotification cdStatesChangedNotification) throws MultispeakWebServiceClientException;

    /**
     * End device events notification
     * 
     * @param MSP vendor details
     * @param String the URI of the NOT Server
     * @param EndDeviceEventsNotification
     * @throws MultispeakWebServiceClientException
     */
    public void endDeviceEventsNotification(MultispeakVendor mspVendor, String uri,
            EndDeviceEventsNotification deviceEventsNotification) throws MultispeakWebServiceClientException;

    /**
     * Meter Readings Notification
     * 
     * @param MSP vendor details
     * @param String the URI of the NOT Server
     * @param MeterReadingsNotification
     * @throws MultispeakWebServiceClientException
     */
    public void meterReadingsNotification(MultispeakVendor mspVendor, String uri,
            MeterReadingsNotification meterReadingsNotification) throws MultispeakWebServiceClientException;

    /**
     * Format Block Notification
     * 
     * @param MSP vendor details
     * @param String the URI of the NOT Server
     * @param FormattedBlockNotification
     * @throws MultispeakWebServiceClientException
     */
    public void formattedBlockNotification(MultispeakVendor mspVendor, String uri,
            FormattedBlockNotification formattedBlockNotification) throws MultispeakWebServiceClientException;
}
