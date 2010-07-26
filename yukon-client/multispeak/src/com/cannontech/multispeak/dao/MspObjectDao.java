package com.cannontech.multispeak.dao;

import java.rmi.RemoteException;
import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.ServiceLocation;

public interface MspObjectDao {

    /**
     * Returns a Msp Customer for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found, 
     * an empty Customer object is returned.
     * @param meter The Meter to get the Customer information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public Customer getMspCustomer(Meter meter, MultispeakVendor mspVendor);
    
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
     * Returns a Msp ServiceLocation for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty ServiceLocation object is returned.
     * @param meter The Meter to get the ServiceLocation information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public ServiceLocation getMspServiceLocation(Meter meter, MultispeakVendor mspVendor);

    /**
     * Returns a Msp ServiceLocation for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty ServiceLocation object is returned.
     * @param meterNumber The meter number to get the ServiceLocation information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public ServiceLocation getMspServiceLocation(String meterNumber, MultispeakVendor mspVendor);

    
    /**
     * Returns a Msp Meter for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty Meter object is returned.
     * @param meter The Meter to get the Meter information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public com.cannontech.multispeak.deploy.service.Meter getMspMeter(Meter meter, MultispeakVendor mspVendor);

    /**
     * Returns a Msp Meter for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty Meter object is returned.
     * @param meterNumber The meter number to get the Meter information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public com.cannontech.multispeak.deploy.service.Meter getMspMeter(String meterNumber, MultispeakVendor mspVendor);


    /**
     * Retrieves msp meters from the mspVendor.
     * Lists of msp meters are given to the callback as they are retrieved in chunks from the vendor.
     * @param mspVendor
     * @param callback
     * @throws Exception
     */
    public void getAllMspMeters(MultispeakVendor mspVendor, SimpleCallback<List<com.cannontech.multispeak.deploy.service.Meter>> callback) throws Exception;
    
    /**
     * Retrieves msp ServiceLocation objects from the mspVendor.
     * Lists of msp ServiceLocation objects are given to the callback as they are retrieved in chunks from the vendor.
     */
    public void getAllMspServiceLocations(MultispeakVendor mspVendor, MultispeakGetAllServiceLocationsCallback callback) throws RemoteException;
    
    /**
     * Returns a list of the MeterNumber(s) for the mspServiceLocation.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * @param mspServiceLocation The serviceLocation to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<com.cannontech.multispeak.deploy.service.Meter> getMspMetersByServiceLocation(ServiceLocation mspServiceLocation, MultispeakVendor mspVendor);
    
    /**
     * Returns a list of the MeterNumber(s) for the serviceLocation .
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * @param serviceLocation The serviceLocation to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<com.cannontech.multispeak.deploy.service.Meter> getMspMetersByServiceLocation(String serviceLocation, MultispeakVendor mspVendor);

    	
    /**
     * Creates a new (MSP) ErrorObject 
     * @param objectID The Multispeak objectID
     * @param errorMessage The error message.
     * @return
     */
    public ErrorObject getErrorObject(String objectID, String errorMessage, String nounType, String method, String userName);

    /**
     * Creates a new (MSP) ErrorObject 
     * @param objectID The Multispeak objectID
     * @param nounType The object type
     * @param notFoundObjectType The objectID type
     * @return
     */
    public ErrorObject getNotFoundErrorObject(String objectID, String notFoundObjectType, String nounType, String method, String userName);

    /**
     * Creates and ErrorObject array from errorObjects List
     * @param errorObjects
     * @return
     */
    public ErrorObject[] toErrorObject(List<ErrorObject> errorObjects);
    
    /**
     * Creates an entry in the System log and prints a debug statement.
     * @param method
     * @param description
     * @param userName
     */
    public void logMSPActivity(String method, String description, String userName);
    
    /**
     * Returns a list of SubstationNames for the vendor.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty String List object is returned.
     * @param mspVendor
     * @return
     */
    public List<String> getMspSubstationName(MultispeakVendor mspVendor);

    /**
     * Returns a list of supported method names for mspVendor
     * @param mspVendor
     * @return
     */
    public List<String> getMspMethods(String mspServer, MultispeakVendor mspVendor);

    /**
     * Returns a list of the MeterNumber(s) for the eaLocation.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * @param eaLocation The eaLocation to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<com.cannontech.multispeak.deploy.service.Meter> getMspMetersByEALocation(String eaLocation, MultispeakVendor mspVendor);

    /**
     * Returns a list of the MeterNumber(s) for the facilityId.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * @param facilityId The facilityId to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<com.cannontech.multispeak.deploy.service.Meter> getMspMetersByFacilityId(String facilityId, MultispeakVendor mspVendor);

    /**
     * Returns a list of the MeterNumber(s) for the accountNumber.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * @param accountNumber The accountNumber to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<com.cannontech.multispeak.deploy.service.Meter> getMspMetersByAccountNumber(String accountNumber, MultispeakVendor mspVendor);

    /**
     * Returns a list of the MeterNumber(s) for the custId.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * @param custId The custId to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<com.cannontech.multispeak.deploy.service.Meter> getMspMetersByCustId(String custId, MultispeakVendor mspVendor);

    /**
     * Utility to implement the pingURL method for the service.
     * @param mspVendor The multispeak vendor to invoke. 
     * @param service The string representation of the webservice to run. 
     * @return Returns an ArrayOfErrorObjects
     * @throws RemoteException 
     */
    public ErrorObject[] pingURL(MultispeakVendor mspVendor, String service) throws RemoteException;
    
    /**
     * Utility to implement the getMethods method for the service.
     * @param mspVendor The multispeak vendor to invoke. 
     * @param service The string representation of the webservice to run. 
     * @return Returns an ArrayOfErrorObjects
     * @throws RemoteException
     */
    public String[] getMethods(MultispeakVendor mspVendor, String service) throws RemoteException;
}
