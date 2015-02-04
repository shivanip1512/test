package com.cannontech.multispeak.client.core;

import com.cannontech.msp.beans.v3.CDStateChangedNotification;
import com.cannontech.msp.beans.v3.CDStateChangedNotificationResponse;
import com.cannontech.msp.beans.v3.FormattedBlockNotification;
import com.cannontech.msp.beans.v3.FormattedBlockNotificationResponse;
import com.cannontech.msp.beans.v3.GetAllServiceLocations;
import com.cannontech.msp.beans.v3.GetAllServiceLocationsResponse;
import com.cannontech.msp.beans.v3.GetCustomerByMeterNo;
import com.cannontech.msp.beans.v3.GetCustomerByMeterNoResponse;
import com.cannontech.msp.beans.v3.GetDomainMembers;
import com.cannontech.msp.beans.v3.GetDomainMembersResponse;
import com.cannontech.msp.beans.v3.GetMeterByAccountNumber;
import com.cannontech.msp.beans.v3.GetMeterByAccountNumberResponse;
import com.cannontech.msp.beans.v3.GetMeterByCustID;
import com.cannontech.msp.beans.v3.GetMeterByCustIDResponse;
import com.cannontech.msp.beans.v3.GetMeterByMeterNo;
import com.cannontech.msp.beans.v3.GetMeterByMeterNoResponse;
import com.cannontech.msp.beans.v3.GetMeterByServLoc;
import com.cannontech.msp.beans.v3.GetMeterByServLocResponse;
import com.cannontech.msp.beans.v3.GetMetersByEALocation;
import com.cannontech.msp.beans.v3.GetMetersByEALocationResponse;
import com.cannontech.msp.beans.v3.GetMetersByFacilityID;
import com.cannontech.msp.beans.v3.GetMetersByFacilityIDResponse;
import com.cannontech.msp.beans.v3.GetMetersBySearchString;
import com.cannontech.msp.beans.v3.GetMetersBySearchStringResponse;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.GetServiceLocationByMeterNo;
import com.cannontech.msp.beans.v3.GetServiceLocationByMeterNoResponse;
import com.cannontech.msp.beans.v3.MeterEventNotification;
import com.cannontech.msp.beans.v3.MeterEventNotificationResponse;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.msp.beans.v3.ReadingChangedNotification;
import com.cannontech.msp.beans.v3.ReadingChangedNotificationResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface ICBClient {
    /**
     * Gets Customer by Meter Number.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetCustomerByMeterNo the GetCustomerByMeterNo used as input.
     * @return GetCustomerByMeterNoResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetCustomerByMeterNoResponse getCustomerByMeterNo(MultispeakVendor mspVendor, String uri,
            GetCustomerByMeterNo getCustomerByMeterNo) throws MultispeakWebServiceClientException;

    /**
     * Gets Service Location By Meter Number.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetServiceLocationByMeterNo the GetServiceLocationByMeterNo used as input.
     * @return GetServiceLocationByMeterNoResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetServiceLocationByMeterNoResponse getServiceLocationByMeterNo(MultispeakVendor mspVendor, String uri,
            GetServiceLocationByMeterNo getServiceLocationByMeterNo) throws MultispeakWebServiceClientException;

    /**
     * Gets Meter By Meter Number.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetMeterByMeterNo the GetServiceLocationByMeterNo used as input.
     * @return GetMeterByMeterNoResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetMeterByMeterNoResponse getMeterByMeterNo(MultispeakVendor mspVendor, String uri,
            GetMeterByMeterNo getMeterByMeterNo) throws MultispeakWebServiceClientException;

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
     * Gets a meter by service Locations.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetMeterByServLoc the GetMeterByServLoc used as input.
     * @return GetMeterByServLocResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetMeterByServLocResponse getMeterByServLoc(MultispeakVendor mspVendor, String uri,
            GetMeterByServLoc GetMeterByServLoc) throws MultispeakWebServiceClientException;

    /**
     * Gets domain members.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetDomainMembers the GetDomainMembers used as input.
     * @return GetDomainMembersResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetDomainMembersResponse getDomainMembers(MultispeakVendor mspVendor, String uri,
            GetDomainMembers getDomainMembers) throws MultispeakWebServiceClientException;

    /**
     * Gets meters by EA Location.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetMetersByEALocation the GetMetersByEALocation used as input.
     * @return GetMetersByEALocationResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetMetersByEALocationResponse getMetersByEALocation(MultispeakVendor mspVendor, String uri,
            GetMetersByEALocation getMetersByEALocation) throws MultispeakWebServiceClientException;

    /**
     * Gets Meters by facility.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetMetersByFacilityID the GetMetersByFacilityID used as input.
     * @return GetMetersByFacilityIDResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetMetersByFacilityIDResponse getMetersByFacilityID(MultispeakVendor mspVendor, String uri,
            GetMetersByFacilityID getMetersByFacilityID) throws MultispeakWebServiceClientException;

    /**
     * Gets Meter by Account Number.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetMeterByAccountNumber the GetMeterByAccountNumber used as input.
     * @return GetMeterByAccountNumberResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetMeterByAccountNumberResponse getMeterByAccountNumber(MultispeakVendor mspVendor, String uri,
            GetMeterByAccountNumber getMeterByAccountNumber) throws MultispeakWebServiceClientException;

    /**
     * Gets Meter by Customer.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetMeterByCustID the GetMeterByCustID used as input.
     * @return GetMeterByCustIDResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetMeterByCustIDResponse getMeterByCustID(MultispeakVendor mspVendor, String uri,
            GetMeterByCustID getMeterByCustID) throws MultispeakWebServiceClientException;

    /**
     * Pings the URL.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param PingURL the PingURL used as input.
     * @return PingURLResponse
     * @throws MultispeakWebServiceClientException
     */
    public PingURLResponse pingURL(MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException;

    /**
     * Get all the supported methods.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetMethods the GetMethods used as input.
     * @return GetMethodsResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetMethodsResponse getMethods(MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException;

    /**
     * Reads the change in notification.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param ReadingChangedNotification the ReadingChangedNotification used as input.
     * @return ReadingChangedNotificationResponse
     * @throws MultispeakWebServiceClientException
     */
    public ReadingChangedNotificationResponse readingChangedNotification(MultispeakVendor mspVendor, String uri,
            ReadingChangedNotification readingChangedNotification) throws MultispeakWebServiceClientException;

    /**
     * Format the blocked notifications
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param FormattedBlockNotification the FormattedBlockNotification used as input.
     * @return FormattedBlockNotificationResponse
     * @throws MultispeakWebServiceClientException
     */
    public FormattedBlockNotificationResponse formattedBlockNotification(MultispeakVendor mspVendor, String uri,
            FormattedBlockNotification formattedBlockNotification) throws MultispeakWebServiceClientException;

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
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param CDStateChangedNotification the CDStateChangedNotification used as input.
     * @return CDStateChangedNotificationResponse
     * @throws MultispeakWebServiceClientException
     */
    public CDStateChangedNotificationResponse cdStateChangedNotification(MultispeakVendor mspVendor, String uri,
            CDStateChangedNotification cdStateChangedNotification) throws MultispeakWebServiceClientException;
    /**
     * Get Meters By Search String
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetMetersBySearchString the getMetersBySearchString used as input.
     * @return GetMetersBySearchStringResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetMetersBySearchStringResponse getMetersBySearchString(final MultispeakVendor mspVendor, String uri,
    		GetMetersBySearchString getMetersBySearchString) throws MultispeakWebServiceClientException;

}
