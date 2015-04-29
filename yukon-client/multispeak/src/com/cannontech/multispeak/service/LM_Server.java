package com.cannontech.multispeak.service;

import java.util.List;

import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.LoadManagementEvent;
import com.cannontech.msp.beans.v3.ScadaAnalog;
import com.cannontech.msp.beans.v3.SubstationLoadControlStatus;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface LM_Server {

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
     * SCADA Analog Changed Notification.
     * 
     * @param scadaAnalogs the scada analogs
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> SCADAAnalogChangedNotification(List<ScadaAnalog> scadaAnalogs)
            throws MultispeakWebServiceException;

    /**
     * get All Substation Load Control Statuses.
     * 
     * @return the all substation load control statuses
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<SubstationLoadControlStatus> getAllSubstationLoadControlStatuses() throws MultispeakWebServiceException;

    /**
     * initiate Load Management Event.
     * 
     * @param theLMEvent the the lm event
     * @return the error object
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public ErrorObject initiateLoadManagementEvent(LoadManagementEvent theLMEvent) throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param theLMEvents the the lm events
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiateLoadManagementEvents(List<LoadManagementEvent> theLMEvents)
            throws MultispeakWebServiceException;

}
