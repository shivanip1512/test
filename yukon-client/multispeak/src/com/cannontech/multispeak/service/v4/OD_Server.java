package com.cannontech.multispeak.service.v4;

import java.util.Calendar;
import java.util.List;

import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface OD_Server {
    /**
     * ping URL.
     * 
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public void pingURL() throws MultispeakWebServiceException;

    /**
     * get Methods.
     * 
     * @return the methods
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<String> getMethods() throws MultispeakWebServiceException;
    
    /**
     * init.
     * 
     * @param meterIDs the meter IDs
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiateOutageDetectionEventRequest(List<MeterID> meterIDs, Calendar requestDate,
            String responseURL, String transactionID, Float expirationTime) throws MultispeakWebServiceException;
    
    
}
