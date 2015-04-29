package com.cannontech.multispeak.service;

import java.util.List;

import com.cannontech.msp.beans.v3.ScadaAnalog;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface SCADA_Server {

    /**
     * ping URL
     * 
     * @return
     * @throws MultispeakWebServiceException
     */
    public void pingURL() throws MultispeakWebServiceException;

    /**
     * get Methods
     * 
     * @return
     * @throws MultispeakWebServiceException
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
