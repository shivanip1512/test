package com.cannontech.multispeak.service;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.FormattedBlock;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.MeterGroup;
import com.cannontech.msp.beans.v3.MeterIdentifier;
import com.cannontech.msp.beans.v3.MeterRead;
import com.cannontech.msp.beans.v3.ServiceLocation;
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
    public List<String> getMethods() throws MultispeakWebServiceException;;

    /**
     * get AMR SupportedMeters.
     * 
     * @param lastReceived the last received
     * @return the AMR supported meters
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<Meter> getAMRSupportedMeters(java.lang.String lastReceived) throws MultispeakWebServiceException;

    /**
     * is AMR Meter.
     * 
     * @param meterNo the meter no
     * @return true, if is AMR meter
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public boolean isAMRMeter(java.lang.String meterNo) throws MultispeakWebServiceException;

    /**
     * get Readings By Date.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param lastReceived the last received
     * @return the readings by date
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<MeterRead> getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate,
            java.lang.String lastReceived) throws MultispeakWebServiceException;

    /**
     * get Readings By Meter No.
     * 
     * @param meterNo the meter no
     * @param startDate the start date
     * @param endDate the end date
     * @return the readings by meter no
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<MeterRead> getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate,
            java.util.Calendar endDate) throws MultispeakWebServiceException;

    /**
     * get Latest Reading By Meter No.
     * 
     * @param meterNo the meter no
     * @return the latest reading by meter no
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws MultispeakWebServiceException;

    /**
     * get Readings By Billing Cycle.
     * 
     * @param billingCycle the billing cycle
     * @param billingDate the billing date
     * @param kWhLookBack the k wh look back
     * @param kWLookBack the k w look back
     * @param kWLookForward the k w look forward
     * @param lastReceived the last received
     * @param formattedBlockTemplateName the formatted block template name
     * @param fieldName the field name
     * @return the readings by billing cycle
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<FormattedBlock> getReadingsByBillingCycle(String billingCycle, Calendar billingDate, int kWhLookBack,
            int kWLookBack, int kWLookForward, String lastReceived, String formattedBlockTemplateName) throws MultispeakWebServiceException;

    /**
     * initiate Usage Monitoring.
     * 
     * @param meterNos the meter nos
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiateUsageMonitoring(List<String> meterNos) throws MultispeakWebServiceException;

    /**
     * cancel Usage Monitoring.
     * 
     * @param meterNos the meter nos
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> cancelUsageMonitoring(List<String> meterNos) throws MultispeakWebServiceException;

    /**
     * initiate Disconnected Status.
     * 
     * @param meterNos the meter nos
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiateDisconnectedStatus(List<String> meterNos) throws MultispeakWebServiceException;

    /**
     * cancel Disconnected Status.
     * 
     * @param meterNos the meter nos
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> cancelDisconnectedStatus(List<String> meterNos) throws MultispeakWebServiceException;

    /**
     * initiate Meter Read By Meter Number.
     * 
     * @param meterNos the meter nos
     * @param responseURL the response url
     * @param transactionID the transaction id
     * @param expirationTime the expiration time
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiateMeterReadByMeterNumber(List<String> meterNos, String responseURL, String transactionID,
            Float expirationTime) throws MultispeakWebServiceException;

    /**
     * service Location Changed Notification.
     * 
     * @param changedServiceLocations the changed service locations
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> serviceLocationChangedNotification(List<ServiceLocation> changedServiceLocations)
            throws MultispeakWebServiceException;

    /**
     * meter Changed Notification.
     * 
     * @param changedMeters the changed meters
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> meterChangedNotification(List<Meter> changedMeters) throws MultispeakWebServiceException;

    /**
     * meter Remove Notification.
     * 
     * @param removedMeters the removed meters
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> meterRemoveNotification(List<Meter> removedMeters) throws MultispeakWebServiceException;

    /**
     * meter Add Notification.
     * 
     * @param addedMeters the added meters
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> meterAddNotification(List<Meter> addedMeters) throws MultispeakWebServiceException;

    /**
     * delete Meter Group.
     * 
     * @param meterGroupID the meter group id
     * @return the error object
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public ErrorObject deleteMeterGroup(String meterGroupID) throws MultispeakWebServiceException;

    /**
     * establish Meter Group.
     * 
     * @param meterGroup the meter group
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> establishMeterGroup(MeterGroup meterGroup) throws MultispeakWebServiceException;

    /**
     * insert Meter In Meter Group.
     * 
     * @param meterNumbers the meter numbers
     * @param meterGroupID the meter group id
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> insertMeterInMeterGroup(List<String> meterNumbers, String meterGroupID)
            throws MultispeakWebServiceException;

    /**
     * remove Meters From Meter Group.
     * 
     * @param meterNumbers the meter numbers
     * @param meterGroupID the meter group id
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> removeMetersFromMeterGroup(List<String> meterNumbers, String meterGroupID)
            throws MultispeakWebServiceException;

    /**
     * get Latest Readings.
     * 
     * @param lastReceived the last received
     * @return the latest readings
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<MeterRead> getLatestReadings(String lastReceived) throws MultispeakWebServiceException;

    /**
     * get Latest Reading By Meter No And Type.
     * 
     * @param meterNo the meter no
     * @param readingType the reading type
     * @param formattedBlockTemplateName the formatted block template name
     * @return the latest reading by meter no and type
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public FormattedBlock getLatestReadingByMeterNoAndType(String meterNo, String readingType,
            String formattedBlockTemplateName) throws MultispeakWebServiceException;

    /**
     * get Latest Reading By Type.
     * 
     * @param readingType the reading type
     * @param lastReceived the last received
     * @param formattedBlockTemplateName the formatted block template name
     * @return the latest reading by type
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<FormattedBlock> getLatestReadingByType(String readingType, String lastReceived,
            String formattedBlockTemplateName) throws MultispeakWebServiceException;

    /**
     * get Readings By Date And Type.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param readingType the reading type
     * @param lastReceived the last received
     * @param formattedBlockTemplateName the formatted block template name
     * @return the readings by date and type
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<FormattedBlock> getReadingsByDateAndType(Calendar startDate, Calendar endDate, String readingType,
            String lastReceived, String formattedBlockTemplateName)
            throws MultispeakWebServiceException;

    /**
     * get Readings By Meter No And Type.
     * 
     * @param meterNo the meter no
     * @param startDate the start date
     * @param endDate the end date
     * @param readingType the reading type
     * @param lastReceived the last received
     * @param formattedBlockTemplateName the formatted block template name
     * @param fieldName the field name
     * @return the readings by meter no and type
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<FormattedBlock> getReadingsByMeterNoAndType(String meterNo, Calendar startDate, Calendar endDate,
            String readingType, String lastReceived, String formattedBlockTemplateName)
            throws MultispeakWebServiceException;

    /**
     * get Supported Reading Types.
     * 
     * @return the supported reading types
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public Set<String> getSupportedReadingTypes() throws MultispeakWebServiceException;

    /**
     * initiate Meter Read By Meter No And Type.
     * 
     * @param meterNo the meter no
     * @param responseURL the response url
     * @param readingType the reading type
     * @param transactionID the transaction id
     * @param expirationTime the expiration time
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiateMeterReadByMeterNoAndType(String meterNo, String responseURL, String readingType,
            String transactionID, Float expirationTime) throws MultispeakWebServiceException;

    /**
     * initiate Demand Reset.
     * 
     * @param meterIDs the meter i ds
     * @param responseURL the response url
     * @param transactionId the transaction id
     * @param expirationTime the expiration time
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiateDemandReset(List<MeterIdentifier> meterIDs, String responseURL, String transactionId,
            Float expirationTime) throws MultispeakWebServiceException;

}
