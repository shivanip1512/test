package com.cannontech.multispeak.client.core.v5;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.cannontech.msp.beans.v5.commonarrays.ArrayOfString;
import com.cannontech.msp.beans.v5.mr_server.GetMethods;
import com.cannontech.msp.beans.v5.mr_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.mr_server.ObjectFactory;
import com.cannontech.msp.beans.v5.mr_server.PingURL;
import com.cannontech.msp.beans.v5.mr_server.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class MRClient implements IMRClient {
    private WebServiceTemplate webServiceTemplate;
    @Autowired private CustomWebServiceMsgCallback customWebServiceMsgCallback;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;

    /**
     * MR Client Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public MRClient(@Qualifier("webServiceTemplateV5") WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public PingURLResponse pingURL(final MultispeakVendor mspVendor, String uri)
            throws MultispeakWebServiceClientException {
        try {
            PingURL pingURL = objectFactory.createPingURL();
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            return (PingURLResponse) webServiceTemplate.marshalSendAndReceive(uri, pingURL,
                customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
    }

    @Override
    public List<String> getMethods(final MultispeakVendor mspVendor, String uri)
            throws MultispeakWebServiceClientException {
        List<String> methodList = new ArrayList<>();
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            GetMethods getMethods = objectFactory.createGetMethods();

            GetMethodsResponse response =
                (GetMethodsResponse) webServiceTemplate.marshalSendAndReceive(uri, getMethods,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor));
            if (response != null) {
                ArrayOfString arrayOfString = response.getArrayOfString();
                if (arrayOfString != null) {
                    methodList = arrayOfString.getTheString();
                }
            }
        } catch (WebServiceException | XmlMappingException e) {
            throw new MultispeakWebServiceClientException(e.getMessage());
        }
        return methodList;
    }
}
