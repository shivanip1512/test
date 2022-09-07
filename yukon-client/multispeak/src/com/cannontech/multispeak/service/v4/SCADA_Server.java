package com.cannontech.multispeak.service.v4;

import java.util.List;

import com.cannontech.msp.beans.v4.ScadaAnalog;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface SCADA_Server {
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
     * get All SCADA Analogs
     * 
     * @param lastReceived
     * @return
     * @throws MultispeakWebServiceException
     */
    public List<ScadaAnalog> getAllSCADAAnalogs(String lastReceived) throws MultispeakWebServiceException;

}
