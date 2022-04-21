package com.cannontech.multispeak.dao.v4;

import java.util.List;

import com.cannontech.msp.beans.v4.ErrorObject;
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
     * @param objectID: The Multispeak objectID
     * @param errorMessage
     * @return
     */
    public ErrorObject getErrorObject(String objectID, String errorMessage, String nounType, String method, String userName);
    
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
     * Creates an entry in the System log and prints a debug statement.
     * @param method
     * @param description
     * @param userName
     */
    public void logMSPActivity(String method, String description, String userName);

}