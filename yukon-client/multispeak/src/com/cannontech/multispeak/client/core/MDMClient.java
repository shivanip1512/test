package com.cannontech.multispeak.client.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class MDMClient implements IMDMClient {
    private WebServiceTemplate webServiceTemplate;
    @Autowired private CustomWebServiceMsgCallback customWebServiceMsgCallback;
    @Autowired private MultispeakFuncs multispeakFuncs;
    /**
     * MDMClient Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public MDMClient(@Qualifier("webServiceTemplate") WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public PingURLResponse pingURL(final MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
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
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (GetMethodsResponse) webServiceTemplate.marshalSendAndReceive(uri, getMethods,
                customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }
}
