package com.cannontech.multispeak.client.core.v5;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.cannontech.msp.beans.v5.commonarrays.ArrayOfString;
import com.cannontech.msp.beans.v5.mdm_server.GetMethods;
import com.cannontech.msp.beans.v5.mdm_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.mdm_server.ObjectFactory;
import com.cannontech.msp.beans.v5.mdm_server.PingURL;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class MDMClient implements IMDMClient {
    private WebServiceTemplate webServiceTemplate;
    @Autowired private CustomWebServiceMsgCallback customWebServiceMsgCallback;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;

    /**
     * MDMClient Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public MDMClient(@Qualifier("webServiceTemplateV5") WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public void pingURL(MultispeakVendor mspVendor, String uri) throws MultispeakWebServiceClientException {
        try {
            PingURL pingURL = objectFactory.createPingURL();
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);
            webServiceTemplate.marshalSendAndReceive(uri, pingURL,
                customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.MDM_Server_STR));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }

    }

    @Override
    public List<String> getMethods(MultispeakVendor mspVendor, String uri) throws MultispeakWebServiceClientException {
        List<String> methodList = new ArrayList<>();
        try {
            GetMethods getMethods = objectFactory.createGetMethods();
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            GetMethodsResponse response =
                (GetMethodsResponse) webServiceTemplate.marshalSendAndReceive(uri, getMethods,
                    customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.MDM_Server_STR));

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
