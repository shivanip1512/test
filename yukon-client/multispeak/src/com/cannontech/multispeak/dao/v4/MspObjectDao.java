package com.cannontech.multispeak.dao.v4;

import java.util.List;

import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.MspMeter;
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
     * Creates an entry in the System log and prints a debug statement.
     * TODO All of this logging (SystemLog) can be removed following completion of MultiSpeak EventLogs.
     * 
     * @param method
     * @param description
     * @param userName
     */
    public void logMSPActivity(String method, String description, String userName);
    
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
     * Returns a list of the MeterNumber(s) for the mspServiceLocation.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty List<Meter> object is returned.
     * @param mspServiceLocation The serviceLocation to get the Meter information for.
     * @param mspVendor The MultiSpeak Vendor to ask for the information from.
     * @return
     */
    public List<MspMeter> getMspMetersByServiceLocation(ServiceLocation mspServiceLocation, MultispeakVendor vendor);

}