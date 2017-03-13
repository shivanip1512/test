package com.cannontech.multispeak.client.core.v5;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.cannontech.msp.beans.v5.commonarrays.ArrayOfString;
import com.cannontech.msp.beans.v5.oa_server.GetMethods;
import com.cannontech.msp.beans.v5.oa_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.oa_server.ObjectFactory;
import com.cannontech.msp.beans.v5.oa_server.PingURL;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class OAClient implements IOAClient {
    private WebServiceTemplate webServiceTemplate;
    private HttpComponentsMessageSender messageSender;
    @Autowired private CustomWebServiceMsgCallback customWebServiceMsgCallback;
    @Autowired private ObjectFactory objectFactory;

    /**
     * OAClient Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public OAClient(@Qualifier("webServiceTemplateV5") WebServiceTemplate webServiceTemplate) {

        this.webServiceTemplate = webServiceTemplate;
        messageSender = (HttpComponentsMessageSender) webServiceTemplate.getMessageSenders()[0];
    }

    @Override
    public void pingURL(final MultispeakVendor mspVendor, String uri) throws MultispeakWebServiceClientException {
        try {

            PingURL pingURL = objectFactory.createPingURL();
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            webServiceTemplate.marshalSendAndReceive(uri, pingURL,
                customWebServiceMsgCallback.addRequestHeader(mspVendor));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

    @Override
    public List<String> getMethods(final MultispeakVendor mspVendor, String uri)
            throws MultispeakWebServiceClientException {
        List<String> methodList = new ArrayList<>();
        try {
            GetMethods getMethods = objectFactory.createGetMethods();
            messageSender.setConnectionTimeout(new Long(mspVendor.getRequestMessageTimeout()).intValue());

            GetMethodsResponse response =
                (GetMethodsResponse) webServiceTemplate.marshalSendAndReceive(uri, getMethods,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor));

            if (response != null) {
                ArrayOfString arrayOfString = response.getArrayOfString();
                if (arrayOfString != null) {
                    methodList = arrayOfString.getTheString();
                }
            }

        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
        return methodList;
    }

}
