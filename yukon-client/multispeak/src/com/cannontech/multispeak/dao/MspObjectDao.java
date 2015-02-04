package com.cannontech.multispeak.dao;

import java.util.List;

import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.msp.beans.v3.Customer;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface MspObjectDao {

    /**
     * Returns a Msp Customer for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty Customer object is returned.
     * 
     * @param meter The Meter to get the Customer information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public Customer getMspCustomer(SimpleMeter meter, MultispeakVendor mspVendor);

    /**
     * Returns a Msp Customer for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty Customer object is returned.
     * 
     * @param meterNumber The meter number to get the Customer information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public Customer getMspCustomer(String meterNumber, MultispeakVendor mspVendor);

    /**
     * Returns a Msp ServiceLocation for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty ServiceLocation object is returned.
     * 
     * @param meter The Meter to get the ServiceLocation information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public ServiceLocation getMspServiceLocation(SimpleMeter meter, MultispeakVendor mspVendor);

    /**
     * Returns a Msp ServiceLocation for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty ServiceLocation object is returned.
     * 
     * @param meterNumber The meter number to get the ServiceLocation information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public ServiceLocation getMspServiceLocation(String meterNumber, MultispeakVendor mspVendor);

    /**
     * Returns a Msp Meter for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty Meter object is returned.
     * 
     * @param meter The Meter to get the Meter information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public Meter getMspMeter(SimpleMeter meter, MultispeakVendor mspVendor);

    /**
     * Returns a Msp Meter for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty Meter object is returned.
     * 
     * @param meterNumber The meter number to get the Meter information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public Meter getMspMeter(String meterNumber, MultispeakVendor mspVendor);

    /**
     * Retrieves msp ServiceLocation objects from the mspVendor.
     * Lists of msp ServiceLocation objects are given to the callback as they are retrieved in chunks from the
     * vendor.
     */
    public void getAllMspServiceLocations(MultispeakVendor mspVendor, MultispeakGetAllServiceLocationsCallback callback)
            throws MultispeakWebServiceClientException;

    /**
     * Returns a list of the MeterNumber(s) for the mspServiceLocation.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * 
     * @param mspServiceLocation The serviceLocation to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<Meter> getMspMetersByServiceLocation(ServiceLocation mspServiceLocation, MultispeakVendor mspVendor);

    /**
     * Returns a list of the MeterNumber(s) for the serviceLocation .
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * 
     * @param serviceLocation The serviceLocation to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<Meter> getMspMetersByServiceLocation(String serviceLocation, MultispeakVendor mspVendor);

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
     * @return
     */
    public ErrorObject getNotFoundErrorObject(String objectID, String notFoundObjectType, String nounType,
            String method, String userName);

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
     * Returns a list of SubstationNames for the vendor.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty String List object is returned.
     * 
     * @param mspVendor
     * @return
     */
    public List<String> getMspSubstationName(MultispeakVendor mspVendor);

    /**
     * Returns a list of the MeterNumber(s) for the eaLocation.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * 
     * @param eaLocation The eaLocation to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<com.cannontech.msp.beans.v3.Meter> getMspMetersByEALocation(String eaLocation,
            MultispeakVendor mspVendor);

    /**
     * Returns a list of the MeterNumber(s) for the facilityId.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * 
     * @param facilityId The facilityId to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<com.cannontech.msp.beans.v3.Meter> getMspMetersByFacilityId(String facilityId,
            MultispeakVendor mspVendor);

    /**
     * Returns a list of the MeterNumber(s) for the accountNumber.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * 
     * @param accountNumber The accountNumber to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<com.cannontech.msp.beans.v3.Meter> getMspMetersByAccountNumber(String accountNumber,
            MultispeakVendor mspVendor);

    /**
     * Returns a list of the MeterNumber(s) for the custId.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * 
     * @param custId The custId to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<com.cannontech.msp.beans.v3.Meter> getMspMetersByCustId(String custId, MultispeakVendor mspVendor);

    /**
     * Utility to implement the pingURL method for the service.
     * 
     * @param mspVendor The multispeak vendor to invoke.
     * @param service The string representation of the webservice to run.
     * @return Returns an ArrayOfErrorObjects
     * @throws MultispeakWebServiceClientException
     */
    public ErrorObject[] pingURL(MultispeakVendor mspVendor, String service) throws MultispeakWebServiceClientException;

    /**
     * Returns a list of supported method names for mspVendor
     * Catches any MultispeakWebServiceClientException and returns emptyList.
     * 
     * @param mspVendor
     * @return
     */
    public List<String> findMethods(String mspServer, MultispeakVendor mspVendor);

    /**
     * Utility to implement the getMethods method for the service.
     * 
     * @param mspVendor The multispeak vendor to invoke.
     * @param service The string representation of the webservice to run.
     * @return Returns an ArrayOfErrorObjects
     * @throws MultispeakWebServiceClientException
     */
    public List<String> getMethods(MultispeakVendor mspVendor, String service)
            throws MultispeakWebServiceClientException;
}
