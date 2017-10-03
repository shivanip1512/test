package com.cannontech.multispeak.service.v5;

import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.commontypes.MeterID;
import com.cannontech.msp.beans.v5.commontypes.ObjectID;
import com.cannontech.msp.beans.v5.multispeak.FormattedBlock;
import com.cannontech.msp.beans.v5.multispeak.MeterGroup;
import com.cannontech.msp.beans.v5.multispeak.MeterReading;
import com.cannontech.msp.beans.v5.multispeak.Meters;
import com.cannontech.msp.beans.v5.multispeak.ReadingTypeCode;
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
     * @param startDate
     * @param endDate
     * @param lastReceived
     * @return
     */
    public List<MeterReading> getMeterReadingsByDate(Calendar startDate, Calendar endDate, String lastReceived)
            throws MultispeakWebServiceException;

    /**
     * get AMR Supported Meters.
     * 
     * @param lastReceived
     * @return
     */
    public Meters getAMRSupportedMeters(String lastReceived) throws MultispeakWebServiceException;

    /**
     * get Latest Readings
     * 
     * @param lastRecd
     * @return
     */
    public List<MeterReading> getLatestMeterReadings(String lastRecd) throws MultispeakWebServiceException;

    /**
     * get Latest Meter Readings By Meter IDs.
     * 
     * @param meterIDs
     * @return
     */
    public List<MeterReading> getLatestMeterReadingsByMeterIDs(List<MeterID> meterIDs) throws MultispeakWebServiceException;

    /**
     * get Meter Readings By MeterIDs
     * 
     * @param meterIDs
     * @param startDate
     * @param endDate
     * @return
     * @throws MultispeakWebServiceException
     */
    List<MeterReading> getMeterReadingsByMeterIDs(List<MeterID> meterIDs, Calendar startDate, Calendar endDate)
            throws MultispeakWebServiceException;
    

    /**
     * get Meter Readings By Date And Reading Type codes.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param list of readingTypeCode the reading type code
     * @param lastReceived the last received
     * @param formattedBlockTemplateID the formatted block template name
     * @return the formattedBlock
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    List<FormattedBlock> getMeterReadingsByDateAndReadingTypeCodes(Calendar startDate, Calendar endDate,
            List<ReadingTypeCode> readingTypeCodes, String lastReceived, ObjectID formattedBlockTemplateID)
            throws MultispeakWebServiceException;

    /**
     * get Latest Meter Reading By Meters And Reading Type codes.
     * 
     * @param meterIDs - List of meters"/"MeterId's
     * @param readingType the reading type
     * @param formattedBlockTemplateName the formatted block template name
     * @return the latest reading by meter no and type
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public FormattedBlock getLatestMeterReadingsByMeterIDsAndReadingTypeCodes(List<MeterID> meterIDs,
            List<ReadingTypeCode> readingTypeCodes, ObjectID formattedBlockTemplateID)
            throws MultispeakWebServiceException;

    /**
     * get Meter Readings By meters And Type codes.
     * 
     * @param list of meters
     * @param startDate the start date
     * @param endDate the end date
     * @param readingType the reading type
     * @param formattedBlockTemplateID the formatted block template name
     * @return the readings by meters and type
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<FormattedBlock> getMeterReadingsByMeterIDsAndReadingTypeCodes(List<MeterID> meterIDs,
            Calendar startDate, Calendar endDate, List<ReadingTypeCode> readingTypeCodes,
            ObjectID formattedBlockTemplateID) throws MultispeakWebServiceException;

    /**
     * get Latest Meter Readings By Type codes.
     * 
     * @param list of readingType codes
     * @param lastReceived the last received
     * @param formattedBlockTemplateID the formatted block template name
     * @return the latest reading by type
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<FormattedBlock> getLatestMeterReadingsByReadingTypeCodes(List<ReadingTypeCode> readingTypeCodes,
            String lastReceived, ObjectID formattedBlockTemplateID) throws MultispeakWebServiceException;
    
    /**
     * initiate Meter Read By Meter ID.
     * @param meterIds the meter ids
     * @param responseURL the response url
     * @param transactionID the transaction id
     * @param expirationTime the expiration time
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service
     *             exception
     */
    public List<ErrorObject> initiateMeterReadingsByMeterIDs(List<MeterID> meterIds,
            String responseURL, String transactionID, XMLGregorianCalendar expirationTime)
            throws MultispeakWebServiceException;

    /**
     * initiate Meter Read By Reading Type.
     * @param meterIDs to contain multiple meter references
     * @param responseURL the response url
     * @param readingType the reading type
     * @param transactionID the transaction id
     * @param expirationTime the expiration time
     * @param formattedBlockTemplateID the formatted block TemplateID
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service
     *             exception
     */
    List<ErrorObject> initiateMeterReadingsByReadingTypeCodes(List<MeterID> meterIDs,
            String responseURL, List<ReadingTypeCode> readingTypeCodes, String transactionID,
            XMLGregorianCalendar expirationTime, ObjectID formattedBlockTemplateID)
            throws MultispeakWebServiceException;

    /**
     * is AMR Meter.
     * @param meterNo the meter no
     * @return true, if is AMR meter
     * @throws MultispeakWebServiceException the multispeak web service
     *             exception
     */
    public boolean isAMRMeter(String meterNo) throws MultispeakWebServiceException;

    /**
     * initiate End Device Usage Monitoring.
     * @param meterIDs the meter ids
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service
     *             exception
     */
    public List<ErrorObject> initiateEndDeviceEventMonitoring(List<MeterID> meterIDs)
            throws MultispeakWebServiceException;

    /**
     * cancel Usage Monitoring.
     * @param meterID the meter id
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service
     *             exception
     */
    public List<ErrorObject> cancelEndDeviceEventMonitoring(String meterID)
            throws MultispeakWebServiceException;

    /**
     * initiate Disconnected Status.
     * @param meterIDs the meter ids
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service
     *             exception
     */
    public List<ErrorObject> setDisconnectedStatus(List<MeterID> meterIDs)
            throws MultispeakWebServiceException;

    /**
     * clear Disconnected Status.
     * @param meterIDs the meter ids
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service
     *             exception
     */
    public List<ErrorObject> clearDisconnectedStatus(List<MeterID> meterIDs)
            throws MultispeakWebServiceException;

    /**
     * delete Meter Group.
     * @param objectID to contain multiple object references
     * @return the error object
     * @throws MultispeakWebServiceException the multispeak web service
     *             exception
     */
    public List<ErrorObject> deleteMeterGroups(List<ObjectID> objectID)
            throws MultispeakWebServiceException;

    /**
     * remove Meters From Meter Group.
     * @param meterNumbers the meter numbers
     * @param meterGroupID the meter group id
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service
     *             exception
     */
    public List<ErrorObject> removeMetersFromMeterGroup(List<MeterID> meterIDs, String meterGroupID)
            throws MultispeakWebServiceException;

    /**
     * create Meter Groups.
     * @param meterGroups the meter groups
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service
     *             exception
     */
    public List<ErrorObject> createMeterGroups(List<MeterGroup> meterGroups)
            throws MultispeakWebServiceException;

    /**
     * insert Meters In Meter Group.
     * @param meterID the meter ids
     * @param meterGroupID the meter group id
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service
     *             exception
     */
    public List<ErrorObject> insertMeterInMeterGroup(List<MeterID> meterNumbers, String meterGroupID)
            throws MultispeakWebServiceException;

    /**
     * initiate Demand Reset.
     * @param meterIDs the meter ids
     * @param responseURL the response url
     * @param transactionId the transaction id
     * @param expirationTime the expiration time
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service
     *             exception
     */
    public List<ErrorObject> initiateDemandReset(List<MeterID> meterIDs, String responseURL,
            String transactionId, XMLGregorianCalendar expirationTime) throws MultispeakWebServiceException;
}
