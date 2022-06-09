package com.cannontech.multispeak.client.core.v4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.cannontech.msp.beans.v4.CDStateChangedNotification;
import com.cannontech.msp.beans.v4.CDStateChangedNotificationResponse;
import com.cannontech.msp.beans.v4.FormattedBlockNotification;
import com.cannontech.msp.beans.v4.FormattedBlockNotificationResponse;
import com.cannontech.msp.beans.v4.GetAllServiceLocations;
import com.cannontech.msp.beans.v4.GetAllServiceLocationsResponse;
import com.cannontech.msp.beans.v4.GetCustomerByMeterID;
import com.cannontech.msp.beans.v4.GetCustomerByMeterIDResponse;
import com.cannontech.msp.beans.v4.GetDomainMembers;
import com.cannontech.msp.beans.v4.GetDomainMembersResponse;
import com.cannontech.msp.beans.v4.GetMeterByMeterID;
import com.cannontech.msp.beans.v4.GetMeterByMeterIDResponse;
import com.cannontech.msp.beans.v4.GetMeterByServiceLocationID;
import com.cannontech.msp.beans.v4.GetMeterByServiceLocationIDResponse;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.GetServiceLocationByMeterID;
import com.cannontech.msp.beans.v4.GetServiceLocationByMeterIDResponse;
import com.cannontech.msp.beans.v4.MeterEventNotification;
import com.cannontech.msp.beans.v4.MeterEventNotificationResponse;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.msp.beans.v4.ReadingChangedNotification;
import com.cannontech.msp.beans.v4.ReadingChangedNotificationResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class CBClient implements ICBClient {

    private WebServiceTemplate webServiceTemplate;
    @Autowired CustomWebServiceMsgCallback customWebServiceMsgCallback;
    @Autowired private MultispeakFuncs multispeakFuncs;

    /**
     * CBClient Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public CBClient(@Qualifier("webServiceTemplateV4") WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public PingURLResponse pingURL(MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException {

        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (PingURLResponse) webServiceTemplate.marshalSendAndReceive(uri, pingURL,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }

    }

    @Override
    public GetMethodsResponse getMethods(MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (GetMethodsResponse) webServiceTemplate.marshalSendAndReceive(uri, getMethods,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }

    }
    
    @Override
    public GetMeterByMeterIDResponse getMeterByMeterID(final MultispeakVendor mspVendor, String uri,
            GetMeterByMeterID getMeterByMeterId) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (GetMeterByMeterIDResponse) webServiceTemplate.marshalSendAndReceive(uri, getMeterByMeterId,
                customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }
    
    @Override
    public GetCustomerByMeterIDResponse getCustomerByMeterId(final MultispeakVendor mspVendor, String uri,
            GetCustomerByMeterID getCustomerByMeterId) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (GetCustomerByMeterIDResponse) webServiceTemplate.marshalSendAndReceive(uri, getCustomerByMeterId,
                customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }
    
    @Override
    public GetServiceLocationByMeterIDResponse getServiceLocationByMeterId(final MultispeakVendor mspVendor,
            String uri, GetServiceLocationByMeterID getServiceLocationByMeterId)
            throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (GetServiceLocationByMeterIDResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getServiceLocationByMeterId, customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetAllServiceLocationsResponse getAllServiceLocations(MultispeakVendor mspVendor, String uri,
            GetAllServiceLocations getAllServiceLocations) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (GetAllServiceLocationsResponse) webServiceTemplate.marshalSendAndReceive(uri,
                getAllServiceLocations, customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetMeterByServiceLocationIDResponse getMeterByServiceLocationID(MultispeakVendor mspVendor, String endpointUrl,
            GetMeterByServiceLocationID getMeterByServLocID) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (GetMeterByServiceLocationIDResponse) webServiceTemplate.marshalSendAndReceive(endpointUrl,
                    getMeterByServLocID,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public GetDomainMembersResponse getDomainMembers(MultispeakVendor mspVendor, String uri, GetDomainMembers getDomainMembers)
            throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (GetDomainMembersResponse) webServiceTemplate.marshalSendAndReceive(uri, getDomainMembers,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    public GetServiceLocationByMeterIDResponse getServiceLocationByMeterID(MultispeakVendor mspVendor, String endpointUrl,
            GetServiceLocationByMeterID getServiceLocationByMspMeterID) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (GetServiceLocationByMeterIDResponse) webServiceTemplate.marshalSendAndReceive(endpointUrl,
                    getServiceLocationByMspMeterID,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }
    
    @Override
    public MeterEventNotificationResponse meterEventNotification(final MultispeakVendor mspVendor, String uri,
            MeterEventNotification meterEventNotification) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            MeterEventNotificationResponse response = (MeterEventNotificationResponse) webServiceTemplate.marshalSendAndReceive(
                    uri, meterEventNotification,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
            return response;
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public CDStateChangedNotificationResponse cdStateChangedNotification(final MultispeakVendor mspVendor, String uri,
            CDStateChangedNotification cdStateChangedNotification) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            CDStateChangedNotificationResponse response = (CDStateChangedNotificationResponse) webServiceTemplate.marshalSendAndReceive(
                                                               uri,
                                                               cdStateChangedNotification,
                                                               customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
            return response;
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public ReadingChangedNotificationResponse readingChangedNotification(final MultispeakVendor mspVendor, String uri,
            ReadingChangedNotification readingChangedNotification) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);;

            return (ReadingChangedNotificationResponse) webServiceTemplate.marshalSendAndReceive(uri,
                readingChangedNotification, customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    
    }
    
    @Override
    public FormattedBlockNotificationResponse formattedBlockNotification(MultispeakVendor mspVendor, String uri,
            String interfaceName,
            FormattedBlockNotification formattedBlockNotification) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (FormattedBlockNotificationResponse) webServiceTemplate.marshalSendAndReceive(uri,
                    formattedBlockNotification,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.CB_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }
}