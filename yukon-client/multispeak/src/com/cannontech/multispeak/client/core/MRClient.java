package com.cannontech.multispeak.client.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

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

public class MRClient implements IMRClient {
    private WebServiceTemplate webServiceTemplate;
    private HttpComponentsMessageSender messageSender;

    /**
     * MR Client Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public MRClient(@Qualifier("webServiceTemplate") WebServiceTemplate webServiceTemplate) {

        this.webServiceTemplate = webServiceTemplate;
        messageSender = (HttpComponentsMessageSender) webServiceTemplate.getMessageSenders()[0];
    }

    @Override
    public PingURLResponse pingURL(final MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (PingURLResponse) webServiceTemplate.marshalSendAndReceive(uri, pingURL,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public GetMethodsResponse getMethods(final MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetMethodsResponse) webServiceTemplate.marshalSendAndReceive(uri, getMethods,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public InitiateMeterReadByMeterNoAndTypeResponse initiateMeterReadByMeterNoAndType(MultispeakVendor mspVendor,
            String uri, InitiateMeterReadByMeterNoAndType initiateMeterReadByMeterNoAndType)
            throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (InitiateMeterReadByMeterNoAndTypeResponse) webServiceTemplate.marshalSendAndReceive(uri,
                initiateMeterReadByMeterNoAndType, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public InitiateDemandResetResponse initiateDemandReset(MultispeakVendor mspVendor, String uri,
            InitiateDemandReset initiateDemandReset) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (InitiateDemandResetResponse) webServiceTemplate.marshalSendAndReceive(uri, initiateDemandReset,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public IsAMRMeterResponse isAMRMeter(MultispeakVendor mspVendor, String uri, IsAMRMeter isAMRMeter)
            throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
            return (IsAMRMeterResponse) webServiceTemplate.marshalSendAndReceive(uri, isAMRMeter,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public GetReadingsByDateResponse getReadingsByDate(MultispeakVendor mspVendor, String uri,
            GetReadingsByDate getReadingsByDate) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetReadingsByDateResponse) webServiceTemplate.marshalSendAndReceive(uri, getReadingsByDate,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public GetAMRSupportedMetersResponse getAMRSupportedMeters(MultispeakVendor mspVendor, String uri,
            GetAMRSupportedMeters getAMRSupportedMeters) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetAMRSupportedMetersResponse) webServiceTemplate.marshalSendAndReceive(uri, getAMRSupportedMeters,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public GetLatestReadingByMeterNoResponse getLatestReadingByMeterNo(MultispeakVendor mspVendor, String uri,
            GetLatestReadingByMeterNo getLatestReadingByMeterNo) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetLatestReadingByMeterNoResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getLatestReadingByMeterNo, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public GetLatestReadingsResponse getLatestReadings(MultispeakVendor mspVendor, String uri,
            GetLatestReadings getLatestReadings) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetLatestReadingsResponse) webServiceTemplate.marshalSendAndReceive(uri, getLatestReadings,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public GetLatestReadingByMeterNoAndTypeResponse getLatestReadingByMeterNoAndType(MultispeakVendor mspVendor, String uri,
            GetLatestReadingByMeterNoAndType getLatestReadingByMeterNoAndType)
            throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetLatestReadingByMeterNoAndTypeResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getLatestReadingByMeterNoAndType, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public GetLatestReadingByTypeResponse getLatestReadingByType(MultispeakVendor mspVendor, String uri,
            GetLatestReadingByType getLatestReadingByType) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetLatestReadingByTypeResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getLatestReadingByType, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public GetReadingsByMeterNoResponse getReadingsByMeterNo(MultispeakVendor mspVendor, String uri,
            GetReadingsByMeterNo getReadingsByMeterNo) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetReadingsByMeterNoResponse) webServiceTemplate.marshalSendAndReceive(uri, getReadingsByMeterNo,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public GetReadingsByDateAndTypeResponse getReadingsByDateAndType(MultispeakVendor mspVendor, String uri,
            GetReadingsByDateAndType getReadingsByDateAndType) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetReadingsByDateAndTypeResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getReadingsByDateAndType, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public GetReadingsByMeterNoAndTypeResponse getReadingsByMeterNoAndType(MultispeakVendor mspVendor, String uri,
            GetReadingsByMeterNoAndType getReadingsByMeterNoAndType) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetReadingsByMeterNoAndTypeResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getReadingsByMeterNoAndType, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public GetSupportedReadingTypesResponse getSupportedReadingTypes(MultispeakVendor mspVendor, String uri,
            GetSupportedReadingTypes getSupportedReadingTypes) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetSupportedReadingTypesResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getSupportedReadingTypes, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public MeterAddNotificationResponse meterAddNotification(MultispeakVendor mspVendor, String uri,
            MeterAddNotification meterAddNotification) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (MeterAddNotificationResponse) webServiceTemplate.marshalSendAndReceive(uri, meterAddNotification,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public MeterRemoveNotificationResponse meterRemoveNotification(MultispeakVendor mspVendor, String uri,
            MeterRemoveNotification meterRemoveNotification) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (MeterRemoveNotificationResponse) webServiceTemplate.marshalSendAndReceive(uri,
                meterRemoveNotification, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public MeterChangedNotificationResponse meterChangedNotification(MultispeakVendor mspVendor, String uri,
            MeterChangedNotification meterChangedNotification) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (MeterChangedNotificationResponse) webServiceTemplate.marshalSendAndReceive(uri,
                meterChangedNotification, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public InitiateUsageMonitoringResponse initiateUsageMonitoring(MultispeakVendor mspVendor, String uri,
            InitiateUsageMonitoring initiateUsageMonitoring) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (InitiateUsageMonitoringResponse) webServiceTemplate.marshalSendAndReceive(uri,
                initiateUsageMonitoring, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public CancelUsageMonitoringResponse cancelUsageMonitoring(MultispeakVendor mspVendor, String uri,
            CancelUsageMonitoring cancelUsageMonitoring) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (CancelUsageMonitoringResponse) webServiceTemplate.marshalSendAndReceive(uri, cancelUsageMonitoring,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public InitiateDisconnectedStatusResponse initiateDisconnectedStatus(MultispeakVendor mspVendor, String uri,
            InitiateDisconnectedStatus initiateDisconnectedStatus) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (InitiateDisconnectedStatusResponse) webServiceTemplate.marshalSendAndReceive(uri,
                initiateDisconnectedStatus, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public CancelDisconnectedStatusResponse cancelDisconnectedStatus(MultispeakVendor mspVendor, String uri,
            CancelDisconnectedStatus cancelDisconnectedStatus) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (CancelDisconnectedStatusResponse) webServiceTemplate.marshalSendAndReceive(uri,
                cancelDisconnectedStatus, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public ServiceLocationChangedNotificationResponse serviceLocationChangedNotification(MultispeakVendor mspVendor,
            String uri, ServiceLocationChangedNotification serviceLocationChangedNotification)
            throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (ServiceLocationChangedNotificationResponse) webServiceTemplate.marshalSendAndReceive(uri,
                serviceLocationChangedNotification, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public DeleteMeterGroupResponse deleteMeterGroup(MultispeakVendor mspVendor, String uri,
            DeleteMeterGroup deleteMeterGroup) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (DeleteMeterGroupResponse) webServiceTemplate.marshalSendAndReceive(uri, deleteMeterGroup,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public EstablishMeterGroupResponse establishMeterGroup(MultispeakVendor mspVendor, String uri,
            EstablishMeterGroup establishMeterGroup) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (EstablishMeterGroupResponse) webServiceTemplate.marshalSendAndReceive(uri, establishMeterGroup,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public InsertMeterInMeterGroupResponse insertMeterInMeterGroup(MultispeakVendor mspVendor, String uri,
            InsertMeterInMeterGroup insertMeterInMeterGroup) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (InsertMeterInMeterGroupResponse) webServiceTemplate.marshalSendAndReceive(uri,
                insertMeterInMeterGroup, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public RemoveMetersFromMeterGroupResponse removeMetersFromMeterGroup(MultispeakVendor mspVendor, String uri,
            RemoveMetersFromMeterGroup removeMetersFromMeterGroup) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (RemoveMetersFromMeterGroupResponse) webServiceTemplate.marshalSendAndReceive(uri,
                removeMetersFromMeterGroup, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }
}
