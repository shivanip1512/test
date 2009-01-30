package com.cannontech.multispeak.dao;

import java.util.List;

import com.cannontech.amr.meter.model.Meter;
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
}
