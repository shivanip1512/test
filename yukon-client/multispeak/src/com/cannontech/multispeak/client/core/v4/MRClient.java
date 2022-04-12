package com.cannontech.multispeak.client.core.v4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;

import com.cannontech.msp.beans.v4.GetReadingsByDate;
import com.cannontech.msp.beans.v4.GetReadingsByDateResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class MRClient implements IMRClient {
    private WebServiceTemplate webServiceTemplate;
    @Autowired private CustomWebServiceMsgCallback customWebServiceMsgCallback;
    @Autowired private MultispeakFuncs multispeakFuncs;
    /**
     * MR Client Constructor
     * 
     * @param webServiceTemplate
     */
    @Autowired
    public MRClient(@Qualifier("webServiceTemplate") WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public GetReadingsByDateResponse getReadingsByDate(MultispeakVendor mspVendor, String uri,
            GetReadingsByDate getReadingsByDate) throws MultispeakWebServiceClientException {
        try {
            multispeakFuncs.setMsgSender(webServiceTemplate, mspVendor);

            return (GetReadingsByDateResponse) webServiceTemplate.marshalSendAndReceive(uri, getReadingsByDate,
                customWebServiceMsgCallback.addRequestHeader(mspVendor, MultispeakDefines.MR_Server_STR));
        } catch (WebServiceException | XmlMappingException ex) {
            throw new MultispeakWebServiceClientException(ex.getMessage());
        }
    }

}
