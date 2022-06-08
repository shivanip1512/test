package com.cannontech.multispeak.dao.v4;

import java.util.List;

import com.cannontech.msp.beans.v4.ErrorObject;
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
<<<<<<< HEAD
     * Returns a list of the MeterNumber(s) for the mspServiceLocation.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * @param mspServiceLocation The serviceLocation to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<MspMeter> getMspMetersByServiceLocation(ServiceLocation mspServiceLocation, MultispeakVendor vendor);
   
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
     * Retrieves msp ServiceLocation objects from the mspVendor.
     * Lists of msp ServiceLocation objects are given to the callback as they are retrieved in chunks from the vendor.
     */
    public void getAllMspServiceLocations(MultispeakVendor mspVendor, MultispeakGetAllServiceLocationsCallback callback)
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

}