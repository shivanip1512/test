package com.cannontech.multispeak.client.core.v5;
import java.util.UUID;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Node;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.multispeak.client.Credential;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;

public class CustomWebServiceMsgCallback {
    private final static Logger log = YukonLogManager.getLogger(CustomWebServiceMsgCallback.class);

    @Autowired public MultispeakFuncs multispeakFuncs;

    public WebServiceMessageCallback addRequestHeader(final MultispeakVendor mspVendor, String interfaceName) {
        return new WebServiceMessageCallback() {
            @Override
            public void doWithMessage(WebServiceMessage message) {

                // Update Header with Soap Action
                SaajSoapMessage saajSoapRequestMessage = (SaajSoapMessage) message;
                Node nxtNode = null;
                SOAPEnvelope env = null;
                try {
                    env = saajSoapRequestMessage.getSaajMessage().getSOAPPart().getEnvelope();
                    nxtNode = env.getBody().getFirstChild();
                    String soapAction = nxtNode.getNamespaceURI() + "/" + nxtNode.getLocalName();

                    MimeHeaders mimeHeaders = saajSoapRequestMessage.getSaajMessage().getMimeHeaders();
                    mimeHeaders.setHeader("SOAPAction", soapAction);

                    env.addNamespaceDeclaration("req", "http://www.multispeak.org/V5.0/ws/request");
                    env.addNamespaceDeclaration("com", "http://www.multispeak.org/V5.0/commonTypes");
                    env.addNamespaceDeclaration(nxtNode.getPrefix(), nxtNode.getNamespaceURI());
                    env.addNamespaceDeclaration("v5", "http://www.multispeak.org/V5.0");
                    env.addNamespaceDeclaration("com1", "http://www.multispeak.org/V5.0/commonArrays");

                } catch (SOAPException e) {
                    log.warn("Unable to set SOAPAction in the Header");
                }

                try {
                    SOAPHeader header = env.getHeader();
                    SOAPElement headElement = header.addChildElement("MultiSpeakRequestMsgHeader", "req");
                    headElement.setAttribute("MessageID", UUID.randomUUID().toString().replace("-", ""));
                    
                    headElement.setAttribute("TimeStamp", Iso8601DateUtil.formatIso8601Date(Instant.now().toDate(), true));

                    Credential credential = multispeakFuncs.getOutgoingCredential(mspVendor, interfaceName);

                    multispeakFuncs.getHeader(headElement, "req", credential.getUserName(), credential.getPassword());

                } catch (SOAPException e) {
                    log.warn("caught exception in addRequestHeader", e);
                }
            }
        };
    }

}
