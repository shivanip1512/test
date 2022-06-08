package com.cannontech.multispeak.client.core.v4;


import com.cannontech.msp.beans.v4.CDStateChangedNotification;
import com.cannontech.msp.beans.v4.CDStateChangedNotificationResponse;
import com.cannontech.msp.beans.v4.FormattedBlockNotification;
import com.cannontech.msp.beans.v4.FormattedBlockNotificationResponse;
import com.cannontech.msp.beans.v4.GetAllServiceLocations;
import com.cannontech.msp.beans.v4.GetAllServiceLocationsResponse;
import com.cannontech.msp.beans.v4.GetDomainMembers;
import com.cannontech.msp.beans.v4.GetDomainMembersResponse;
import com.cannontech.msp.beans.v4.GetMeterByCustomerID;
import com.cannontech.msp.beans.v4.GetMeterByCustomerIDResponse;
import com.cannontech.msp.beans.v4.GetMeterByServiceLocationID;
import com.cannontech.msp.beans.v4.GetMeterByServiceLocationIDResponse;
import com.cannontech.msp.beans.v4.GetMetersBySearchString;
import com.cannontech.msp.beans.v4.GetMetersBySearchStringResponse;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.MeterEventNotification;
import com.cannontech.msp.beans.v4.MeterEventNotificationResponse;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.msp.beans.v4.ReadingChangedNotification;
import com.cannontech.msp.beans.v4.ReadingChangedNotificationResponse;
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
    
    /**
     * Gets domain members.
     * 
     * @param MSP              vendor details
     * @param String           the URI of the CB Server
     * @param GetDomainMembers the GetDomainMembers used as input.
     * @return GetDomainMembersResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetDomainMembersResponse getDomainMembers(MultispeakVendor mspVendor, String uri,
            GetDomainMembers getDomainMembers) throws MultispeakWebServiceClientException;
    
    /**
     * 
     * @param mspVendor           vendor details
     * @param endpointUrl         the URI of the CB Server
     * @param getMeterByServLocID serviceLocationID used
     * @return Meters By ServiceLocationID
     * @throws MultispeakWebServiceClientException
     */
    public GetMeterByServiceLocationIDResponse getMeterByServiceLocationID(MultispeakVendor mspVendor, String endpointUrl,
            GetMeterByServiceLocationID getMeterByServLocID) throws MultispeakWebServiceClientException;

    
    /**
     * Format the blocked notifications
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param MeterEventNotification the MeterEventNotification used as input.
     * @return MeterEventNotificationResponse
     * @throws MultispeakWebServiceClientException
     */
    public MeterEventNotificationResponse meterEventNotification(MultispeakVendor mspVendor, String uri,
            MeterEventNotification meterEventNotification) throws MultispeakWebServiceClientException;

    /**
     * CD State Change Notification
     * 
     * @param mspVendor                  vendor details
     * @param uri                        the URI of the CB Server
     * @param cdStateChangedNotification the CDStateChangedNotification used as input.
     * @return CDStateChangedNotificationResponse
     * @throws MultispeakWebServiceClientException
     */
    public CDStateChangedNotificationResponse cdStateChangedNotification(MultispeakVendor mspVendor, String uri,
            CDStateChangedNotification cdStateChangedNotification) throws MultispeakWebServiceClientException;
    
    /**
     * Reads the change in notification.
     * 
     * @param MSP                        vendor details
     * @param String                     the URI of the CB Server
     * @param ReadingChangedNotification the ReadingChangedNotification used as input.
     * @return ReadingChangedNotificationResponse
     * @throws MultispeakWebServiceClientException
     */
    public ReadingChangedNotificationResponse readingChangedNotification(MultispeakVendor mspVendor, String uri,
            ReadingChangedNotification readingChangedNotification) throws MultispeakWebServiceClientException;

    /**
     * Format Block Notification
     * 
     * @param MSP vendor details
     * @param String the URI of the NOT Server
     * @param FormattedBlockNotification
     * @throws MultispeakWebServiceClientException
     */
    public FormattedBlockNotificationResponse formattedBlockNotification(MultispeakVendor mspVendor, String uri, String interfaceName,
            FormattedBlockNotification formattedBlockNotification) throws MultispeakWebServiceClientException;
    /**
     * Gets all service Locations.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetAllServiceLocations the GetAllServiceLocations used as input.
     * @return GetAllServiceLocationsResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetAllServiceLocationsResponse getAllServiceLocations(MultispeakVendor mspVendor, String uri,
            GetAllServiceLocations getAllServiceLocations) throws MultispeakWebServiceClientException;
    
    /**
     * Gets Meter by Customer.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetMeterByCustID the GetMeterByCustID used as input.
     * @return GetMeterByCustIDResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetMeterByCustomerIDResponse getMeterByCustomerID(MultispeakVendor mspVendor, String uri,
            GetMeterByCustomerID getMeterByCustomerID) throws MultispeakWebServiceClientException;
    
    /**
     * Gets Meters by SearchString.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetMetersBySearchString the GetMetersBySearchString used as input.
     * @return GetMetersBySearchStringResponse
     * @throws MultispeakWebServiceClientException
     */
    GetMetersBySearchStringResponse getMetersBySearchString(MultispeakVendor mspVendor, String uri,
            GetMetersBySearchString getMetersBySearchString) throws MultispeakWebServiceClientException;

}