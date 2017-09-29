package com.cannontech.multispeak.dao.v5;

import java.util.List;
import java.util.Map;

import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.multispeak.Customer;
import com.cannontech.msp.beans.v5.multispeak.MspMeter;
import com.cannontech.msp.beans.v5.multispeak.ServiceLocation;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface MspObjectDao {

    /**
     * Creates a new (MSP) ErrorObject
     * 
     * @param objectID The Multispeak objectID
     * @param errorMessage The error message.
     * @return
     */
    public ErrorObject getErrorObject(String objectID, String errorMessage, String nounType, String method,
            String userName);

    /**
     * Creates and ErrorObject array from errorObjects List
     * 
     * @param errorObjects
     * @return
     */
    public ErrorObject[] toErrorObject(List<ErrorObject> errorObjects);

    /**
     * Creates an entry in the System log and prints a debug statement.
     * TODO All of this logging (SystemLog) can be removed following completion of MultiSpeak EventLogs.
     * 
     * @param method
     * @param description
     * @param userName
     */
    public void logMSPActivity(String method, String description, String userName);

    /**
     * Creates a new (MSP) ErrorObject
     * 
     * @param objectID The Multispeak objectID
     * @param nounType The object type
     * @param notFoundObjectType The objectID type
     * @param exceptionMessage An alternative message to return.
     * @return
     */
    public ErrorObject getNotFoundErrorObject(String objectID, String notFoundObjectType, String nounType,
            String method, String userName, String exceptionMessage);

    /**
     * Creates a new (MSP) ErrorObject
     * 
     * @param objectID The Multispeak objectID
     * @param nounType The object type
     * @param notFoundObjectType The objectID type
     * @param exceptionMessage An alternative message to return.
     * @return
     */
    public ErrorObject getNotFoundErrorObject(String objectID, String notFoundObjectType, String nounType,
            String method, String userName);

    /**
     * Returns a Msp ServiceLocation for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty ServiceLocation object is returned.
     * 
     * @param meter The Meter to get the ServiceLocation information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public ServiceLocation getMspServiceLocation(String meterNumber, MultispeakVendor mspVendor);

    /**
     * Returns a list of the MeterNumber(s) for the serviceLocation .
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<MspMeter> object is returned.
     * 
     * @param serviceLocation The serviceLocation to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<MspMeter> getMspMetersByServiceLocation(ServiceLocation mspServiceLocation,
            MultispeakVendor mspVendor);

    /**
     * Utility to implement the pingURL method for the service.
     * 
     * @param mspVendor The multispeak vendor to invoke.
     * @param service The string representation of the webservice to run.
     * @param endpointUrl The endpointUrl for the service.
     * @return Returns an ArrayOfErrorObjects
     * @throws MultispeakWebServiceClientException
     */
    public ErrorObject[] pingURL(MultispeakVendor mspVendor, String service, String endpointUrl)
            throws MultispeakWebServiceClientException;

    /**
     * Utility to implement the getMethods method for the service.
     * 
     * @param mspVendor The MultiSpeak vendor to invoke.
     * @param service The string representation of the webservice to run.
     * @param endpointUrl The endpointUrl for the service.
     * @return Returns the list of supported methods
     * @throws MultispeakWebServiceClientException
     */
    public List<String> getMethods(MultispeakVendor mspVendor, String service, String endpointUrl)
            throws MultispeakWebServiceClientException;

    /**
     * Returns a list of MspMeter(s) for the given facilityNameValue(s)
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<MspMeter> object is returned.
     * 
     * @param facilityNames The list of facilityName to get the Meters information for.
     * @param facilityNameValues The map of facilityName Value to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<MspMeter> getMetersByContactInfo(Map<String, String> facilityNameValues, MultispeakVendor mspVendor);

    /**
     * Returns a list of MspMeter(s) for given customerId(s).
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<MspMeter> object is returned.
     * 
     * @param customerIDs The customerIDs to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return list of the MspMeter(s)
     */
    public List<MspMeter> getMetersByCustomerIDs(List<String> customerIDs, MultispeakVendor mspVendor);

    /**
     * Returns a list of MspMeter(s) for the given list of accountID(s).
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<MspMeter> object is returned.
     * 
     * @param accoundIDs The list of account IDs to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return List<MspMeter>
     */
    public List<MspMeter> getMetersByAccountIDs(List<String> accoundIDs, MultispeakVendor mspVendor);

    /**
     * Returns a list of MspMeter(s) for the given search String .
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<MspMeter> object is returned.
     * 
     * @param searchString The searchString to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return List<MspMeter>
     */
    public List<MspMeter> getMetersBySearchString(String searchString, MultispeakVendor mspVendor);

    /**
     * Retrieves msp ServiceLocation objects from the mspVendor.
     * List of msp ServiceLocation objects are given to the callback as they are retrieved in chunks from the
     * vendor.
     */
    public void getAllMspServiceLocations(MultispeakVendor mspVendor, MultispeakGetAllServiceLocationsCallback callback)
            throws MultispeakWebServiceClientException;

    /**
     * Returns a list of SubstationNames for the vendor.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty String List object is returned.
     * 
     * @param mspVendor
     *        List<String> domainNames list of domain names
     * @return List<DomainMember> the list of domainMembers
     */
    public List<String> getMspSubstationName(MultispeakVendor mspVendor, List<String> domainNames);

    /**
     * Returns a list of the MspMeter(s) for the given location/NertworkModelRef(s).
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<MspMeter> object is returned.
     * 
     * @param List<String> locations : The list of NetworkModelRef(locations) to get the Meter information
     *        for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return List<MspMeter>
     */
    public List<MspMeter> getMetersByNetworkModelRef(List<String> locations, MultispeakVendor mspVendor);

    /**
     * Returns a list of MSP Customer(s) for the meter numbers provided
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty list of Customer object is returned.
     * 
     * @param meterNumbers List of meter numbers to get the Customer information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return List<Customer> List of MSP Customers
     */
    List<Customer> getCustomersByMeterIDs(List<String> meterNumbers, MultispeakVendor mspVendor);

    /**
     * Returns MSP customer for the given meter number
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty Customer object is returned.
     * 
     * @param meter
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return Customer MSP customer
     */
    Customer getMspCustomer(SimpleMeter meter, MultispeakVendor mspVendor);

    /**
     * Returns a list of supported method names for mspVendor
     * Catches any MultispeakWebServiceClientException and returns emptyList.
     * 
     * @param mspVendor
     */
    List<String> findMethods(String mspServer, MultispeakVendor mspVendor);
    
    /**
     * Returns multispeak ServiceLocation for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty ServiceLocation object is returned.
     * @param meter The Meter to get the ServiceLocation information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    ServiceLocation getMspServiceLocation(SimpleMeter meter, MultispeakVendor mspVendor);
    /**
     * Returns a list of SubstationNames for the vendor.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty String List object is returned.
     * 
     * @param mspVendor
     */
    List<String> getMspSubstationName(MultispeakVendor mspVendor);

}
