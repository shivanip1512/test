package com.cannontech.multispeak.client.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.cannontech.msp.beans.v3.CDStateChangedNotification;
import com.cannontech.msp.beans.v3.CDStateChangedNotificationResponse;
import com.cannontech.msp.beans.v3.FormattedBlockNotification;
import com.cannontech.msp.beans.v3.FormattedBlockNotificationResponse;
import com.cannontech.msp.beans.v3.GetAllServiceLocations;
import com.cannontech.msp.beans.v3.GetAllServiceLocationsResponse;
import com.cannontech.msp.beans.v3.GetCustomerByMeterNo;
import com.cannontech.msp.beans.v3.GetCustomerByMeterNoResponse;
import com.cannontech.msp.beans.v3.GetDomainMembers;
import com.cannontech.msp.beans.v3.GetDomainMembersResponse;
import com.cannontech.msp.beans.v3.GetMeterByAccountNumber;
import com.cannontech.msp.beans.v3.GetMeterByAccountNumberResponse;
import com.cannontech.msp.beans.v3.GetMeterByCustID;
import com.cannontech.msp.beans.v3.GetMeterByCustIDResponse;
import com.cannontech.msp.beans.v3.GetMeterByMeterNo;
import com.cannontech.msp.beans.v3.GetMeterByMeterNoResponse;
import com.cannontech.msp.beans.v3.GetMeterByServLoc;
import com.cannontech.msp.beans.v3.GetMeterByServLocResponse;
import com.cannontech.msp.beans.v3.GetMetersByEALocation;
import com.cannontech.msp.beans.v3.GetMetersByEALocationResponse;
import com.cannontech.msp.beans.v3.GetMetersByFacilityID;
import com.cannontech.msp.beans.v3.GetMetersByFacilityIDResponse;
import com.cannontech.msp.beans.v3.GetMetersBySearchString;
import com.cannontech.msp.beans.v3.GetMetersBySearchStringResponse;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.GetServiceLocationByMeterNo;
import com.cannontech.msp.beans.v3.GetServiceLocationByMeterNoResponse;
import com.cannontech.msp.beans.v3.MeterEventNotification;
import com.cannontech.msp.beans.v3.MeterEventNotificationResponse;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.msp.beans.v3.ReadingChangedNotification;
import com.cannontech.msp.beans.v3.ReadingChangedNotificationResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class CBClient implements ICBClient {
    private WebServiceTemplate webServiceTemplate;
    private HttpComponentsMessageSender messageSender;

    /**
     * CBClient Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public CBClient(@Qualifier("webServiceTemplate") WebServiceTemplate webServiceTemplate) {

        this.webServiceTemplate = webServiceTemplate;
        messageSender = (HttpComponentsMessageSender) webServiceTemplate.getMessageSenders()[0];

    }

    @Override
    public GetCustomerByMeterNoResponse getCustomerByMeterNo(final MultispeakVendor mspVendor, String uri,
            GetCustomerByMeterNo getCustomerByMeterNo) throws MultispeakWebServiceClientException {
        try {

            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());
            return (GetCustomerByMeterNoResponse) webServiceTemplate.marshalSendAndReceive(uri, getCustomerByMeterNo,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetServiceLocationByMeterNoResponse getServiceLocationByMeterNo(final MultispeakVendor mspVendor,
            String uri, GetServiceLocationByMeterNo getServiceLocationByMeterNo)
            throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetServiceLocationByMeterNoResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getServiceLocationByMeterNo, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetMeterByMeterNoResponse getMeterByMeterNo(final MultispeakVendor mspVendor, String uri,
            GetMeterByMeterNo getMeterByMeterNo) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetMeterByMeterNoResponse) webServiceTemplate.marshalSendAndReceive(uri, getMeterByMeterNo,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetAllServiceLocationsResponse getAllServiceLocations(final MultispeakVendor mspVendor, String uri,
            GetAllServiceLocations getAllServiceLocations) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetAllServiceLocationsResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getAllServiceLocations, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }

    }

    @Override
    public GetMeterByServLocResponse getMeterByServLoc(final MultispeakVendor mspVendor, String uri,
            GetMeterByServLoc getMeterByServLoc) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetMeterByServLocResponse) webServiceTemplate.marshalSendAndReceive(uri, getMeterByServLoc,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetDomainMembersResponse getDomainMembers(final MultispeakVendor mspVendor, String uri,
            GetDomainMembers getDomainMembers) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetDomainMembersResponse) webServiceTemplate.marshalSendAndReceive(uri, getDomainMembers,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetMetersByEALocationResponse getMetersByEALocation(final MultispeakVendor mspVendor, String uri,
            GetMetersByEALocation getMetersByEALocation) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetMetersByEALocationResponse) webServiceTemplate.marshalSendAndReceive(uri, getMetersByEALocation,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetMetersByFacilityIDResponse getMetersByFacilityID(final MultispeakVendor mspVendor, String uri,
            GetMetersByFacilityID getMetersByFacilityID) throws MultispeakWebServiceClientException {
        try {

            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetMetersByFacilityIDResponse) webServiceTemplate.marshalSendAndReceive(uri, getMetersByFacilityID,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetMeterByAccountNumberResponse getMeterByAccountNumber(final MultispeakVendor mspVendor, String uri,
            GetMeterByAccountNumber getMeterByAccountNumber) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetMeterByAccountNumberResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getMeterByAccountNumber, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetMeterByCustIDResponse getMeterByCustID(final MultispeakVendor mspVendor, String uri,
            GetMeterByCustID getMeterByCustID) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetMeterByCustIDResponse) webServiceTemplate.marshalSendAndReceive(uri, getMeterByCustID,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public PingURLResponse pingURL(final MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (PingURLResponse) webServiceTemplate.marshalSendAndReceive(uri, pingURL,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetMethodsResponse getMethods(final MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetMethodsResponse) webServiceTemplate.marshalSendAndReceive(uri, getMethods,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public ReadingChangedNotificationResponse readingChangedNotification(final MultispeakVendor mspVendor, String uri,
            ReadingChangedNotification readingChangedNotification) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (ReadingChangedNotificationResponse) webServiceTemplate.marshalSendAndReceive(uri,
                readingChangedNotification, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public FormattedBlockNotificationResponse formattedBlockNotification(final MultispeakVendor mspVendor, String uri,
            FormattedBlockNotification formattedBlockNotification) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (FormattedBlockNotificationResponse) webServiceTemplate.marshalSendAndReceive(uri,
                formattedBlockNotification, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public MeterEventNotificationResponse meterEventNotification(final MultispeakVendor mspVendor, String uri,
            MeterEventNotification meterEventNotification) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            MeterEventNotificationResponse response =
                (MeterEventNotificationResponse) webServiceTemplate.marshalSendAndReceive(uri, meterEventNotification,
                    new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
            return response;
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public CDStateChangedNotificationResponse cdStateChangedNotification(final MultispeakVendor mspVendor, String uri,
            CDStateChangedNotification cdStateChangedNotification) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            CDStateChangedNotificationResponse response =
                (CDStateChangedNotificationResponse) webServiceTemplate.marshalSendAndReceive(uri,
                    cdStateChangedNotification, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
            return response;
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }
    
    @Override
    public GetMetersBySearchStringResponse getMetersBySearchString(final MultispeakVendor mspVendor, String uri,
            GetMetersBySearchString getMetersBySearchString) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetMetersBySearchStringResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getMetersBySearchString, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

}
