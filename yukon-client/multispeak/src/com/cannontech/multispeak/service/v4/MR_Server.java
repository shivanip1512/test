package com.cannontech.multispeak.service.v4;

import java.util.Calendar;
import java.util.List;

import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.FormattedBlock;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.msp.beans.v4.Meters;
import com.cannontech.msp.beans.v4.MspMeter;
import com.cannontech.msp.beans.v4.ServiceLocation;
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
     * @param meterId   the Meter Id
     * @param startDate the start date
     * @param endDate   the end date
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

    /**
     * is AMR Meter.
     * 
     * @param meterNo the meter no
     * @return true, if is AMR meter
     * @throws MultispeakWebServiceException the multispeak web service
     *                                       exception
     */
    public boolean isAMRMeter(String meterNo) throws MultispeakWebServiceException;

    /**
     * initiate Usage Monitoring.
     * 
     * @param meterIDs the meter IDs
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiateUsageMonitoring(List<MeterID> meterIDs) throws MultispeakWebServiceException;

    /**
     * cancel Usage Monitoring.
     * 
     * @param meterIDs the meter IDs
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> cancelUsageMonitoring(List<MeterID> meterIDs) throws MultispeakWebServiceException;

    /**
     * get AMR Supported Meters.
     * 
     * @param lastReceived
     * @return
     */
    public Meters getAMRSupportedMeters(String lastReceived) throws MultispeakWebServiceException;

    /**
     * get Supported Field names against attributes
     * 
     * @return list of FieldNames
     */
    public List<String> getSupportedFieldNames() throws MultispeakWebServiceException;

    /**
     * get Readings By Date And FieldName.
     * 
     * @param startDate                  the start date
     * @param endDate                    the end date
     * @param lastReceived               the last received
     * @param formattedBlockTemplateName the formatted block template name
     * @return the readings by date and FieldName
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<FormattedBlock> getReadingsByDateAndFieldName(Calendar startDate, Calendar endDate, String lastReceived,
            String formattedBlockTemplateName)
            throws MultispeakWebServiceException;

    /**
     * get Latest Reading By FieldName.
     * 
     * @param lastReceived               the last received
     * @param formattedBlockTemplateName the formatted block template name
     * @return the latest reading by FieldName
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    List<FormattedBlock> getLatestReadingByFieldName(String lastReceived, String formattedBlockTemplateName)
            throws MultispeakWebServiceException;

    /**
     * get Latest Reading By Meter No And FieldName.
     * 
     * @param meterNo                    the meter no
     * @param formattedBlockTemplateName the formatted block template name
     * @return the latest reading by meter no and FieldName
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    FormattedBlock getLatestReadingByMeterIDAndFieldName(String meterNo, String formattedBlockTemplateName)
            throws MultispeakWebServiceException;

    /**
     * get Readings By Meter No And FieldName.
     * 
     * @param meterNo                    the meter no
     * @param startDate                  the start date
     * @param endDate                    the end date
     * @param lastReceived               the last received
     * @param formattedBlockTemplateName the formatted block template name
     * @return the readings by meter no and FieldName
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    List<FormattedBlock> getReadingsByMeterIDAndFieldName(String meterNo, Calendar startDate, Calendar endDate,
            String lastReceived, String formattedBlockTemplateName) throws MultispeakWebServiceException;

    /**
     * service Location Changed Notification.
     * 
     * @param changedServiceLocations the changed service locations
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> serviceLocationChangedNotification(List<ServiceLocation> serviceLocations) throws MultispeakWebServiceException;

    /**
     * meter Add Notification.
     * 
     * @param addedMeters the added meters
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> meterAddNotification(List<MspMeter> addedMeters) throws MultispeakWebServiceException;

    /**
     * meter Remove Notification.
     * 
     * @param removedMeters the removed meters
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> meterRemoveNotification(List<MspMeter> removedMeters) throws MultispeakWebServiceException;
}
