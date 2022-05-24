package com.cannontech.multispeak.client.core.v4;

import com.cannontech.msp.beans.v4.MeterRemoveNotification;
import com.cannontech.msp.beans.v4.MeterRemoveNotificationResponse;
import com.cannontech.msp.beans.v4.DeleteMeterGroup;
import com.cannontech.msp.beans.v4.DeleteMeterGroupResponse;
import com.cannontech.msp.beans.v4.EstablishMeterGroup;
import com.cannontech.msp.beans.v4.EstablishMeterGroupResponse;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.GetReadingsByDate;
import com.cannontech.msp.beans.v4.GetReadingsByDateResponse;
import com.cannontech.msp.beans.v4.InsertMeterInMeterGroup;
import com.cannontech.msp.beans.v4.InsertMeterInMeterGroupResponse;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.msp.beans.v4.RemoveMetersFromMeterGroup;
import com.cannontech.msp.beans.v4.RemoveMetersFromMeterGroupResponse;
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

    /**
     * establish Meter Group
     * 
     * @param mspVendor
     * @param uri
     * @param establishMeterGroup
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public EstablishMeterGroupResponse establishMeterGroup(MultispeakVendor mspVendor, String uri,
            EstablishMeterGroup establishMeterGroup) throws MultispeakWebServiceClientException;

    /**
     * 
     * insert Meter In Meter Group
     * 
     * @param mspVendor
     * @param uri
     * @param insertMeterInMeterGroup
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public InsertMeterInMeterGroupResponse insertMeterInMeterGroup(MultispeakVendor mspVendor, String uri,
            InsertMeterInMeterGroup insertMeterInMeterGroup) throws MultispeakWebServiceClientException;

    /**
     * delete Meter Group
     * 
     * @param mspVendor
     * @param uri
     * @param deleteMeterGroup
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public DeleteMeterGroupResponse deleteMeterGroup(MultispeakVendor mspVendor, String uri,
            DeleteMeterGroup deleteMeterGroup) throws MultispeakWebServiceClientException;

    /**
     * remove Meters From Meter Group
     * 
     * @param mspVendor
     * @param uri
     * @param removeMetersFromMeterGroup
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public RemoveMetersFromMeterGroupResponse removeMetersFromMeterGroup(MultispeakVendor mspVendor, String uri,
            RemoveMetersFromMeterGroup removeMetersFromMeterGroup) throws MultispeakWebServiceClientException;
}