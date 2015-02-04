package com.cannontech.multispeak.client.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.InitiateOutageDetectionEventRequest;
import com.cannontech.msp.beans.v3.InitiateOutageDetectionEventRequestResponse;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class ODClient implements IODClient {
    private WebServiceTemplate webServiceTemplate;
    private HttpComponentsMessageSender messageSender;

    /**
     * ODClient Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public ODClient(@Qualifier("webServiceTemplate") WebServiceTemplate webServiceTemplate) {

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
    public InitiateOutageDetectionEventRequestResponse initiateOutageDetectionEventRequest(MultispeakVendor mspVendor,
            String uri, InitiateOutageDetectionEventRequest initiateOutageDetectionEventRequest)
            throws MultispeakWebServiceClientException {
        try {
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            return (InitiateOutageDetectionEventRequestResponse) webServiceTemplate.marshalSendAndReceive(uri,
                initiateOutageDetectionEventRequest, new CustomWebServiceMsgCallback().addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

}
