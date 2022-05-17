package com.cannontech.multispeak.service.v4;

import java.util.List;

import com.cannontech.msp.beans.v4.ConnectDisconnectEvent;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.ExpirationTime;
import com.cannontech.msp.beans.v4.CDState;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.Meters;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface CD_Server {
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
    public List<String> getMethods() throws MultispeakWebServiceException;;

    /**
     * Returns Current State of a Connect/Disconnect Device for a
     * given the Meter Number. The default condition is Closed.
     */
    public CDState getCDMeterState(MeterID meterID) throws MultispeakWebServiceException;

    /**
     * Returns all meters that have Connect/Disconnect Capability.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks. lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked. When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.
     */
    public Meters getCDSupportedMeters(String lastReceived) throws MultispeakWebServiceException;
    
    /**
     * CB initiates a connect or disconnect action by issuing one
     * or more connectDisconnectEvent objects to the CD. CD returns information
     * about failed transactions by returning an array of errorObjects.
     * The connect/disconnect function returns infromation about this action
     * using the CDStateChangedNotification to the URL specified in the responseURL
     * calling parameter and references the transactionID specified to link
     * the transaction to this Initiate request.The expiration time parameter
     * indicates the amount of time for which the publisher should try to
     * obtain and publish the data; if the publisher has been unsuccessful
     * in publishing the data after the expiration time (specified in seconds),
     * then the publisher will discard the request and the requestor should
     * not expect a response.
     */
    public List<ErrorObject> initiateConnectDisconnect(List<ConnectDisconnectEvent> cdEvents, String responseURL,
            String transactionId, ExpirationTime expirationTime) throws MultispeakWebServiceException;
}
