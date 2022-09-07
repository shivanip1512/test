package com.cannontech.multispeak.dao.v4;

import java.util.List;

import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.msp.beans.v4.ArrayOfServiceLocation1;
import com.cannontech.msp.beans.v4.Customer;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.Meters;
import com.cannontech.msp.beans.v4.MspMeter;
import com.cannontech.msp.beans.v4.MspObject;
import com.cannontech.msp.beans.v4.ServiceLocation;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface MspObjectDao {
    
    /**
     * Creates and ErrorObject array from errorObjects List
     * @param errorObjects
     * @return
     */
    public ErrorObject[] toErrorObject(List<ErrorObject> errorObjects);

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
     * Utility to implement the pingURL method for the service.
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
     * @param mspVendor The multispeak vendor to invoke. 
     * @param service The string representation of the webservice to run. 
     * @param endpointUrl The endpointUrl for the service. 
     * @return Returns an ArrayOfErrorObjects
     * @throws MultispeakWebServiceClientException
     */
    public List<String> getMethods(MultispeakVendor mspVendor, String service, String endpointUrl)
            throws MultispeakWebServiceClientException;
    
    /**
     * Returns a list of SubstationNames for the vendor.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty String List object is returned.
     * @param mspVendor
     * @return
     */
    public List<String> getMspSubstationName(MultispeakVendor mspVendor);
    
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
     * Returns a Msp ServiceLocation for the meter
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty ServiceLocation object is returned.
     * @param meterNumber The meter to get the ServiceLocation information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public ServiceLocation getServiceLocationByMeterNo(String meterNo,MultispeakVendor mspVendor);

    /**
     * Returns a Msp ServiceLocation for the meter
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty ServiceLocation object is returned.
     * @param mspObject The meter to get the ServiceLocation information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public ServiceLocation getMspServiceLocation(MspObject mspObject, MultispeakVendor mspVendor);
 
    /**
     * Creates an entry in the System log and prints a debug statement.
     * @param method
     * @param description
     * @param userName
     */
    public void logMSPActivity(String method, String description, String userName);

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
     * Returns multispeak ServiceLocation for the meterNo.     
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty ServiceLocation object is returned.
     * @param meter The Meter to get the ServiceLocation information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    ArrayOfServiceLocation1 getMspServiceLocation(SimpleMeter meter, MultispeakVendor mspVendor);
    /**
     * Returns a Msp Customer for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found, 
     * an empty Customer object is returned.
     * @param meterNumber The meter number to get the Customer information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public Customer getMspCustomer(String meterNumber, MultispeakVendor mspVendor);

    
    /**
     * Returns a Msp Meter for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty Meter object is returned.
     * @param meter The Meter to get the Meter information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public Meters getMspMeter(SimpleMeter meter, MultispeakVendor mspVendor);

    /**
     * Returns a Msp Meter for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty Meter object is returned.
     * @param meterNumber The meter number to get the Meter information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public Meters getMspMeter(String meterNumber, MultispeakVendor mspVendor) 
            throws MultispeakWebServiceClientException;

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
     * Retrieves msp ServiceLocation objects from the mspVendor.
     * List of msp ServiceLocation objects are given to the callback as they are retrieved in chunks from the
     * vendor.
     */
    public void getAllMspServiceLocations(MultispeakVendor mspVendor, MultispeakGetAllServiceLocationsCallback callback)
            throws MultispeakWebServiceClientException; 

    /**
    * Returns a Msp ArrayOfServiceLocation1 for the meter
    * If the interface/method is not supported by mspVendor, or if no object is found,
    * an empty ServiceLocation object is returned.
    * @param meterNumber The meterNumber to get the ServiceLocation information for.
    * @param mspVendor The Multispeak Vendor to ask for the information from.
    * @return Array of ServiceLocation for meter
    */
    
    public ArrayOfServiceLocation1 getMspServiceLocation(String meterNumber, MultispeakVendor mspVendor)
            throws MultispeakWebServiceClientException;
    
    /**
     * Returns a list of supported method names for mspVendor
     * Catches any MultispeakWebServiceClientException and returns emptyList.
     * 
     * @param mspVendor
     */
    List<String> findMethods(String mspServer, MultispeakVendor mspVendor);
    
    /**
     * Returns a list of the MeterNumber(s) for the custId.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * @param custId The custId to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<MspMeter> getMspMetersByCustomerId(String custId, MultispeakVendor mspVendor);
    
    /**
     * Returns a list of the MeterNumber(s) for the give search String .
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * @param searchString The searchString to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<MspMeter> getMetersBySearchString(String searchString, MultispeakVendor mspVendor);
    
    /**
     * Returns a list of the MeterNumber(s) for the facilityId.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * @param facilityId The facilityId to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<MspMeter> getMspMetersByFacilityId(String facilityId, MultispeakVendor mspVendor);
    
    /**
     * Returns a list of the MeterNumber(s) for the eaLocation.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * @param eaLocation The eaLocation to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<MspMeter> getMspMetersByEALocation(String eaLocation, MultispeakVendor mspVendor);
    
    /**
     * Returns a list of the MeterNumber(s) for the accountNumber.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * @param accountNumber The accountNumber to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<MspMeter> getMspMetersByAccountNumber(String accountNumber, MultispeakVendor mspVendor);

}