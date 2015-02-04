package com.cannontech.multispeak.client.core;

import com.cannontech.msp.beans.v3.CancelDisconnectedStatus;
import com.cannontech.msp.beans.v3.CancelDisconnectedStatusResponse;
import com.cannontech.msp.beans.v3.CancelUsageMonitoring;
import com.cannontech.msp.beans.v3.CancelUsageMonitoringResponse;
import com.cannontech.msp.beans.v3.DeleteMeterGroup;
import com.cannontech.msp.beans.v3.DeleteMeterGroupResponse;
import com.cannontech.msp.beans.v3.EstablishMeterGroup;
import com.cannontech.msp.beans.v3.EstablishMeterGroupResponse;
import com.cannontech.msp.beans.v3.GetAMRSupportedMeters;
import com.cannontech.msp.beans.v3.GetAMRSupportedMetersResponse;
import com.cannontech.msp.beans.v3.GetLatestReadingByMeterNo;
import com.cannontech.msp.beans.v3.GetLatestReadingByMeterNoAndType;
import com.cannontech.msp.beans.v3.GetLatestReadingByMeterNoAndTypeResponse;
import com.cannontech.msp.beans.v3.GetLatestReadingByMeterNoResponse;
import com.cannontech.msp.beans.v3.GetLatestReadingByType;
import com.cannontech.msp.beans.v3.GetLatestReadingByTypeResponse;
import com.cannontech.msp.beans.v3.GetLatestReadings;
import com.cannontech.msp.beans.v3.GetLatestReadingsResponse;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.GetReadingsByDate;
import com.cannontech.msp.beans.v3.GetReadingsByDateAndType;
import com.cannontech.msp.beans.v3.GetReadingsByDateAndTypeResponse;
import com.cannontech.msp.beans.v3.GetReadingsByDateResponse;
import com.cannontech.msp.beans.v3.GetReadingsByMeterNo;
import com.cannontech.msp.beans.v3.GetReadingsByMeterNoAndType;
import com.cannontech.msp.beans.v3.GetReadingsByMeterNoAndTypeResponse;
import com.cannontech.msp.beans.v3.GetReadingsByMeterNoResponse;
import com.cannontech.msp.beans.v3.GetSupportedReadingTypes;
import com.cannontech.msp.beans.v3.GetSupportedReadingTypesResponse;
import com.cannontech.msp.beans.v3.InitiateDemandReset;
import com.cannontech.msp.beans.v3.InitiateDemandResetResponse;
import com.cannontech.msp.beans.v3.InitiateDisconnectedStatus;
import com.cannontech.msp.beans.v3.InitiateDisconnectedStatusResponse;
import com.cannontech.msp.beans.v3.InitiateMeterReadByMeterNoAndType;
import com.cannontech.msp.beans.v3.InitiateMeterReadByMeterNoAndTypeResponse;
import com.cannontech.msp.beans.v3.InitiateUsageMonitoring;
import com.cannontech.msp.beans.v3.InitiateUsageMonitoringResponse;
import com.cannontech.msp.beans.v3.InsertMeterInMeterGroup;
import com.cannontech.msp.beans.v3.InsertMeterInMeterGroupResponse;
import com.cannontech.msp.beans.v3.IsAMRMeter;
import com.cannontech.msp.beans.v3.IsAMRMeterResponse;
import com.cannontech.msp.beans.v3.MeterAddNotification;
import com.cannontech.msp.beans.v3.MeterAddNotificationResponse;
import com.cannontech.msp.beans.v3.MeterChangedNotification;
import com.cannontech.msp.beans.v3.MeterChangedNotificationResponse;
import com.cannontech.msp.beans.v3.MeterRemoveNotification;
import com.cannontech.msp.beans.v3.MeterRemoveNotificationResponse;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.msp.beans.v3.RemoveMetersFromMeterGroup;
import com.cannontech.msp.beans.v3.RemoveMetersFromMeterGroupResponse;
import com.cannontech.msp.beans.v3.ServiceLocationChangedNotification;
import com.cannontech.msp.beans.v3.ServiceLocationChangedNotificationResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public interface IMRClient {

    /**
     * Pings the URL.
     * 
     * @param String the URI of the MR Server
     * @param PingURL the PingURL used as input.
     * @return PingURLResponse
     */
    public PingURLResponse pingURL(MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException;

    /**
     * Get all the supported methods.
     * 
     * @param String the URI of the MR Server
     * @param GetMethods the GetMethods used as input.
     * @return GetMethodsResponse
     */
    public GetMethodsResponse getMethods(MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException;

    /**
     * initiateMeterReadByMeterNoAndType
     * 
     * @param mspVendor
     * @param uri
     * @param initiateMeterReadByMeterNoAndType
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public InitiateMeterReadByMeterNoAndTypeResponse initiateMeterReadByMeterNoAndType(MultispeakVendor mspVendor,
            String uri, InitiateMeterReadByMeterNoAndType initiateMeterReadByMeterNoAndType)
            throws MultispeakWebServiceClientException;

    /**
     * initiate Demand Reset
     * 
     * @param mspVendor
     * @param uri
     * @param initiateDemandReset
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public InitiateDemandResetResponse initiateDemandReset(MultispeakVendor mspVendor, String uri,
            InitiateDemandReset initiateDemandReset) throws MultispeakWebServiceClientException;

    /**
     * is AMR Meter
     * 
     * @param mspVendor
     * @param uri
     * @param isAMRMeter
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public IsAMRMeterResponse isAMRMeter(MultispeakVendor mspVendor, String uri, IsAMRMeter isAMRMeter)
            throws MultispeakWebServiceClientException;

    /**
     * get Readings By Date
     * 
     * @param mspVendor
     * @param uri
     * @param getReadingsByDate
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public GetReadingsByDateResponse getReadingsByDate(MultispeakVendor mspVendor, String uri,
            GetReadingsByDate getReadingsByDate) throws MultispeakWebServiceClientException;

    /**
     * get AMR Supported Meters
     * 
     * @param mspVendor
     * @param uri
     * @param getAMRSupportedMeters
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public GetAMRSupportedMetersResponse getAMRSupportedMeters(MultispeakVendor mspVendor, String uri,
            GetAMRSupportedMeters getAMRSupportedMeters) throws MultispeakWebServiceClientException;

    /**
     * get Latest Reading By MeterNo
     * 
     * @param mspVendor
     * @param uri
     * @param getLatestReadingByMeterNo
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public GetLatestReadingByMeterNoResponse getLatestReadingByMeterNo(MultispeakVendor mspVendor, String uri,
            GetLatestReadingByMeterNo getLatestReadingByMeterNo) throws MultispeakWebServiceClientException;

    /**
     * get Latest Readings
     * 
     * @param mspVendor
     * @param uri
     * @param GetLatestReadings
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public GetLatestReadingsResponse getLatestReadings(MultispeakVendor mspVendor, String uri,
            GetLatestReadings getLatestReadings) throws MultispeakWebServiceClientException;

    /**
     * get Latest Reading By Meter No And Type
     * 
     * @param mspVendor
     * @param uri
     * @param GetLatestReadingByMeterNoAndType
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public GetLatestReadingByMeterNoAndTypeResponse getLatestReadingByMeterNoAndType(MultispeakVendor mspVendor, String uri,
            GetLatestReadingByMeterNoAndType getLatestReadingByMeterNoAndType)
            throws MultispeakWebServiceClientException;

    /**
     * get Latest Reading By Type
     * 
     * @param mspVendor
     * @param uri
     * @param getLatestReadingByType
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public GetLatestReadingByTypeResponse getLatestReadingByType(MultispeakVendor mspVendor, String uri,
            GetLatestReadingByType getLatestReadingByType) throws MultispeakWebServiceClientException;

    /**
     * get Readings By Meter No
     * 
     * @param mspVendor
     * @param uri
     * @param getReadingsByMeterNo
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public GetReadingsByMeterNoResponse getReadingsByMeterNo(MultispeakVendor mspVendor, String uri,
            GetReadingsByMeterNo getReadingsByMeterNo) throws MultispeakWebServiceClientException;

    /**
     * get Readings By Date And Type
     * 
     * @param mspVendor
     * @param uri
     * @param getReadingsByDateAndType
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public GetReadingsByDateAndTypeResponse getReadingsByDateAndType(MultispeakVendor mspVendor, String uri,
            GetReadingsByDateAndType getReadingsByDateAndType) throws MultispeakWebServiceClientException;

    /**
     * get Readings By Meter No And Type
     * 
     * @param mspVendor
     * @param uri
     * @param getReadingsByMeterNoAndType
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public GetReadingsByMeterNoAndTypeResponse getReadingsByMeterNoAndType(MultispeakVendor mspVendor, String uri,
            GetReadingsByMeterNoAndType getReadingsByMeterNoAndType) throws MultispeakWebServiceClientException;

    /**
     * get Supported Reading Types
     * 
     * @param mspVendor
     * @param uri
     * @param getSupportedReadingTypes
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public GetSupportedReadingTypesResponse getSupportedReadingTypes(MultispeakVendor mspVendor, String uri,
            GetSupportedReadingTypes getSupportedReadingTypes) throws MultispeakWebServiceClientException;

    /**
     * meter Add Notification
     * 
     * @param mspVendor
     * @param uri
     * @param meterAddNotification
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public MeterAddNotificationResponse meterAddNotification(MultispeakVendor mspVendor, String uri,
            MeterAddNotification meterAddNotification) throws MultispeakWebServiceClientException;

    /**
     * meter Remove Notification
     * 
     * @param mspVendor
     * @param uri
     * @param meterRemoveNotification
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public MeterRemoveNotificationResponse meterRemoveNotification(MultispeakVendor mspVendor, String uri,
            MeterRemoveNotification meterRemoveNotification) throws MultispeakWebServiceClientException;

    /**
     * meter Changed Notification
     * 
     * @param mspVendor
     * @param uri
     * @param meterChangedNotification
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public MeterChangedNotificationResponse meterChangedNotification(MultispeakVendor mspVendor, String uri,
            MeterChangedNotification meterChangedNotification) throws MultispeakWebServiceClientException;

    /**
     * initiate Usage Monitoring
     * 
     * @param mspVendor
     * @param uri
     * @param initiateUsageMonitoring
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public InitiateUsageMonitoringResponse initiateUsageMonitoring(MultispeakVendor mspVendor, String uri,
            InitiateUsageMonitoring initiateUsageMonitoring) throws MultispeakWebServiceClientException;

    /**
     * cancel Usage Monitoring
     * 
     * @param mspVendor
     * @param uri
     * @param cancelUsageMonitoring
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public CancelUsageMonitoringResponse cancelUsageMonitoring(MultispeakVendor mspVendor, String uri,
            CancelUsageMonitoring cancelUsageMonitoring) throws MultispeakWebServiceClientException;

    /**
     * initiate Disconnected Status
     * 
     * @param mspVendor
     * @param uri
     * @param initiateDisconnectedStatus
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public InitiateDisconnectedStatusResponse initiateDisconnectedStatus(MultispeakVendor mspVendor, String uri,
            InitiateDisconnectedStatus initiateDisconnectedStatus) throws MultispeakWebServiceClientException;

    /**
     * cancel Disconnected Status
     * 
     * @param mspVendor
     * @param uri
     * @param cancelDisconnectedStatus
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public CancelDisconnectedStatusResponse cancelDisconnectedStatus(MultispeakVendor mspVendor, String uri,
            CancelDisconnectedStatus cancelDisconnectedStatus) throws MultispeakWebServiceClientException;

    /**
     * service Location Changed Notification
     * 
     * @param mspVendor
     * @param uri
     * @param serviceLocationChangedNotification
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public ServiceLocationChangedNotificationResponse serviceLocationChangedNotification(MultispeakVendor mspVendor,
            String uri, ServiceLocationChangedNotification serviceLocationChangedNotification)
            throws MultispeakWebServiceClientException;

    /**
     * delete Meter Group
     * 
     * @param mspVendor
     * @param uri
     * @param deleteMeterGroup
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public DeleteMeterGroupResponse deleteMeterGroup(MultispeakVendor mspVendor, String uri,
            DeleteMeterGroup deleteMeterGroup) throws MultispeakWebServiceClientException;

    /**
     * establish Meter Group
     * 
     * @param mspVendor
     * @param uri
     * @param establishMeterGroup
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public EstablishMeterGroupResponse establishMeterGroup(MultispeakVendor mspVendor, String uri,
            EstablishMeterGroup establishMeterGroup) throws MultispeakWebServiceClientException;

    /**
     * 
     * insert Meter In Meter Group
     * 
     * @param mspVendor
     * @param uri
     * @param insertMeterInMeterGroup
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public InsertMeterInMeterGroupResponse insertMeterInMeterGroup(MultispeakVendor mspVendor, String uri,
            InsertMeterInMeterGroup insertMeterInMeterGroup) throws MultispeakWebServiceClientException;

    /**
     * remove Meters From Meter Group
     * 
     * @param mspVendor
     * @param uri
     * @param removeMetersFromMeterGroup
     * @return
     * @throws MultispeakWebServiceClientException
     */
    public RemoveMetersFromMeterGroupResponse removeMetersFromMeterGroup(MultispeakVendor mspVendor, String uri,
            RemoveMetersFromMeterGroup removeMetersFromMeterGroup) throws MultispeakWebServiceClientException;

}
