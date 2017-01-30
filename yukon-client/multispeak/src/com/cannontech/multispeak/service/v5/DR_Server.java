package com.cannontech.multispeak.service.v5;

import java.util.List;

import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.multispeak.LoadManagementEvent;
import com.cannontech.msp.beans.v5.multispeak.SubstationLoadControlStatus;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface DR_Server {

    /**
     * ping URL.
     * 
     * @return the error object[]
     * @throws MultispeakWebServiceException
     *         the multispeak web service exception
     */
    public void pingURL() throws MultispeakWebServiceException;

    /**
     * get Methods.
     * 
     * @return the methods
     * @throws MultispeakWebServiceException
     *         the multispeak web service exception
     */
    public List<String> getMethods() throws MultispeakWebServiceException;

    /**
     * get All Substation Load Control Statuses.
     * 
     * @return the all substation load control statuses
     * @throws MultispeakWebServiceException
     *         the multispeak web service exception
     */
    public List<SubstationLoadControlStatus> getAllSubstationLoadControlStatuses() throws MultispeakWebServiceException;

    /**
     * initiate Load Management Events.
     * 
     * @param theLMEvents
     *        the the lm events
     * @return the error object[]
     * @throws MultispeakWebServiceException
     *         the multispeak web service exception
     */
    public List<ErrorObject> initiateLoadManagementEvents(List<LoadManagementEvent> theLMEvents)
            throws MultispeakWebServiceException;

}
