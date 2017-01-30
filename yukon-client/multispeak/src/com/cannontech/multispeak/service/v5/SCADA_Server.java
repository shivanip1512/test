package com.cannontech.multispeak.service.v5;

import java.util.List;

import com.cannontech.msp.beans.v5.multispeak.SCADAAnalog;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface SCADA_Server {

    /**
     * Ping URL
     * 
     * @throws MultispeakWebServiceException
     */
    public void pingURL() throws MultispeakWebServiceException;

    /**
     * Get Methods
     * 
     * @return list of methods
     * @throws MultispeakWebServiceException
     */
    public List<String> getMethods() throws MultispeakWebServiceException;

    /**
     * Get All SCADA Analogs
     * 
     * @param lastReceived
     * @return list of Scada Analog Point information
     * @throws MultispeakWebServiceException
     */
    public List<SCADAAnalog> getLatestSCADAAnalogs(String lastReceived) throws MultispeakWebServiceException; 
    
} 