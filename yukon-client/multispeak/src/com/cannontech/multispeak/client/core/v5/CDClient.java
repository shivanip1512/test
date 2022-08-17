package com.cannontech.multispeak.client.core.v5;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.cannontech.msp.beans.v5.cd_server.GetMethods;
import com.cannontech.msp.beans.v5.cd_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.cd_server.ObjectFactory;
import com.cannontech.msp.beans.v5.cd_server.PingURL;
import com.cannontech.msp.beans.v5.cd_server.PingURLResponse;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfString;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.client.core.v5.CustomWebServiceMsgCallback;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

/**
 * Spring SOAP Web service Client for making web service requests to CD_Server implemented methods
 * 
 */
public class CDClient implements ICDClient {

    private WebServiceTemplate webServiceTemplate;
    @Autowired private CustomWebServiceMsgCallback customWebServiceMsgCallback;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;

    /**
     * CD Client Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public CDClient(@Qualifier("webServiceTemplateV5") WebServiceTemplate webServiceTemplate) {

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
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
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
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
        return methodList;
    }

}
