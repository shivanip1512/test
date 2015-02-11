package com.cannontech.multispeak.client.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.cannontech.msp.beans.v3.CDDeviceAddNotification;
import com.cannontech.msp.beans.v3.CDDeviceAddNotificationResponse;
import com.cannontech.msp.beans.v3.GetCDMeterState;
import com.cannontech.msp.beans.v3.GetCDMeterStateResponse;
import com.cannontech.msp.beans.v3.GetCDSupportedMeters;
import com.cannontech.msp.beans.v3.GetCDSupportedMetersResponse;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.InitiateConnectDisconnect;
import com.cannontech.msp.beans.v3.InitiateConnectDisconnectResponse;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

/**
 * Spring SOAP Web service Client for making web service requests to CD_Server implemented methods
 * 
 *  @author Dinesh Angolkar
 */
public class CDClient implements ICDClient {

    private WebServiceTemplate webServiceTemplate;
    private HttpComponentsMessageSender messageSender;

    /**
     * CD Client Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public CDClient(@Qualifier("webServiceTemplate") WebServiceTemplate webServiceTemplate) {

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
    public GetCDSupportedMetersResponse getCDSupportedMeters(final MultispeakVendor mspVendor, String uri,
            GetCDSupportedMeters getCDSupportedMeters) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetCDSupportedMetersResponse) webServiceTemplate.marshalSendAndReceive(uri, getCDSupportedMeters,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public InitiateConnectDisconnectResponse initiateConnectDisconnect(final MultispeakVendor mspVendor, String uri,
            InitiateConnectDisconnect initiateConnectDisconnect) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (InitiateConnectDisconnectResponse) webServiceTemplate.marshalSendAndReceive(uri,
                initiateConnectDisconnect, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public GetCDMeterStateResponse getCDMeterState(final MultispeakVendor mspVendor, String uri,
            GetCDMeterState getCDMeterState) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (GetCDMeterStateResponse) webServiceTemplate.marshalSendAndReceive(uri, getCDMeterState,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public CDDeviceAddNotificationResponse cdDeviceAddNotification(final MultispeakVendor mspVendor, String uri,
            CDDeviceAddNotification cdDeviceAddNotification) throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (CDDeviceAddNotificationResponse) webServiceTemplate.marshalSendAndReceive(uri, cdDeviceAddNotification,
                new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }
    
}
