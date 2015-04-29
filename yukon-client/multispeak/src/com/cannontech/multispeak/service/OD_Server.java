package com.cannontech.multispeak.service;

import java.util.Calendar;
import java.util.List;

import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface OD_Server {

    /**
     * Requester pings URL of OD to see if it is alive. Returns errorObject(s)
     * as necessary to communicate application status.
     * @throws MultispeakWebServiceException 
     */
    public void pingURL() throws MultispeakWebServiceException;

    /**
     * Requester requests list of methods supported by OD.
     */
    public List<String> getMethods() throws MultispeakWebServiceException;

    /**
     * Client requests server to update the status of an outageDetectionDevice.
     * OD responds by publishing a revised outageDetectionEvent (using the
     * ODEventNotification method on OA-OD) to the URL specified in the responseURL
     * parameter. OD returns information about failed transactions using
     * an array of errorObjects. The transactionID calling parameter is included
     * to link a returned ODEventNotification with this request.The expiration
     * time parameter indicates the amount of time for which the publisher
     * should try to obtain and publish the data; if the publisher has been
     * unsuccessful in publishing the data after the expiration time (specified
     * in seconds), then the publisher will discard the request and the requestor
     * should not expect a response.
     */
    
    public List<ErrorObject> initiateOutageDetectionEventRequest(List<String> meterNos, Calendar requestDate,
            String responseURL, String transactionID, Float expirationTime) throws MultispeakWebServiceException;
}
