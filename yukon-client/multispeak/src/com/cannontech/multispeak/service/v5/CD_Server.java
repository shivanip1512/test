package com.cannontech.multispeak.service.v5;

import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.multispeak.CDDevice;
import com.cannontech.msp.beans.v5.multispeak.CDDeviceIdentifier;
import com.cannontech.msp.beans.v5.multispeak.CDState;
import com.cannontech.msp.beans.v5.multispeak.ConnectDisconnectEvent;
import com.cannontech.msp.beans.v5.multispeak.Meters;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface CD_Server {
    /**
     * Requester Pings URL of CD to see if it is alive. Returns
     * errorObject(s) as necessary to communicate application status.
     * 
     * @throws MultispeakWebServiceException
     */
    public void pingURL() throws MultispeakWebServiceException;

    /**
     * Requester requests list of methods supported by CD.
     */
    public List<String> getMethods() throws MultispeakWebServiceException;

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
    public List<CDDevice> getAllCDDevices(String lastReceived) throws MultispeakWebServiceException;

    /**
     * Returns Current State of a Connect/Disconnect Device for a
     * given the Meter Number. The default condition is Closed.
     */
    public List<CDState> getCDDeviceStates(List<CDDeviceIdentifier> meterNos) throws MultispeakWebServiceException;

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
            String transactionID, XMLGregorianCalendar expirationTime) throws MultispeakWebServiceException;

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
    Meters getCDEnabledMeters(String lastReceived) throws MultispeakWebServiceException;
}
