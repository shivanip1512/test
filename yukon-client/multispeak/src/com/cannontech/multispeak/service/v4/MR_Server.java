package com.cannontech.multispeak.service.v4;

import java.util.Calendar;
import java.util.List;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

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

    /**
     * get Readings By Meter Id.
     * 
     * @param meterId the Meter Id
     * @param startDate the start date
     * @param endDate the end date
     * @return the readings by Meter Id
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<MeterReading> getReadingsByMeterID(String meterNo, Calendar startDate, Calendar endDate)
            throws MultispeakWebServiceException;

    /**
     * get Latest Readings.
     * 
     * @param lastReceived the last received
     * @return the latest readings
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<MeterReading> getLatestReadings(String lastReceived) throws MultispeakWebServiceException;

    
    /**
     * get Latest Reading By Meter Id.
     * 
     * @param meterNo the meter Id
     * @return the latest reading by meter id
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public MeterReading getLatestReadingByMeterID(String meterNo) throws MultispeakWebServiceException;

}
