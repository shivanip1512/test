package com.cannontech.multispeak.client.v4;

import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.logging.log4j.Logger;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.AbstractSoapMessage;
import org.springframework.ws.soap.SoapEnvelope;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Node;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.multispeak.client.MessageContextHolder;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakFuncsBase;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public class MultispeakFuncs extends MultispeakFuncsBase {
    private final static Logger log = YukonLogManager.getLogger(MultispeakFuncs.class);

    @Resource(name="domainMarshallerV4") Jaxb2Marshaller jaxb2Marshaller;
    
    @Override
    public MultiSpeakVersion version() {
        return MultiSpeakVersion.V4;
    }

    @Override
    public void loadResponseHeader() throws MultispeakWebServiceException {
        SoapEnvelope env;
        try {
            env = getResponseMessageSOAPEnvelope();
            SoapHeader header = env.getHeader();
            // the YukonMultispeakMsgHeader will be built with "dummy" values for userId and pwd fields. The
            // expectation is that getMultispeakVendorFromHeader will replace these values with the correct
            // values from the other vendor once it is loaded.
            getHeader(header, "unauthorized", "unauthorized");
        } catch (NotFoundException | SOAPException e) {
            throw new MultispeakWebServiceException(e.getMessage());
        }
        
    }

    public SoapHeaderElement getHeader(SoapHeader header, String outUserName, String outPassword) throws SOAPException {

        YukonMultispeakMsgHeader yukonMspMsgHeader =
            new YukonMultispeakMsgHeader(outUserName, outPassword, version().getVersion());
        QName qname = new QName(version().getNamespace(), "MultiSpeakMsgHeader");
        SoapHeaderElement headerElement = header.addHeaderElement(qname);
        headerElement.addAttribute(new QName("MajorVersion"), "4");
        headerElement.addAttribute(new QName("MinorVersion"), "1");
        headerElement.addAttribute(new QName("Build"), "6");
        headerElement.addAttribute(new QName("UserID"), yukonMspMsgHeader.getUserID());
        headerElement.addAttribute(new QName("Pwd"), yukonMspMsgHeader.getPwd());
        headerElement.addAttribute(new QName("AppName"), yukonMspMsgHeader.getAppName());
        headerElement.addAttribute(new QName("AppVersion"), yukonMspMsgHeader.getAppVersion());
        headerElement.addAttribute(new QName("Company"), yukonMspMsgHeader.getCompany());
        headerElement.addAttribute(new QName("CSUnits"), yukonMspMsgHeader.getCSUnits().value());
        return headerElement;
    }

    @Override
    public LiteYukonUser authenticateMsgHeader() throws MultispeakWebServiceException {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * Returns the Soap Envelope updated with the SOAPAction.
     * 
     * @return SoapEnvelope
     * @throws javax.xml.soap.SOAPExceptionn
     */
    private SoapEnvelope getResponseMessageSOAPEnvelope() throws SOAPException {

        MessageContext ctx = MessageContextHolder.getMessageContext();
        WebServiceMessage responseMessage = ctx.getResponse();
        AbstractSoapMessage abstractSoapMessage = (AbstractSoapMessage) responseMessage;
        SaajSoapMessage saajSoapMessage = (SaajSoapMessage) abstractSoapMessage;
        SOAPMessage soapMessage = saajSoapMessage.getSaajMessage();
        SoapEnvelope soapEnvelop = saajSoapMessage.getEnvelope();
        MimeHeaders mimeHeaders = soapMessage.getMimeHeaders();

        WebServiceMessage webServiceRequestMessage = ctx.getRequest();
        SaajSoapMessage saajSoapRequestMessage = (SaajSoapMessage) webServiceRequestMessage;
        Node nxtNode = saajSoapRequestMessage.getSaajMessage().getSOAPPart().getEnvelope().getBody().getFirstChild();
        if (nxtNode != null && nxtNode.getNamespaceURI() == null) {
            nxtNode = nxtNode.getNextSibling();
        }

        if (nxtNode != null) {
            String soapAction = nxtNode.getNamespaceURI() + "/" + nxtNode.getLocalName();
            mimeHeaders.setHeader("SOAPAction", soapAction);
        } else {
            log.warn("Namespace and method not identified. SOAPAction not set.");
        }

        return soapEnvelop;
    }


}
