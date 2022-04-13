package com.cannontech.multispeak.client.core.v4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class EAClient implements IEAClient {

    private WebServiceTemplate webServiceTemplate;
    @Autowired CustomWebServiceMsgCallback customWebServiceMsgCallback;
    @Autowired private MultispeakFuncs multispeakFuncs;
    /**
     * EAClient Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public EAClient(@Qualifier("webServiceTemplateV4") WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }
    
    @Override
    public PingURLResponse pingURL(MultispeakVendor mspVendor, String uri, PingURL pingURL)
            throws MultispeakWebServiceClientException {

        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (PingURLResponse) webServiceTemplate.marshalSendAndReceive(uri, pingURL,
                customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.EA_Server_STR));
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
                customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.EA_Server_STR));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    
    }


}
