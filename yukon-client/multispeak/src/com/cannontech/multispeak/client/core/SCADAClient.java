package com.cannontech.multispeak.client.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.cannontech.msp.beans.v3.GetAllSCADAAnalogs;
import com.cannontech.msp.beans.v3.GetAllSCADAAnalogsResponse;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class SCADAClient implements ISCADAClient {
    private WebServiceTemplate webServiceTemplate;
    private HttpComponentsMessageSender messageSender;
    @Autowired private CustomWebServiceMsgCallback customWebServiceMsgCallback;
    /**
     * SCADA Client Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public SCADAClient(@Qualifier("webServiceTemplate") WebServiceTemplate webServiceTemplate) {

        this.webServiceTemplate = webServiceTemplate;
        messageSender = (HttpComponentsMessageSender) webServiceTemplate.getMessageSenders()[0];
    }

    @Override
    public PingURLResponse pingURL(final MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException {
        try {
            setMsgSenderTimeOutValues(mspVendor);

            return (PingURLResponse) webServiceTemplate.marshalSendAndReceive(uri, pingURL,
                customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public GetMethodsResponse getMethods(final MultispeakVendor mspVendor, String uri, GetMethods getMethods)
            throws MultispeakWebServiceClientException {
        try {
            setMsgSenderTimeOutValues(mspVendor);

            return (GetMethodsResponse) webServiceTemplate.marshalSendAndReceive(uri, getMethods,
                customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public GetAllSCADAAnalogsResponse getAllSCADAAnalogs(MultispeakVendor mspVendor, String uri,
            GetAllSCADAAnalogs getAllSCADAAnalogs) throws MultispeakWebServiceClientException {
        try {
            setMsgSenderTimeOutValues(mspVendor);

            return (GetAllSCADAAnalogsResponse) webServiceTemplate.marshalSendAndReceive(uri, getAllSCADAAnalogs,
                customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    private void setMsgSenderTimeOutValues(MultispeakVendor mspVendor) {
        int timeOut = (int) mspVendor.getRequestMessageTimeout();
        messageSender.setReadTimeout(timeOut);
        messageSender.setConnectionTimeout(timeOut);
    }
}
