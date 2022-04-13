package com.cannontech.multispeak.service.v4;

import java.util.List;

import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import java.util.Calendar;
import java.lang.String;

public interface MR_Server {

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
     * get Readings By Date.
     * 
     * @param startDate    the start date
     * @param endDate      the end date
     * @param lastReceived the last received
     * @return the readings by date
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<MeterReading> getReadingsByDate(Calendar startDate, Calendar endDate,
            String lastReceived) throws MultispeakWebServiceException;

}
