package com.cannontech.multispeak.client.core.v5;

import java.util.List;

import com.cannontech.msp.beans.v5.cb_server.GetAllServiceLocations;
import com.cannontech.msp.beans.v5.cb_server.GetAllServiceLocationsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetCustomersByMeterIDs;
import com.cannontech.msp.beans.v5.cb_server.GetCustomersByMeterIDsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetDomainsByDomainNames;
import com.cannontech.msp.beans.v5.cb_server.GetDomainsByDomainNamesResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByAccountIDs;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByAccountIDsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByContactInfo;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByContactInfoResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByCustomerIDs;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByCustomerIDsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByNetworkModelRefs;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByNetworkModelRefsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersBySearchString;
import com.cannontech.msp.beans.v5.cb_server.GetMetersBySearchStringResponse;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByServiceLocationIDs;
import com.cannontech.msp.beans.v5.cb_server.GetMetersByServiceLocationIDsResponse;
import com.cannontech.msp.beans.v5.cb_server.GetServiceLocationsByMeterIDs;
import com.cannontech.msp.beans.v5.cb_server.GetServiceLocationsByMeterIDsResponse;
import com.cannontech.msp.beans.v5.cb_server.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface ICBClient {

    /**
     * Ping the URL.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @return PingURLResponse
     * @throws MultispeakWebServiceClientException
     */
    public PingURLResponse pingURL(MultispeakVendor mspVendor, String uri) throws MultispeakWebServiceClientException;

    /**
     * Get all the supported methods.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @return list of methods
     * @throws MultispeakWebServiceClientException
     */
    public List<String> getMethods(MultispeakVendor mspVendor, String uri)
            throws MultispeakWebServiceClientException;

    /**
     * Gets Meters by Contact Info.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetMetersByContactInfo : getMetersByContactInfo used as input.
     * @return GetMetersByContactInfoResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetMetersByContactInfoResponse getMetersByContactInfo(MultispeakVendor mspVendor, String uri,
            GetMetersByContactInfo getMetersByContactInfo) throws MultispeakWebServiceClientException;

    /**
     * Gets Meter by Account IDs.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetMetersByAccountIDs : getMetersByAccountIDs used as input.
     * @return GetMetersByAccountIDsResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetMetersByAccountIDsResponse getMetersByAccountIDs(MultispeakVendor mspVendor, String uri,
            GetMetersByAccountIDs getMetersByAccountIDs) throws MultispeakWebServiceClientException;

    /**
     * Gets Meter by Customer.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetMetersByCustomerIDs the GetMetersByCustomerIDs used as input.
     * @return GetMetersByCustomerIDsResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetMetersByCustomerIDsResponse getMetersByCustomerIDs(MultispeakVendor mspVendor, String uri,
            GetMetersByCustomerIDs getMetersByCustomerIDs) throws MultispeakWebServiceClientException;

    /**
     * Gets Meters by SearchString.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetMetersBySearchString the GetMetersBySearchString used as input.
     * @return GetMetersBySearchStringResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetMetersBySearchStringResponse getMetersBySearchString(MultispeakVendor mspVendor, String uri,
            GetMetersBySearchString getMetersBySearchString) throws MultispeakWebServiceClientException;

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
     * Gets domains by domain names.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetDomainsByDomainNames the GetDomainsByDomainNames used as input.
     * @return GetDomainsByDomainNamesResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetDomainsByDomainNamesResponse getDomainsByDomainNames(MultispeakVendor mspVendor, String uri,
            GetDomainsByDomainNames getDomainsByDomainNames) throws MultispeakWebServiceClientException;

    /**
     * Gets meters by Network Model Ref
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetMetersByNetworkModelRefs the GetMetersByNetworkModelRefs used as input.
     * @return GetMetersByNetworkModelRefsResponse
     * @throws MultispeakWebServiceClientException
     */
    public GetMetersByNetworkModelRefsResponse getMetersByNetworkModelRef(MultispeakVendor mspVendor, String uri,
            GetMetersByNetworkModelRefs getMetersByNetworkModelRefs) throws MultispeakWebServiceClientException;

    /**
     * Gets Customers information by Meter IDs.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetCustomersByMeterIDs the GetCustomersByMeterIDs used as input
     * @return GetCustomersByMeterIDsResponse
     * @throws MultispeakWebServiceClientException
     */
    GetCustomersByMeterIDsResponse getCustomersByMeterIDs(MultispeakVendor mspVendor, String uri,
            GetCustomersByMeterIDs getCustomersByMeterIDs) throws MultispeakWebServiceClientException;


    /**
     * Get Meters By Service Location IDs.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetMetersByServiceLocationIDs , the GetMetersByServiceLocationIDs used as input
     * @return GetMetersByServiceLocationIDsResponse
     * @throws MultispeakWebServiceClientException
     */
    GetMetersByServiceLocationIDsResponse getMetersByServiceLocationIDs(MultispeakVendor mspVendor, String uri,
            GetMetersByServiceLocationIDs getMetersByServiceLocationIDs) throws MultispeakWebServiceClientException;

    /**
     * Gets Service Locations By Meter Ids.
     * 
     * @param MSP vendor details
     * @param String the URI of the CB Server
     * @param GetServiceLocationsByMeterIDs, the GetServiceLocationsByMeterIDs used as input
     * @return GetServiceLocationsByMeterIDsResponse
     * @throws MultispeakWebServiceClientException
     */
    GetServiceLocationsByMeterIDsResponse getServiceLocationsByMeterIDs(MultispeakVendor mspVendor, String uri,
            GetServiceLocationsByMeterIDs getServiceLocationsByMeterIDs) throws MultispeakWebServiceClientException;
}
