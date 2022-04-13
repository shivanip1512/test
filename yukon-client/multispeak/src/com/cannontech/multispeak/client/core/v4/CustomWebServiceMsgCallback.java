package com.cannontech.multispeak.client.core.v4;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Node;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.multispeak.client.Credentials;

import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;

public class CustomWebServiceMsgCallback {
    private final static Logger log = YukonLogManager.getLogger(CustomWebServiceMsgCallback.class);

    @Autowired public MultispeakFuncs multispeakFuncs;
    public WebServiceMessageCallback addRequestHeader(final MultispeakVendor mspVendor, String interfaceName) {
        return new WebServiceMessageCallback() {
            @Override
            public void doWithMessage(WebServiceMessage message) {
                SoapMessage soapMessage = (SoapMessage) message;
                // Update Header with Soap Action
                SaajSoapMessage saajSoapRequestMessage = (SaajSoapMessage) message;
                Node nxtNode = null;
                try {
                    nxtNode =
                        saajSoapRequestMessage.getSaajMessage().getSOAPPart().getEnvelope().getBody().getFirstChild();
                    String soapAction = nxtNode.getNamespaceURI() + "/" + nxtNode.getLocalName();
                    MimeHeaders mimeHeaders = saajSoapRequestMessage.getSaajMessage().getMimeHeaders();
                    mimeHeaders.setHeader("SOAPAction", soapAction);
                } catch (SOAPException e) {
                    log.warn("Unable to set SOAPAction in the Header");
                }

                SoapHeader header = soapMessage.getSoapHeader();
                Credentials credentials = multispeakFuncs.getOutgoingCredentials(mspVendor, interfaceName);

                try {   
                    multispeakFuncs.getHeader(header, credentials.getUserName(), credentials.getPassword());
                } catch (SOAPException e) {
                    log.warn("caught exception in addRequestHeader", e);
                }
            }
        };
    }

}
