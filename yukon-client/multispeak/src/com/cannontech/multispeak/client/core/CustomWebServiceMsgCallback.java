package com.cannontech.multispeak.client.core;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;

import com.cannontech.multispeak.client.MultispeakVendor;

public class CustomWebServiceMsgCallback {

    public WebServiceMessageCallback addRequestHeader(final MultispeakVendor mspVendor) {
        return new WebServiceMessageCallback() {
            @Override
            public void doWithMessage(WebServiceMessage message) {
                SoapMessage soapMessage = (SoapMessage) message;
                SoapHeader header = soapMessage.getSoapHeader();
                mspVendor.getHeader(header);
            }
        };
    }

}
